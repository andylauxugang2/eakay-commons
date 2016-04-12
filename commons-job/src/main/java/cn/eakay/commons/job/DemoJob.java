package cn.eakay.commons.job;

import cn.eakay.commons.job.base.quartz.Cron;
import lombok.extern.slf4j.Slf4j;

/**
 * demo job 示例
 *  @see BaseJob
 *
 * @author xugang
 */
@Slf4j
public class DemoJob extends BaseJob {

    /*@Autowired
    protected MockService mockService;*/

    /**
     * @Cron 配置任务表达式
     * 配置Cron注解的方法会被注册到quartz触发器被调度 commons-job-ext:scanner 时完成注册
     *
     * @throws Exception
     */
    @Override
    @Cron(value = "0 */1 * * * ?", desc = "定时调用mockService")
    protected void work() throws Exception {
        log.info("start DemoJob");
//        mockService.mock();
        log.info("end DriverComplainPunishJob");
    }

}