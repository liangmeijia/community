package cn.mj.community.config;

import cn.mj.community.controller.interceptor.DataInterceptor;
import cn.mj.community.controller.interceptor.LoginInterceptor;
import cn.mj.community.controller.interceptor.LoginRequiredInterceptor;
import cn.mj.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;
//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.jpeg","/**/*.png");

//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.jpeg","/**/*.png");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.jpeg","/**/*.png");

        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.jpeg","/**/*.png");

    }
}
