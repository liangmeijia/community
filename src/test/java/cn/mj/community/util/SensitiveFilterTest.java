package cn.mj.community.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@Slf4j
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test(){
        String text = "#赌#博#吸#毒屡教不改的法吸#毒律规定";
        String s = sensitiveFilter.sensitiveWordsFilter(text);
        log.debug("{}",s);
    }

}
