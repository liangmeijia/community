package cn.mj.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.image.BufferedImage;
import java.util.Properties;

@Configuration
public class KaptChaConfig {

    @Bean
    public Producer defaultKaptCha(){
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
//        properties.setProperty("kaptcha.border", "yes");
//        properties.setProperty("kaptcha.border.color", "105,179,90");
//        properties.setProperty("kaptcha.textproducer.font.color", "blue");
//        properties.setProperty("kaptcha.image.width", "110");
//        properties.setProperty("kaptcha.image.height", "40");
//        properties.setProperty("kaptcha.textproducer.font.size", "30");
//        properties.setProperty("kaptcha.session.key", "code");
//        properties.setProperty("kaptcha.textproducer.char.length", "4");
//        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
