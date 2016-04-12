package cn.eakay.commons.job.base.quartz;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.util.Properties;
import java.util.Set;

/**
 * 自定义scanner
 * @see ClassPathBeanDefinitionScanner
 * @see ClassPathScanningCandidateComponentProvider
 *
 * @author xugang
 */
public abstract class CommonsJobClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	/**
	 * 十六进制数 40 commons-job 私有包名 降级 由实现类去做 Set<BeanDefinition>生成
	 */
	private static final String CB_PRIVATE_PKG_NAME = "\0x28";

	/**
	 * 属性配置
	 */
	private Properties properties = new Properties();

	/**
	 * 用于获取basePackage和autoStartUp属性值
	 * @param key
	 * @return
	 */
	protected String getProperty(String key) {
		return properties.getProperty(key);
	}

	protected void addProperty(String key, String value) {
		properties.put(key, value);
	}

	protected Properties getProperties() {
		return properties;
	}

	/**
	 * 调用父类构造
	 * 默认 useDefaultFilters true 扫描所有@Component,@Service...
	 * @param registry
	 */
	public CommonsJobClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	/**
	 * 扫描包
	 * 调用spring doScan 再回调 this.findCandidateComponents
	 * 该方法不能被继承改写
	 * @return
	 */
	public final Set<BeanDefinitionHolder> doScan() {
		return doScan(CB_PRIVATE_PKG_NAME);
	}

	/**
	 * 从doScan设置的basePackage传到本方法中 剩下的注册Set<BeanDefinition> 交给spring
	 *
	 * 从spring 贴来的注释:
	 * Scan the class path for candidate components.
	 * @param basePackage the package to check for annotated classes
	 * @return a corresponding Set of autodetected bean definitions
	 */
	@Override
	public final Set<BeanDefinition> findCandidateComponents(String basePackage) {
		logger.info("findCandidateComponents basePackage:" + basePackage);
		if (CB_PRIVATE_PKG_NAME.equals(basePackage)) {
			return generateCandidateComponents();
		}
		//保险起见 私有包名降级失败交给spring去生产Set<BeanDefinition>
		return super.findCandidateComponents(basePackage);
	}

	/**
	 * 必须由实现类去做 生产Set<BeanDefinition>
	 *     @since 2016/4/12
	 * @return Set<BeanDefinition>
	 */
	abstract protected Set<BeanDefinition> generateCandidateComponents();

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
