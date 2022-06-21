package cn.mj.community.controller.advice;


import cn.mj.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler({Exception.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws Exception{
        logger.error("{}",e.getMessage());
        for(StackTraceElement element:e.getStackTrace()){
            logger.error("{}",element);
        }
        String requestWay = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(requestWay)){
            //json
            response.setContentType("application/plain;utf-8");
            PrintWriter writer =  response.getWriter();
            writer.write(CommunityUtil.getJsonString(1,"server is error"));
        }else {
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
