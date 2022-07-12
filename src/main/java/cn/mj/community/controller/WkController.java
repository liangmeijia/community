package cn.mj.community.controller;

import cn.mj.community.event.EventProducer;
import cn.mj.community.pojo.Event;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WkController implements CommunityConst {

    private static final Logger logger = LoggerFactory.getLogger(WkController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkHtmlToImageStorage;

    @Autowired
    private EventProducer eventProducer;

    @Value("${qiniu.bucket.share.name}")
    private String bucketShareName;
    @Value("${qiniu.bucket.share.url}")
    private String bucketShareUrl;

    @RequestMapping(path = "/share",method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl){
        String filename = CommunityUtil.getUUID();
        String suffix = ".png";
        //1.
        Event event = new Event()
                .setTopic(KAFKA_TOPIC_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("filename",filename)
                .setData("suffix",suffix);
        eventProducer.fireEvent(event);
        //2.
        Map<String, Object> map = new HashMap<>();
//        map.put("shareUrl",domain+contextPath+"/share/image/"+filename);
        map.put("shareUrl",bucketShareUrl+"/"+filename);
        return CommunityUtil.getJsonString(0,null,map);
    }

    //Deprecated
    @RequestMapping(path = "/share/image/{filename}",method = RequestMethod.GET)
    public void getShareImage(@PathVariable("filename")String filename, HttpServletResponse response){
        if(StringUtils.isBlank(filename)){
            logger.error("thr filename of share graph is null, filename is {}",filename);
            throw new IllegalArgumentException("thr filename of share graph is null");
        }
        //
        File file = new File(wkHtmlToImageStorage+filename+".png");
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int index = 0;
            while((index=(fis.read(b)))!=-1){
                outputStream.write(b,0,index);
            }
        } catch (IOException e) {
            logger.error("get share graph unsuccessfully, {}",e.getMessage());
            e.printStackTrace();
        }
    }
}
