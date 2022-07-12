package cn.mj.community.dao;

import cn.mj.community.pojo.DiscussPost;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
@Slf4j
public class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void test(){
        int rows = discussPostMapper.selectDiscussPostRows();
        log.debug("total rows {}",rows);//

        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 1, 10,0);
        for(DiscussPost discussPost:discussPosts){
            log.debug("discussPost {}",discussPost.toString());
        }
    }
    @Test
    public void test1(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setCreateTime(new Date());
        discussPost.setTitle("hhh");
        discussPost.setContent("hhhhhhhhhhhhh");
        discussPostMapper.insertDiscussPost(discussPost);
    }
}
