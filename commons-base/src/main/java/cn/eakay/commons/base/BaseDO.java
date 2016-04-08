package cn.eakay.commons.base;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础数据对象
 */
public abstract class BaseDO implements Serializable {

    protected Long id;

    protected Date createTime;

    protected Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
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