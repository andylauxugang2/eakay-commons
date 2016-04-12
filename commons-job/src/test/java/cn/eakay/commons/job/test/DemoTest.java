package cn.eakay.commons.job.test;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试 demo
 *
 * @author xugang
 * @since 16/4/12.
 */
public class DemoTest extends TestBase {

    @Test
    public void test() throws Exception {
    }

    private Future<?> getFuture(){
        ExecutorService executor = Executors.newScheduledThreadPool(1);
        Future<?> future = executor.submit(new TestRunnable(new AtomicInteger(Integer.MIN_VALUE)));
        executor.shutdown();
        return future;
    }
}