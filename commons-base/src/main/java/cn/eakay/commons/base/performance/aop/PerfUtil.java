package cn.eakay.commons.base.performance.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 性能有关的配置文件 工具类
 * @author xugang
 */
@Slf4j
class PerfUtil {

    static boolean printLog = true;

    static final String KEY = "switch";
    static final String LOGS_PATH = "logs.path";
    static final String FILE_NAME = "perf-switch.properties";
    static final String TIMER_THREAD_NAME = "PerfAspectjFileWatchTimer";

    static {
        try {
            String path = getPath();
            File file = new File(path, FILE_NAME);
            if (!file.exists()) {
                createFileAndInitContent(file);
            }

            Properties properties = loadProperties(file);
            if (!properties.containsKey(KEY)) {
                createFileAndInitContent(file);
                printLog = true;
            } else {
                Object object = properties.get(KEY);
                String val = PerformanceLogUtils.toStr(object);
                printLog = "on".equals(val);
            }

            TimerTask task = new FileWatchTimerTask(file);
            new Timer(TIMER_THREAD_NAME).schedule(task, new Date(), 10 * 1000);
        } catch (Exception e) {
            log.error("error when init performace aspect", e);
        }
    }

    private static Properties loadProperties(File file) throws Exception {
        Properties properties = new Properties();
        InputStream is = new FileInputStream(file);
        properties.load(is);
        is.close();
        return properties;
    }

    private static String getPath() {
        String path = System.getProperty(LOGS_PATH);
        if (StringUtils.isBlank(path) || !new File(path).isDirectory()) {
            path = "/tmp/";
            new File(path).mkdirs();
        } else {
            if (!path.endsWith("/")) {
                path += "/";
            }
        }
        return path;
    }

    private static void createFileAndInitContent(File file) throws Exception {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println("#是否打印性能日志的开关，on表示开启，非on表示关闭，默认开启");
        writer.println("switch=on");
        writer.close();
    }

    @Slf4j
    private static class FileWatchTimerTask extends TimerTask {

        private long timeStamp;
        private File file;

        public FileWatchTimerTask(File file) {
            if (file == null) {
                throw new IllegalArgumentException("file can not be null!");
            }
            this.file = file;
            this.timeStamp = file.lastModified();
        }

        @Override
        public final void run() {
            long timeStamp = file.lastModified();
            if (this.timeStamp != timeStamp) {
                this.timeStamp = timeStamp;
                onUpdate(file);
            }
        }

        private void onUpdate(File file) {
            try {
                Properties properties = loadProperties(file);
                if (!properties.containsKey(KEY)) {
                    createFileAndInitContent(file);
                    printLog = true;
                } else {
                    Object object = properties.get(KEY);
                    String val = PerformanceLogUtils.toStr(object);
                    printLog = "on".equals(val);
                }
                log.info("#################printPerfLog={}", printLog);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

}
