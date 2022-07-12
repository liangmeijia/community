package cn.mj.community.quartz;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
public class QuartzTest {
    @Autowired
    private Scheduler scheduler;

    @Test
    public void testDeleteJob() throws SchedulerException {
        scheduler.deleteJob(new JobKey("AlphaJob","AlphaJobGroup"));
    }
}
