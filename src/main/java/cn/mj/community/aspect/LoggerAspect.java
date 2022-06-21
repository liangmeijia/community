package cn.mj.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class LoggerAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Pointcut(value = "execution(* cn.mj.community.service.impl.*.*(..))")
    public void pointCut(){

    }

    @Before(value = "pointCut()")
    public void before(JoinPoint joinPoint){
        //[1,1,1,1]
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            //visit service no by controller, like eventConsumer.java
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String host = request.getRemoteHost();
        String date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        logger.info("{} visit {} when {}",host,target,date);
    }

//    @After(value = "pointCut()")
//    public void after(){
//        System.out.println("---after---");
//    }
//
//    @AfterReturning("pointCut()")
//    public void afterReturning(){
//        System.out.println("----afterReturning---");
//    }
//
//    @AfterThrowing("pointCut()")
//    public void afterThrowing(){
//        System.out.println("afterThrowing");
//    }
//
//    @Around("pointCut()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("---around before---");
//        Object re = joinPoint.proceed();
//        System.out.println("---around after---");
//        return re;
//    }
}
