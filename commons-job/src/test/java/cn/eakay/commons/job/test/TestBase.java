package cn.eakay.commons.job.test;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * spring-test junit base class
 * 无需添加事务管理（需要定义transactionManager）
 *
 * @author xugang
 * @see AbstractTransactionalJUnit4SpringContextTests
 * RunWith ContextConfiguration 注解可被继承
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-all.xml"
})
public abstract class TestBase {
    private static final String SYSERROR_JSON = "{error:0,data:null,errorMsg:''}";

    @Autowired
    protected ApplicationContext ctx;

    public void setUp() throws Exception {
        System.out.println(ctx);
    }

    @SuppressWarnings("unchecked")
    public static <T> T unwrap(T proxiedInstance) {
        if (proxiedInstance instanceof Advised) {
            try {
                return unwrap((T) ((Advised) proxiedInstance).getTargetSource().getTarget());
            } catch (Exception e) {
                Assert.fail(proxiedInstance.getClass().getName());
            }
        }
        return proxiedInstance;
    }
}

class TestRunnable implements Runnable {

    final AtomicInteger counter;

    public TestRunnable(AtomicInteger counter) {
        this.counter = counter;
    }

    public void run() {
        System.out.println(counter.incrementAndGet());
    }
}
