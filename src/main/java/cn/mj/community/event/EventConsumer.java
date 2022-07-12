package cn.mj.community.event;

import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.pojo.Event;
import cn.mj.community.pojo.Message;
import cn.mj.community.service.DiscussPostService;
import cn.mj.community.service.ElasticsearchService;
import cn.mj.community.service.MessageService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

@Component
public class EventConsumer implements CommunityConst {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private DiscussPostService discussPostService;

    @Value("${wk.image.command}")
    private String wkHtmlToImageCommand;

    @Value("${wk.image.storage}")
    private String wkHtmlToImageStorage;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String bucketShareName;

    @Value("${qiniu.bucket.share.url}")
    private String bucketShareUrl;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;


    @KafkaListener(topics = {KAFKA_TOPIC_COMMENT,KAFKA_TOPIC_LIKE,KAFKA_TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(record ==null || record.value()!=null) {
            logger.error("event is null");
        }
        Event event = JSONObject.parseObject( record.value().toString(), Event.class);
        if(event == null){
            logger.error("event is not null, but it has a wrong form");
        }
        //produce message
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(event.getData()!=null){
            for(Map.Entry<String, Object> entry: event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        //add a system message
       messageService.sendMessage(message);
       logger.info("{} visit {} when {}",EventConsumer.class,"cn.mj.community.service.messageService.sendMessage", new Date());
    }

    @KafkaListener(topics = {KAFKA_TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(record ==null || record.value()!=null) {
            logger.error("event is null");
        }
        Event event = JSONObject.parseObject( record.value().toString(), Event.class);
        if(event == null){
            logger.error("event is not null, but it has a wrong form");
        }
        //
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.addDiscusPost(post);
        //
        logger.info("{} visit {} when {}",EventConsumer.class,"cn.mj.community.service.discussPostService.findDiscussPostById", new Date());
        logger.info("{} visit {} when {}",EventConsumer.class,"cn.mj.community.service.elasticsearchService.addDiscusPost", new Date());
    }

    @KafkaListener(topics = {KAFKA_TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record){
        if(record ==null || record.value()!=null) {
            logger.error("event is null");
        }
        Event event = JSONObject.parseObject( record.value().toString(), Event.class);
        if(event == null){
            logger.error("event is not null, but it has a wrong form");
        }
        //
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String filename = (String) event.getData().get("filename");
        String suffix = (String) event.getData().get("suffix");
        String cmd = wkHtmlToImageCommand+" --quality 75 "+htmlUrl+" "+wkHtmlToImageStorage+filename+suffix;

        try {
            Runtime.getRuntime().exec(cmd);
//            logger.info("create share graph　successfully");

        } catch (IOException e) {
            logger.error("create share graph　unsuccessfully, {}",e.getMessage());
        }

        // upload file to qiniu server
        UploadTask uploadTask = new UploadTask(filename,suffix);
        Future future = threadPoolTaskScheduler.scheduleAtFixedRate(uploadTask, 500);
        uploadTask.setFuture(future);
    }
    class UploadTask implements Runnable{

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;
        // 启动任务的返回值
        private Future future;
        // 开始时间
        private long startTime;
        // 上传次数
        private int uploadTimes;

        public void setFuture(Future future){
            this.future = future;
        }

        public UploadTask(String fileName, String suffix){
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }
        @Override
        public void run() {
            //1.
            if(System.currentTimeMillis()-startTime >= 30000){
                logger.error("create share graph unsuccessfully (timeout), {}",fileName);
                future.cancel(true);
                return;
            }
            //2.
            if(uploadTimes>=3){
                logger.error("upload share graph to qiniu server unsuccessfully, uploadCount >= 3");
                future.cancel(true);
                return;
            }
            //3.
            String path = wkHtmlToImageStorage+fileName+suffix;
            File file = new File(path);
            if(file.exists()){
                //
                logger.info("start to upload share graph {} to qiniu server ,total {} count",fileName,++uploadTimes);
                // 设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJsonString(0));
                // 生成上传凭证
                Auth auth = Auth.create(accessKey,secretKey);
                String token = auth.uploadToken(bucketShareName, fileName, 3600, policy);
                // 指定上传机房
                UploadManager manager = new UploadManager(new Configuration(Zone.zone2()));
                try {
                    Response response = manager.put(path, fileName, token, null, "image/" + suffix, false);
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if(json == null || json.get("code")==null || !json.get("code").toString().equals("0")){
                        logger.error("upload share graph {} to qiniu server unsuccessfully, uploadCount is {}",fileName,uploadTimes);
                    }else{
                        //successfully
                        logger.error("upload share graph {} to qiniu server successfully, uploadCount is {}",fileName,uploadTimes);
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    logger.error("upload share graph {} to qiniu server unsuccessfully, uploadCount is {}",fileName,uploadTimes);
                    e.printStackTrace();
                }
            }else {
                logger.info("wait to create share graph {}...",fileName);
            }
        }
    }
}
