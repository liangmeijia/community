package cn.mj.community.dao;

import cn.mj.community.pojo.LoginTicket;
import cn.mj.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
public class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void test(){
//        LoginTicket loginTicket = new LoginTicket();
//        loginTicket.setTicket(CommunityUtil.getUUID());
//        loginTicket.setStatus(0);
//        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*30));
        LoginTicket loginTicket1 = loginTicketMapper.selectLoginTicketByTicket("014bb9337f5c479f880d9074ed3b72e8");
        log.debug("{}",loginTicket1);
//        loginTicketMapper.insert(loginTicket);
//        loginTicketMapper.updateStatus(0,1);
    }
}
