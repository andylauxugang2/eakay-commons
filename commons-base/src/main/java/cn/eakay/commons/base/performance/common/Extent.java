package cn.eakay.commons.base.performance.common;

import lombok.Getter;

/**
 * 系统监控区间
 * Created by xugang on 16/4/7.
 */
public enum  Extent {
    Whole("whole"), Part("part");

    @Getter
    private String name;

    private Extent(String name) {
        this.name = name;
    }
}
