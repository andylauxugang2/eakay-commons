package cn.eakay.commons.job.base.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @see ClassPathScanningCandidateComponentProvider#isCandidateComponent(AnnotatedBeanDefinition beanDefinition)
 * isCandidateComponent方法
 */
public class CronTypeFilter implements TypeFilter {
	public static Logger logger = LoggerFactory.getLogger(CronTypeFilter.class);

	private static final String CRON_CLZ = Cron.class.getName();

	@Override
	public boolean match(MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory) throws IOException {

		logger.info("CronTypeFilter match......metadataReader.getResource={}", metadataReader.getResource());

		//org.springframework.core.type.classreading.SimpleMetadataReader
		Field field = ReflectionUtils.findField(metadataReader.getClass(), "annotationMetadata");
		ReflectionUtils.makeAccessible(field);
		AnnotationMetadata annotationMetadata = (AnnotationMetadata) ReflectionUtils.getField(field, metadataReader);

		final AtomicBoolean hasCron = new AtomicBoolean(Boolean.FALSE);

		/*for (MethodMetadata ann : annotationMetadata.getAnnotatedMethods(CRON_CLZ)) {
			logger.info("-------" + ann.getMethodName());
		}*/
		if(!CollectionUtils.isEmpty(annotationMetadata.getAnnotatedMethods(CRON_CLZ))){
			hasCron.set(Boolean.TRUE);
		}

		logger.info("CronTypeFilter match result={}", hasCron.get());
		return hasCron.get();
	}

}
