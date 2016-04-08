package cn.eakay.commons.base.validation.aop;

import cn.eakay.commons.base.BaseParam;
import cn.eakay.commons.base.ResultDO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责处理validation 注解校验
 * 表达式可放到配管中心
 * 针对BaseParam子类的方法参数 去做切面
 * 可添加参数校验失败统计
 * Created by xugang on 16/4/7.
 */
@Slf4j
@Aspect
public class ValidationAspect {
    @Pointcut(value = "execution(public * cn.eakay..*.*(..))")
    public void anyMethod() {
    }

    @Around("anyMethod() && args(param1,param2)")
    public Object doParamValidate(ProceedingJoinPoint pjp, BaseParam param1, BaseParam param2) throws Throwable {
        List<BaseParam> params = new ArrayList<>();
        params.add(param1);
        params.add(param2);
        return _doParamValidate(pjp, params);
    }

    @Around("execution(public * cn.eakay.*..ao.impl..*(..)) && args(param)")
    public Object doParamValidate(ProceedingJoinPoint pjp, BaseParam param)
            throws Throwable {
        List<BaseParam> params = new ArrayList<>();
        params.add(param);
        return _doParamValidate(pjp, params);
    }

    private Object _doParamValidate(ProceedingJoinPoint pjp, List<BaseParam> params) throws Throwable {
        Object rst = null;
        String targetName = pjp.getTarget().getClass().getSimpleName();
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        Method method = methodSignature.getMethod();

        long start = System.currentTimeMillis();
        try {

            // 调用方法
            rst = pjp.proceed();
            // 如果返回值是ResultDO，判断调用结果
            if (rst instanceof ResultDO) {
                ResultDO r = (ResultDO) rst;
                //可以添加tracer log
                if (r.isFailure()) {
                    log.error(targetName + ":" + method.getName() + " call error:" + r.toString());
                }
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            long usedTime = System.currentTimeMillis() - start;
            log.info(targetName + ":" + method.getName() + ":" + usedTime + "ms");
        }
        return rst;
    }
}
