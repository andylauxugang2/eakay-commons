package cn.eakay.commons.job.base.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * ApplicationContext#loadBeanDefinition时遇到commons-job-ext:scan元素会使用CommonsJobExtComponentScanBeanDefinitionParser进行解析
 *
 * @author xugang
 * @see ComponentScanBeanDefinitionParser 依赖
 * @see ClassPathBeanDefinitionScanner
 * <p/>
 * <p/>
 * NamespaceHandlerSupport 将scan元素+本Parser注册到BeanDefinitionParser
 */
@Slf4j
public class CommonsJobExtComponentScanBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * 通过自定义ClassPathBeanDefinitionScanner 加载basePackage beans
     * 我们使用自定义的加载设置bean的方式 将
     *
     * @param element
     * @param parserContext
     * @return BeanDefinition isSingleton() isLazyInit()
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        CommonsJobClassPathBeanDefinitionScanner scanner;
        try {
            //1.加载scanner元素类并实例化 @see QuartzScannerConfigurer
            Class<?> clazz = Class.forName(element.getAttribute("scanner"));
            Constructor<?> c = clazz.getConstructor(BeanDefinitionRegistry.class);
            scanner = (CommonsJobClassPathBeanDefinitionScanner) c.newInstance(parserContext.getRegistry());
            XmlReaderContext readerContext = parserContext.getReaderContext();

            //2.set BeanDefinition
            scanner.setResourceLoader(readerContext.getResourceLoader());
            scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults()); //默认配置
            scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns()); //自动注入

            //3.读子节点property 设置为scanner的attribute
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    Element prop = (Element) node;
                    //如果base
                    scanner.addProperty(prop.getAttribute("key"), prop.getAttribute("value"));
                }
            }

        } catch (Exception e) {
            log.error("commons-job-spring-ext parser exception:", e);
            throw new IllegalArgumentException(e);
        }

        //4.调doScan 扫描包 @see spring ClassPathBeanDefinitionScanner.doScan
        Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan();
        //5.注册扫到的beanDefinitions
        registerComponents(parserContext.getReaderContext(), beanDefinitions, element);

        return null;
    }

    /**
     * 可被实现 扩展
     * 注册扫到的beanDefinitions到readerContext
     * definition register processor def fire listener
     *
     * @param readerContext
     * @param beanDefinitions
     * @param element
     */
    protected void registerComponents(XmlReaderContext readerContext, Set<BeanDefinitionHolder> beanDefinitions, Element element) {

        //提取并包装element为CompositeComponentDefinition
        Object source = readerContext.extractSource(element);
        CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);

        //转为BeanComponentDefinition
        for (BeanDefinitionHolder beanDefHolder : beanDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(beanDefHolder));
        }

        //注册每个BeanComponentDefinition的处理器
        Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils
                .registerAnnotationConfigProcessors(readerContext.getRegistry(), source);

        //添加到监听器执行列表中
        for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(processorDefinition));
        }

        //触发readerContext 注册监听器
        readerContext.fireComponentRegistered(compositeDef);
    }
}
