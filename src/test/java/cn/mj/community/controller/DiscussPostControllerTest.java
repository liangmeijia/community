package cn.mj.community.controller;
import cn.mj.community.service.impl.AlaphServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DiscussPostControllerTest {
    @Autowired
    private DiscussPostController discussPostController;

    @Test
    public void test(){
        discussPostController.addDiscussPost("111","1111");
    }
}
