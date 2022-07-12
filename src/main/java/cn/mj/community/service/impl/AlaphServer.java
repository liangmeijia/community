package cn.mj.community.service.impl;

import cn.mj.community.dao.DiscussPostMapper;
import cn.mj.community.dao.UserMapper;
import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.pojo.User;
import cn.mj.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AlaphServer {

    private static final Logger logger = LoggerFactory.getLogger(AlaphServer.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        //insert new user
        User user = new User();
        user.setUsername("dyy");
        user.setSalt(CommunityUtil.getUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //insert new discussPost
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("hello");
        discussPost.setContent("I am a new person  ");
        discussPost.setCreateTime(new Date());
        discussPost.setUserId(user.getId());
        discussPostMapper.insertDiscussPost(discussPost);

        Integer.valueOf("abc");
        return "ok";
    }
    //让该方法在多线程的环境下,被异步调用
    @Async
    public void execute1(){
        //like a runnable impl
        logger.debug("hello execute1");
    }

    //@Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2(){
        //like a runnable impl
        logger.debug("hello execute2");
    }
}
