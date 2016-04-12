package cn.eakay.commons.job.base.quartz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * @Cron 配置任务表达式 @link http://www.quartz-scheduler.org/documentation
 *
 *      字段 允许值 允许的特殊字符
 *      秒   0-59 , - * /
 *      分   0-59 , - * /
 *      小时 0-23 , - * /
 *       日期 1-31 , - * ? / L W C
 *      月份 1-12 或者 JAN-DEC , - * /
 *      星期 1-7 或者 SUN-SAT , - * ? / L C #
 *      年（可选） 留空, 1970-2099 , - * /
 *
 * 下面配置的是每分钟执行一次任务代码
 * “/”字符用来指定数值的增量 表示从第*分钟开始，每1分钟
 * @author xugang
 */
public @interface Cron {
	// 0 */1 * * * ?"
	String value(); //
	String desc();
}