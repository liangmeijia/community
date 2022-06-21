package cn.mj.community.service;

import cn.mj.community.service.impl.AlaphServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;

@SpringBootTest
public class TransactionTest {
    @Autowired
    private AlaphServer alaphServer;
    @Test
    public void save1(){
        alaphServer.save1();
    }

    @Test
    public void test(){
        System.out.println(this.getClass().getResource("/"));
        //file:home/lmj/java/ideaProject/community_project/community/target/test-classes/

        System.out.println(this.getClass().getResource(""));
        //file:home/lmj/java/ideaProject/community_project/community/target/test-classes/cn/mj/community/service/

        System.out.println(this.getClass().getResource("").getPath());
        //home/lmj/java/ideaProject/community_project/community/target/test-classes/cn/mj/community/service/
    }
}
