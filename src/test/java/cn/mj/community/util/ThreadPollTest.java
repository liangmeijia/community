package cn.mj.community.util;
import cn.mj.community.service.impl.AlaphServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class ThreadPollTest {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPollTest.class);

    //jdk normal thread pool
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    //jdk schedule thread pool
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //spring normal thread pool
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    //spring schedule thread pool
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private AlaphServer alaphServer;

    public void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //test jdk normal thread pool
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    //test jdk schedule thread pool
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ScheduledExecutorService");
            }
        };

       scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);

       sleep(20000);
    }

    //test spring normal thread pool
    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.submit(task);
        }
        sleep(10000);
    }

    //test spring schedule thread pool
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskScheduler");
            }
        };

        threadPoolTaskScheduler.scheduleAtFixedRate(task,new Date(System.currentTimeMillis()+10000),1000);
        sleep(20000);
    }

    //test spring normal thread pool (simple version)
    @Test
    public void testExecutorServiceSimple(){
        for (int i = 0; i < 10; i++) {
            alaphServer.execute1();
        }
        sleep(10000);
    }

    //test spring schedule thread pool (simple version)
    @Test
    public void testExecutorServiceSchedulerSimple(){
        sleep(20000);
    }
}
