package cn.mj.community.controller;

import cn.mj.community.event.EventProducer;
import cn.mj.community.pojo.Event;
import cn.mj.community.pojo.Page;
import cn.mj.community.pojo.User;
import cn.mj.community.service.FollowService;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConst {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(entityType, entityId,user.getId());

        //send system follow message
        if(followService.followStatus(entityType,entityId,user.getId())){
            Event event = new Event()
                    .setTopic(KAFKA_TOPIC_FOLLOW)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJsonString(0);
    }

    @RequestMapping(path = "/findFollowees/{userId}", method = RequestMethod.GET)
    public String findFollowees(@PathVariable("userId")int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("the user is not exist");
        }
        model.addAttribute("user",user);
        //set page
        page.setLimit(5);
        page.setPath("/findFollowees/"+userId);
        page.setRows((int) followService.followeeCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> followees = followService.findFollowees(userId, ENTITY_TYPE_USER, page.getOffset(), page.getLimit());

        if(followees!=null){
            for(Map<String, Object> followee:followees){
                User u = (User) followee.get("user");
                followee.put("followStatus",hostHolder.getUser()!=null?
                        followService.followStatus(ENTITY_TYPE_USER,u.getId(),hostHolder.getUser().getId()):false);
            }
        }
        model.addAttribute("followees",followees);


        return "/site/followee";
    }

    @RequestMapping(path = "/findFollowers/{userId}", method = RequestMethod.GET)
    public String findFollowers(@PathVariable("userId")int userId, Model model,Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("the user is not exist");
        }
        model.addAttribute("user",user);
        //set page
        page.setLimit(5);
        page.setPath("/findFollowers/"+userId);
        page.setRows((int) followService.followerCount(ENTITY_TYPE_USER,userId));

        List<Map<String, Object>> followers = followService.findFollowers(ENTITY_TYPE_USER, userId, page.getOffset(), page.getLimit());

        if(followers!=null){
            for(Map<String, Object> follower:followers){
                User u = (User) follower.get("user");
                follower.put("followStatus",hostHolder.getUser()!=null?
                        followService.followStatus(ENTITY_TYPE_USER,u.getId(),hostHolder.getUser().getId()):false);
            }
        }
        model.addAttribute("followers",followers);
        return "/site/follower";
    }

//    @RequestMapping(path = "/followStatus", method = RequestMethod.GET)
//    @ResponseBody
//    public boolean getFollowStatus(int entityType, int entityId){
//        boolean status = false;
//        User user = hostHolder.getUser();
//        if(user !=null){
//            status = followService.followStatus(entityType,entityId,user.getId());
//        }
//        return status;
//    }
}
