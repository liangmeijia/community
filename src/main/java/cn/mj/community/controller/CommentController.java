package cn.mj.community.controller;

import cn.mj.community.event.EventProducer;
import cn.mj.community.pojo.Comment;
import cn.mj.community.pojo.Event;
import cn.mj.community.service.CommentService;
import cn.mj.community.service.DiscussPostService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConst {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{PostId}",method = RequestMethod.POST)
    public String addComments(@PathVariable("PostId") int postId, Comment comment){
        //1.
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        //2.
        commentService.addComment(comment);
        //3.send system　comment message
        Event event = new Event()
                .setTopic(KAFKA_TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",postId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            event.setEntityUserId(discussPostService.findDiscussPostById(comment.getEntityId()).getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            event.setEntityUserId(commentService.findCommentById(comment.getEntityId()).getUserId());
        }
        eventProducer.fireEvent(event);

        //the comment for post (update the numbers of post comment in ES)
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            event = new Event()
                    .setTopic(KAFKA_TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(postId);
            eventProducer.fireEvent(event);

            //add postId into redis
            String redisKey = CommunityUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return "redirect:/discussPost/detail/"+postId;

    }
}
