package cn.eakay.commons.base.performance.aop;

import cn.eakay.commons.base.performance.annotation.Performance;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

/**
 * 性能logback aop
 * @author xugang
 */
@Slf4j
@Aspect
public class PerformanceAspect {

    @Pointcut(value = "execution(* cn.eakay..*.*(..))")
    public void anyMethod() {
    }

    @Around("anyMethod() && @annotation(performance)")
    public Object logElapsed(ProceedingJoinPoint pjp, Performance performance) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            //调用方法
            return pjp.proceed();
        } finally {
            if (PerfUtil.printLog) {
                try {
                    long elapsed = System.currentTimeMillis() - start;
                    Signature signature = pjp.getSignature();
                    String desc = performance.desc();
                    if (signature != null) {
                        String clazzName = signature.getDeclaringTypeName();
                        String clazzSimpleName = stripPackageName(clazzName);
                        String method = signature.getName();
                        String param = performance.showArgs() ? methodParams(pjp, signature) : "";

                        desc = String.format("%s.%s(%s) %s", clazzSimpleName, method, param, desc);
                    }
                    PerformanceLogUtils.log(performance.extent(), desc, elapsed);
                } catch (Exception e) {
                    log.error("error when log performance", e);
                }
            }
        }
    }

    private String methodParams(ProceedingJoinPoint pjp, Signature signature) {
        StringBuilder sb = new StringBuilder();
        try {
            CodeSignature cs = (CodeSignature) signature;
            String[] paramNames = cs.getParameterNames();
            Object[] paramValues = pjp.getArgs();

            for (int i = 0; i < paramNames.length; i++) {
                sb.append(paramNames[i]).append("=").append(PerformanceLogUtils.toStr(paramValues[i]));
                if (i < paramNames.length - 1) {
                    sb.append(", ");
                }
            }
        } catch (Exception e) {
            log.error("error when generate method param info", e);
        }
        return sb.toString();
    }

    private String stripPackageName(String name) {
        if (name == null)
            return "UNKNOWN_CLASS";
        int dot = name.lastIndexOf('.');
        if (dot == -1)
            return name;
        return name.substring(dot + 1);
    }
}
