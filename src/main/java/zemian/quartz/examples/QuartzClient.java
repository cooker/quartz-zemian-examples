package zemian.quartz.examples;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zemian on 7/3/17.
 */
public class QuartzClient {
    private static Logger LOG = LoggerFactory.getLogger(QuartzClient.class);
    public static final String DEFAULT_CONFIG = "zemian/quartz/examples/quartz.properties";
    public static void main(String[] args) throws Exception {
        String config = System.getProperty("quartzConfig",DEFAULT_CONFIG);
        Scheduler scheduler = new StdSchedulerFactory(config).getScheduler();
        try {
            JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("HelloJob").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("HourlyTrigger", "HelloJob")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                    .build();
            if (!scheduler.checkExists(trigger.getKey())) {
                scheduler.scheduleJob(job, trigger);
            }
            LOG.info("Created {} with {}", job.getKey(), trigger.getKey());

            // Add another trigger with same existing job!
            trigger = TriggerBuilder.newTrigger().withIdentity("Every5SecsTrigger", "HelloJob")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                    .forJob("HelloJob")
                    .build();
            if (!scheduler.checkExists(trigger.getKey())) {
                scheduler.scheduleJob(trigger);
            }
            LOG.info("Created {} with {}", job.getKey(), trigger.getKey());

        } finally {
            scheduler.shutdown();
        }
    }
}
