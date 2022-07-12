package cn.mj.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlphaJob implements Job{
    private static final Logger logger = LoggerFactory.getLogger(AlphaJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug(Thread.currentThread().getName()+" execute a job");
    }
}
