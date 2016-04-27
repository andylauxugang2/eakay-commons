package cn.eakay.commons.base;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础数据对象
 */
public abstract class BaseDO implements Serializable {

    protected Long id;

    protected DateTime createTime = DateTime.now();

    protected DateTime updateTime = DateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Map<String, Object> parseModel(String... args) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String property : args) {
            //使用反射生成map
        }
        return map;
    }
}