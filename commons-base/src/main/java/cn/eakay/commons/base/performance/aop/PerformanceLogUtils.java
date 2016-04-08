package cn.eakay.commons.base.performance.aop;

import cn.eakay.commons.base.performance.common.Extent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerformanceLogUtils {

    private static String logFormat = "request_id:%s elapsed:%s extent:%s desc:%s";

    public static void logPart(String desc, long elapsed) {
        log(Extent.Part, desc, elapsed);
    }

    public static void logWhole(String desc, long elapsed) {
        log(Extent.Whole, desc, elapsed);
    }

    public static void log(Extent extent, String desc, long elapsed) {
        String msg = String.format(logFormat, null, elapsed, extent.getName(), desc);
        log.info(msg);
    }

    static String toStr(Object object) {
        if (object == null)
            return "nil";
        return object.toString();
    }

}
