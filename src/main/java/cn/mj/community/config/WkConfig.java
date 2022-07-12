package cn.mj.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WkConfig {

    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkHtmlToImageStorage;

    @PostConstruct
    public void init(){
        File file = new File(wkHtmlToImageStorage);
        if(!file.exists()){
            file.mkdir();
            logger.info("create wkHtmlToImageStorage successfully");
        }
    }
}
