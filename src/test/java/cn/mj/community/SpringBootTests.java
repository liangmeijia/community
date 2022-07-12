package cn.mj.community;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

@SpringBootTest
public class SpringBootTests {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootTests.class);

    @BeforeTestClass
    public static void beforeClass(){
        logger.debug("beforeClass");
    }

    @AfterTestClass
    public static void afterClass(){
        logger.debug("afterClass");
    }

    @BeforeTestMethod
    public void before(){
        logger.debug("before");
    }

    @AfterTestMethod
    public void after(){
        logger.debug("after");
    }

    @Test
    public void test1(){
        logger.debug("----------------test1----------------");
    }
}


