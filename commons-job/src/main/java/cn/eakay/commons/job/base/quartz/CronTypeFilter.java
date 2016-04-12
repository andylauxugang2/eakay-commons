package cn.eakay.commons.job.base.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用ReflectionUtils metadataReader 过滤Cron注解的方法
 * @author xugang
 * @see ClassPathScanningCandidateComponentProvider#isCandidateComponent(AnnotatedBeanDefinition beanDefinition)
 * isCandidateComponent方法
 * @since 2016/4/12
 */
@Slf4j
public class CronTypeFilter implements TypeFilter {

    private static final String CRON_CLZ = Cron.class.getName();

    /**
     * @see ClassPathScanningCandidateComponentProvider#findCandidateComponents(String basePackage)
     * 会调isCandidateComponent
     * match return true 后 add 到 Set<BeanDefinition> candidates
     *
     * @param metadataReader
     * @param metadataReaderFactory
     * @return
     * @throws IOException
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

        log.info("CronTypeFilter match......metadataReader.getResource={}", metadataReader.getResource());

        //org.springframework.core.type.classreading.SimpleMetadataReader
        Field field = ReflectionUtils.findField(metadataReader.getClass(), "annotationMetadata");
        ReflectionUtils.makeAccessible(field);
        AnnotationMetadata annotationMetadata = (AnnotationMetadata) ReflectionUtils.getField(field, metadataReader);

        final AtomicBoolean hasCron = new AtomicBoolean(Boolean.FALSE);

		/*for (MethodMetadata ann : annotationMetadata.getAnnotatedMethods(CRON_CLZ)) {
            log.info(ann.getMethodName());
		}*/
        //过滤Cron注解的方法
        if (!CollectionUtils.isEmpty(annotationMetadata.getAnnotatedMethods(CRON_CLZ))) {
            hasCron.set(Boolean.TRUE);
        }

        log.info("CronTypeFilter match result={}", hasCron.get());
        return hasCron.get();
    }

}
