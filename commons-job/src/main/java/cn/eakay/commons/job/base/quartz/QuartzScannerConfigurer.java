package cn.eakay.commons.job.base.quartz;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class QuartzScannerConfigurer extends YacsClassPathBeanDefinitionScanner {

    public static Logger log = LoggerFactory.getLogger("jobLogger");

    /**
     * SchedulerFactoryBean:
     * Spring启动的时候 创建一个SchedulerFactoryBean实例，根据配置的属性，创建一个schedule实例
     */
    private static final String CLZ_SCHEDULER_FACTORY_BEAN = SchedulerFactoryBean.class.getName();

    /**
     * 表达式触发器 包好 jobDetail,cronExpression
     */
    private static final String CLZ_CRON_TRIGGER_BEAN = CronTriggerBean.class.getName();

    /**
     * misfireInstruction类型
     * MISFIRE_INSTRUCTION_SMART_POLICY = 0
     * MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1
     * MISFIRE_INSTRUCTION_DO_NOTHING = 2
     */
//    private static final int TRIGGER_MISFIRE_INSTRUCTION_POLICY =  CronTriggerBean.MISFIRE_INSTRUCTION_DO_NOTHING;
    private static final int TRIGGER_MISFIRE_INSTRUCTION_POLICY = 2;

    /**
     * MethodInvokingJobDetailFactoryBean:
     * 实现对任务调度时的拦截
     */
    private static final String CLZ_METHOD_INVOKING_JOB_DETAIL_FACTORYBEAN = MethodInvokingJobDetailFactoryBean.class.getName();
    private Properties properties;


    public QuartzScannerConfigurer(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected void registerDefaultFilters() {
        resetFilters(false);
        addIncludeFilter(new CronTypeFilter());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Set<BeanDefinition> generateCandidateComponents() {
        String basePackage = StringUtils.defaultIfEmpty(getProperty("basePackage"), "**");
        String autoStartUp = StringUtils.defaultIfEmpty(getProperty("autoStartUp"), "false");
        ManagedList triggers = new ManagedList();

        Set<BeanDefinition> cronBeanDefinitions = super.findCandidateComponents(basePackage);
        log.info("findCandidateComponents start");
        for (BeanDefinition cron : cronBeanDefinitions) {
            try {
                Class<?> clz = Class.forName(cron.getBeanClassName());
                triggers.addAll(buildTriggersFromClass(clz));
                log.info("findCandidateComponents cron clz:" + clz.getName());
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException("Error creating Cron", e);
            }
        }

        BeanDefinition scheduler = beanOfClazz(CLZ_SCHEDULER_FACTORY_BEAN);
        log.info("init scheduler.............");
        scheduler.getPropertyValues().addPropertyValue("autoStartup", Boolean.valueOf(autoStartUp));
        scheduler.getPropertyValues().addPropertyValue("triggers", triggers);

        return Collections.singleton(scheduler);
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isConcrete();
    }

    /*
    /**
     * Determine whether the given class does not match any exclude filter
     * and does match at least one include filter.
     * @param metadataReader the ASM ClassReader for the class
     * @return whether the class qualifies as a candidate component
     *
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return isConditionMatch(metadataReader);
            }
        }
        return false;
    }*/

    private Set<BeanDefinition> buildTriggersFromClass(Class<?> clazz) {
        BeanDefinition _target = buildTarget(clazz);
        log.info("prepare trigger ............." + clazz.getName());
        String name = BeanDefinitionReaderUtils.generateBeanName(_target, getRegistry());
        getRegistry().registerBeanDefinition(name, _target);

        RuntimeBeanReference target = new RuntimeBeanReference(name);

        Set<BeanDefinition> triggers = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            Cron cron = method.getAnnotation(Cron.class);
            if (cron != null) {
                log.info("init trigger ............." + clazz.getName() + " method:" + method.getName());
                triggers.add(buidCronTrigger(buidJobDetail(target, method.getName()), cron.value(), clazz.getName(),
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

    private BeanDefinition buidJobDetail(Object target, String methodName) {
        BeanDefinition jobDetail = new GenericBeanDefinition();

        jobDetail.setBeanClassName(CLZ_METHOD_INVOKING_JOB_DETAIL_FACTORYBEAN);
        jobDetail.getPropertyValues().addPropertyValue("targetObject", target);
        jobDetail.getPropertyValues().addPropertyValue("targetMethod", methodName);
        return jobDetail;
    }

    private BeanDefinition buidCronTrigger(BeanDefinition jobDetail, String cronExpress, String className, String desc) {
        Assert.state(CLZ_METHOD_INVOKING_JOB_DETAIL_FACTORYBEAN.equals(jobDetail.getBeanClassName()));

        BeanDefinition trigger = new GenericBeanDefinition();
        trigger.setBeanClassName(CLZ_CRON_TRIGGER_BEAN);
        trigger.getPropertyValues().addPropertyValue("jobDetail", jobDetail);
        trigger.getPropertyValues().addPropertyValue("cronExpression", cronExpress);
        trigger.getPropertyValues().addPropertyValue("name", className);
        trigger.getPropertyValues().addPropertyValue("description", desc);
        trigger.getPropertyValues().addPropertyValue("misfireInstruction",TRIGGER_MISFIRE_INSTRUCTION_POLICY);
        return trigger;
    }

}
