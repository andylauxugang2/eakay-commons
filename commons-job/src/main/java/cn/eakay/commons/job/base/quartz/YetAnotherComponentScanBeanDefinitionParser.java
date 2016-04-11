package cn.eakay.commons.job.base.quartz;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * Custom edition of org.springframework.context.annotation.ComponentScanBeanDefinitionParser
 * 
 */
public class YetAnotherComponentScanBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        
        YacsClassPathBeanDefinitionScanner scanner;
        try {
            Class<?> clazz = Class.forName(element.getAttribute("scanner"));
            Constructor<?> c = clazz.getConstructor(BeanDefinitionRegistry.class);

            XmlReaderContext readerContext = parserContext.getReaderContext();

            scanner = (YacsClassPathBeanDefinitionScanner) c.newInstance(parserContext.getRegistry());
            scanner.setResourceLoader(readerContext.getResourceLoader());
            scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults());
            scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns());

            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    Element prop = (Element) node;
                    scanner.addProperty(prop.getAttribute("key"), prop.getAttribute("value"));
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan();
        registerComponents(parserContext.getReaderContext(), beanDefinitions, element);

        return null;
    }

    protected void registerComponents(XmlReaderContext readerContext, Set<BeanDefinitionHolder> beanDefinitions,
            Element element) {

        Object source = readerContext.extractSource(element);
        CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);

        for (BeanDefinitionHolder beanDefHolder : beanDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(beanDefHolder));
        }

        Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils
                .registerAnnotationConfigProcessors(readerContext.getRegistry(), source);
        for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(processorDefinition));
        }

        readerContext.fireComponentRegistered(compositeDef);
    }
}
