package cn.mj.community.service;

import cn.mj.community.pojo.DiscussPost;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class DiscussPostTest {
    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void test(){
        for (int i = 0; i < 30000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网求职暖春计划");
            post.setContent("今年的就业形势，确实不容乐观。过了个年，仿佛跳水一般，整个讨论区哀鸿遍野！19届真的没人要了吗？！18届被优化真的没有出路了吗？！大家的“哀嚎”与“悲惨遭遇”牵动了每日潜伏于讨论区的牛客小哥哥小姐姐们的心，于是牛客决定：是时候为大家做点什么了！为了帮助大家度过“寒冬”，牛客网特别联合60+家企业，开启互联网求职暖春计划，面向18届&19届，拯救0 offer！");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            discussPostService.addDiscussPost(post);
        }

    }

    @Test
    public void test2(){
        discussPostService.findDiscussPosts(0,0,11,1 );
        discussPostService.findDiscussPosts(0,0,11,1 );
        discussPostService.findDiscussPosts(0,0,11,1 );
        discussPostService.findDiscussPosts(0,0,11,0 );
    }
}
