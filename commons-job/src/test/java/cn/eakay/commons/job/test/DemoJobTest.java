package cn.eakay.commons.job.test;

import cn.eakay.commons.job.DemoJob;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Iterator;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 测试 job demo
 *
 * @author xugang
 * @since 16/4/12.
 */
public class DemoJobTest extends TestBase {

    private Scheduler scheduler;

    @Before
    public void setUp() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            // and start it
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJob() {
        JobDetail job = newJob(DemoJob.class)
                .withIdentity("demoJob", "group1")
                .build();

        try {
            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(10)
                            .withRepeatCount(1))
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void 第一个quartzjob测试_test() {

        Map<String, DemoJob> demoJobBeans = ctx.getBeansOfType(DemoJob.class, false, false);
        String beanKey = demoJobBeans.keySet().iterator().next();
        DemoJob job = demoJobBeans.get(beanKey);
        Assert.assertNotNull(job);


    }

}