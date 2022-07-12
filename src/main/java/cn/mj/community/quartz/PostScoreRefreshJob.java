package cn.mj.community.quartz;

import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.service.CommentService;
import cn.mj.community.service.DiscussPostService;
import cn.mj.community.service.ElasticsearchService;
import cn.mj.community.service.LikeService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundKeyOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job,CommunityConst {
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private static Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            logger.error("creat niuke epoch unsuccessfully");
            e.printStackTrace();
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //get postIds
        String redisKey = CommunityUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size()==0){
            logger.error("no post needs refresh score!");
            return;
            //throw new IllegalArgumentException("no post needs refresh score!");
        }

        logger.error("start to refresh post score!");
        while(operations.size()>0){
            Integer postId = (Integer) operations.pop();
            this.refresh(postId);
        }
        logger.error("complete refresh post score!");

    }

    private void refresh(Integer postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if(post==null){
            logger.error("the post has deleted, the postId is "+postId);
            return;
        }
        boolean wonderful = post.getStatus()==1;
        int commentCount = post.getCommentCount();
        double likeCount_entity = likeService.likeCount_entity(ENTITY_TYPE_POST, postId);
        double weight = (wonderful ? 75 : 0)+commentCount * 10+likeCount_entity*2;
        double score = Math.log10(Math.max(weight,1))+
                ((post.getCreateTime().getTime() - epoch.getTime())/(1000*60*60*24));

        //update post score
        discussPostService.updateDiscussPostScore(postId, score);
        //update post score in ES synchronously
        post.setScore(score);
        elasticsearchService.addDiscusPost(post);

    }
}
