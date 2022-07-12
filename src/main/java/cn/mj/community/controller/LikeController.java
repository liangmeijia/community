package cn.mj.community.controller;

import cn.mj.community.event.EventProducer;
import cn.mj.community.pojo.Event;
import cn.mj.community.pojo.User;
import cn.mj.community.service.LikeService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class LikeController implements CommunityConst {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int targetId, int postId){
        User user = hostHolder.getUser();
        likeService.like(entityType, entityId, user.getId(), targetId);

        long likeCount_entity = likeService.likeCount_entity(entityType, entityId);

        int likeStatus = likeService.likeStatus(entityType, entityId, user.getId());

        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount",String.valueOf(likeCount_entity));
        map.put("likeStatus",String.valueOf(likeStatus));

        //send system like message
        if(likeStatus == 1){
            // 1 -> like　，0 -> no like
            Event event = new Event()
                    .setTopic(KAFKA_TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(targetId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        //add postId into redis
        if(entityType == ENTITY_TYPE_POST){
            //if the type of like entity id "post"
            String redisKey = CommunityUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }


        return CommunityUtil.getJsonString(0,null, map);

    }



//    @RequestMapping(path = "/userCount", method = RequestMethod.GET)
//    @ResponseBody
//    public String getLikeCountForUser(int targetId){
//        long likeCount_user = likeService.likeCount_user(targetId);
//        return CommunityUtil.getJsonString(0, String.valueOf(likeCount_user));
//    }

}
