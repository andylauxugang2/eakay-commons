package cn.eakay.commons.base.performance.annotation;

import cn.eakay.commons.base.performance.common.Extent;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Performance {

    boolean showArgs() default true;

    String desc() default "";

    Extent extent() default Extent.Part;

}
