package cn.eakay.commons.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 基本Job抽象类 各业务线job quartz实现 需要继承本类
 *  @see DemoJob
 *
 * @author xugang
 */
@Slf4j
public abstract class BaseJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            work();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException(e);
        }
    }

    /**
     * 子job需要实现的
     *
     * @throws Exception
     */
    abstract protected void work() throws Exception;

    /**
     * 记录job运行时长
     *
     * @param start
     */
    protected void logRTBaseOnStart(Long start) {
        Long used = System.currentTimeMillis() - start;
        log.info(this.getClass().getSimpleName() + ":runtime used " + used + "(ms)");
    }
}
