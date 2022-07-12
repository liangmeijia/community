package cn.mj.community.wk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@Slf4j
public class WkTest {

    private static final Logger logger = LoggerFactory.getLogger(WkTest.class);

    @Test
    public void test(){
        String cmd = "wkhtmltoimage https://www.baidu.com /home/lmj/wk_img/1.png";
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("create image successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
