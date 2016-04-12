package cn.eakay.commons.job.base.quartz;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * spring解析xml的时不同的xml名称空间使用不同的解析类,扩展spring
 *
 * 创建CommonsJobExtNamespaceHandler时注册元素Name,Parser
 *
 * @see DefaultBeanDefinitionDocumentReader
 * @see BeanDefinitionParserDelegate
 * @see DefaultNamespaceHandlerResolver META-INF/spring.handlers
 *
 * @author xugang
 */
public class CommonsJobExtNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("scan", new CommonsJobExtComponentScanBeanDefinitionParser());
	}

}