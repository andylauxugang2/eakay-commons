package cn.eakay.commons.job.test;

import cn.eakay.commons.job.DemoJob;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Iterator;
import java.util.Map;

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
            super.setUp();
//            scheduler = StdSchedulerFactory.getDefaultScheduler();
//            QuartzScannerConfigurer quartzScannerConfigurer = new QuartzScannerConfigurer();

            BeanDefinition target = new GenericBeanDefinition();
            target.setBeanClassName(SchedulerFactoryBean.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
//        LifecycleUtils.destroy(scheduler);
    }

    @Test
    public void testJob() {

        Map<String, DemoJob> demoJobBeans = ctx.getBeansOfType(DemoJob.class, false, false);
        String beanKey = demoJobBeans.keySet().iterator().next();
        DemoJob job = demoJobBeans.get(beanKey);
        Assert.assertNotNull(job);


    }

    @Test
    public void 第一个quartzjob测试_test() throws Exception {

    }

}