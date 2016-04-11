package cn.eakay.commons.job.base.quartz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * @author xugang
 */
public @interface Cron {
	// */1 * * * * ?"
	String value();
	String desc();
}