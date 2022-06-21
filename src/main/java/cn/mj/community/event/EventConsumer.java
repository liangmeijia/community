package cn.mj.community.event;

import cn.mj.community.pojo.Event;
import cn.mj.community.pojo.Message;
import cn.mj.community.service.MessageService;
import cn.mj.community.util.CommunityConst;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConst {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;

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

}
