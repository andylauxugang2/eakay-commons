package cn.eakay.commons.job.base.quartz;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CalendarIntervalTrigger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * 实例化commons-job-ext:scanner
 * <p/>
 * spring-quartz 核心代码 quartz整合到spring中都是Bean的注入形式 都是BeanDefinition 可参考spring-quartz.xml配置 去百度随意搜个都行
 * 扫描basepackage 创建jobDetail和CronTrigger
 *
 * @author xugang
 * @see CommonsJobClassPathBeanDefinitionScanner
 * @since 2016/4/12
 */
@Slf4j
public class QuartzScannerConfigurer extends CommonsJobClassPathBeanDefinitionScanner {

    /**
     * SchedulerFactoryBean:
     * Spring启动的时候 创建一个SchedulerFactoryBean实例，根据配置的属性，创建一个schedule实例
     */
    private static final String CLZ_SCHEDULER_FACTORY_BEAN = SchedulerFactoryBean.class.getName();

    /**
     * 表达式触发器 包好 jobDetail,cronExpression
     */
    private static final String CLZ_CRON_TRIGGER_BEAN = CronTriggerFactoryBean.class.getName();

    /**
     * misfireInstruction类型
     * 调度(scheduleJob) 恢复调度(resumeTrigger,resumeJob) 处理规则定义
     * MISFIRE_INSTRUCTION_SMART_POLICY = 0
     * MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1
     * MISFIRE_INSTRUCTION_DO_NOTHING = 2 不触发立即执行 下次CronTrigger匹配时刻按照cronExpression依次执行
     */
    private static final int TRIGGER_MISFIRE_INSTRUCTION_POLICY = CalendarIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;
//    private static final int TRIGGER_MISFIRE_INSTRUCTION_POLICY = 2;

    /**
     * 实现对任务调度时的拦截 使用非自定义 MethodInvokingJobDetailFactoryBean:executeInternal
     */
    private static final String CLZ_METHOD_INVOKING_JOB_DETAIL_FACTORYBEAN = MethodInvokingJobDetailFactoryBean.class.getName();

    private Properties properties;

