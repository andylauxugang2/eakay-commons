package cn.eakay.commons.job.base.quartz;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Properties;
import java.util.Set;

/**
 * 自定义scanner
 */
public abstract class YacsClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	/**
	 * 十六进制数 40
	 */
	private static final String YACS_PRIVATE_PKG_NAME = "\0x28";

	/**
	 * 属性配置
	 */
	private Properties properties = new Properties();

	protected String getProperty(String key) {
		return properties.getProperty(key);
	}

	protected void addProperty(String key, String value) {
		properties.put(key, value);
	}

	protected Properties getProperties() {
		return properties;
	}

	public YacsClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		//useDefaultFilters true 扫描所有@Component,@Service
		super(registry);
	}

	/**
	 * 扫描包
	 * @return
	 */
	public final Set<BeanDefinitionHolder> doScan() {
		return doScan(YACS_PRIVATE_PKG_NAME);
	}

	/**
	 * Scan the class path for candidate components.
	 * @param basePackage the package to check for annotated classes
	 * @return a corresponding Set of autodetected bean definitions
	 */
	@Override
	public final Set<BeanDefinition> findCandidateComponents(String basePackage) {
		logger.info("findCandidateComponents basePackage:" + basePackage);
		if (YACS_PRIVATE_PKG_NAME.equals(basePackage)) {
			return generateCandidateComponents();
		}
		return super.findCandidateComponents(basePackage);
	}

	abstract protected Set<BeanDefinition> generateCandidateComponents();

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
