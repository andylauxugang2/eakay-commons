package cn.eakay.commons.job;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 测试 quartz demo
 * 启动后 查看屏幕输出
 * Created by xugang on 16/4/12.
 */
public class DemoQuartzBootstrap {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("quartz-demo.xml");
        System.out.println(context);
    }
}
