package cn.eakay.commons.job;

import cn.eakay.commons.job.base.quartz.Cron;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * demo job 示例
 *
 * @author xugang
 * @see BaseJob
 */
@Slf4j
public class DemoJob extends BaseJob {

    /*@Autowired
    protected MockService mockService;*/

    /**
     * @throws Exception
     * @Cron 配置任务表达式
     * 配置Cron注解的方法会被注册到quartz触发器被调度 commons-job-extscanner 时完成注册
     */
    @Override
    @Cron(value = "*/10 * * * * ?", desc = "定时调用mockService")
    protected void work() throws Exception {
        log.info("start DemoJob");
//        mockService.mock();
        System.out.println("------------DemoJob#work-----------");
        System.out.println("------------"+System.currentTimeMillis() / 1000+"-----------");
        log.info("end DriverComplainPunishJob");
    }
}