    /**
     * 为容器创建类路径Bean定义扫描器，并指定是否使用默认的扫描过滤规则 (@Component、@Repository、@Service、@Controller)
     * <p/>
     * 最终调的是ClassPathScanningCandidateComponentProvider 构造
     *
     * @param registry
     */
    public QuartzScannerConfigurer(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * Reset the configured type filters:useDefaultFilters=false
     * 创建this实例时 构造执行本方法注册Filters
     *
     * @see ClassPathScanningCandidateComponentProvider
     * @see CronTypeFilter#match(MetadataReader, MetadataReaderFactory)
     */
    @Override
    protected void registerDefaultFilters() {
        //重置final includeFilters excludeFilters 不适用默认registerDefaultFilters
        resetFilters(false);
        //仅添加includeFilters
        addIncludeFilter(new CronTypeFilter());
    }

    /**
     * 根据 scanner 设置的属性来生产Set<BeanDefinition>
     * 如果basePackage降级失败(如包名错误) 此方法不会被执行 导致所有job创建和调度失败
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Set<BeanDefinition> generateCandidateComponents() {
        String basePackage = StringUtils.defaultIfEmpty(getProperty("basePackage"), "**");
        String autoStartUp = StringUtils.defaultIfEmpty(getProperty("autoStartUp"), "false");

        //spring ManagedList BeanMetadataElement triggers
        ManagedList triggers = new ManagedList();

        //执行注册的CronTypeFilter match findCandidateComponents方法还会调用本类重写的isCandidateComponent
        Set<BeanDefinition> cronBeanDefinitions = super.findCandidateComponents(basePackage);
        log.info("findCandidateComponents start");
        for (BeanDefinition cron : cronBeanDefinitions) {
            try {
                Class<?> clz = Class.forName(cron.getBeanClassName());
                //build Set<BeanDefinition> triggers
                triggers.addAll(buildTriggersFromClass(clz));
                log.info("findCandidateComponents cron clz:" + clz.getName());
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException("Error creating Cron", e);
            }
        }
        //创建scheduler
        BeanDefinition scheduler = beanOfClazz(CLZ_SCHEDULER_FACTORY_BEAN);
        log.info("init scheduler.............");
        scheduler.getPropertyValues().addPropertyValue("autoStartup", Boolean.valueOf(autoStartUp));
        scheduler.getPropertyValues().addPropertyValue("triggers", triggers);
        //SingletonSet scheduler
        return Collections.singleton(scheduler);
    }

    /**
     * 重写
     *
     * @param beanDefinition
     * @return
     * @see ClassPathScanningCandidateComponentProvider#isCandidateComponent(AnnotatedBeanDefinition beanDefinition)
     * Determine whether the given bean definition qualifies as candidate.
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        //是否是具体的类 而非abstract interface TODO 未对inner class 做处理
        return beanDefinition.getMetadata().isConcrete();
    }

    /**
     * buildCronTrigger buildJobDetail
     * @param clazz
     * @return
     */
    private Set<BeanDefinition> buildTriggersFromClass(Class<?> clazz) {
        BeanDefinition _target = buildTarget(clazz);
        log.info("prepare trigger ............." + clazz.getName());
        //注册match到的Cron方法注解的BeanDefinition
        String name = BeanDefinitionReaderUtils.generateBeanName(_target, getRegistry());
        getRegistry().registerBeanDefinition(name, _target);

        RuntimeBeanReference target = new RuntimeBeanReference(name);

        Set<BeanDefinition> triggers = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            Cron cron = method.getAnnotation(Cron.class);
            //CronTrigger包好jobDetail,cronExpression
            if (cron != null) {
                log.info("init trigger ............." + clazz.getName() + " method:" + method.getName());
                triggers.add(buildCronTrigger(buildJobDetail(target, method.getName()), cron.value(), clazz.getName(),
                        cron.desc()));
            }
        }
        return triggers;
    }

    private BeanDefinition buildTarget(Class<?> clazz) {
        return beanOfClazz(clazz.getName());
    }

    private BeanDefinition beanOfClazz(String clazzName) {
        BeanDefinition target = new GenericBeanDefinition();
        target.setBeanClassName(clazzName);
        return target;
    }

    private BeanDefinition buildJobDetail(Object target, String methodName) {
        BeanDefinition jobDetail = new GenericBeanDefinition();

        //任务调度时的拦截 采用默认即可 TODO 自定义 MethodInvokingJobDetailFactoryBean
        jobDetail.setBeanClassName(CLZ_METHOD_INVOKING_JOB_DETAIL_FACTORYBEAN);
        jobDetail.getPropertyValues().addPropertyValue("targetObject", target);
        jobDetail.getPropertyValues().addPropertyValue("targetMethod", methodName); //方法名就是job的execute
        return jobDetail;
    }

    private BeanDefinition buildCronTrigger(BeanDefinition jobDetail, String cronExpress, String className, String desc) {
        Assert.state(CLZ_METHOD_INVOKING_JOB_DETAIL_FACTORYBEAN.equals(jobDetail.getBeanClassName()));

        BeanDefinition trigger = new GenericBeanDefinition();
        trigger.setBeanClassName(CLZ_CRON_TRIGGER_BEAN);
        trigger.getPropertyValues().addPropertyValue("jobDetail", jobDetail);
        trigger.getPropertyValues().addPropertyValue("cronExpression", cronExpress);
        trigger.getPropertyValues().addPropertyValue("name", className);
        trigger.getPropertyValues().addPropertyValue("description", desc);
        trigger.getPropertyValues().addPropertyValue("misfireInstruction", TRIGGER_MISFIRE_INSTRUCTION_POLICY);
        return trigger;
    }

}
