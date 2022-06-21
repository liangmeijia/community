package cn.mj.community.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailClientTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void setMail(){
        mailClient.sendMail("lmj23333@sina.com","TEST","WELCOME.");
    }
    @Test
    public void setHtmlMail(){
        Context context=new Context();
        context.setVariable("username","sbbbb");
        String content = templateEngine.process("/mail/demo", context);
        mailClient.sendMail("lmj23333@sina.com","HTML",content);
    }
}
