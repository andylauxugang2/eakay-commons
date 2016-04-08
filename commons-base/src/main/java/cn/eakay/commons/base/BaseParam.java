package cn.eakay.commons.base;

import java.io.Serializable;

/**
 * 基本的外部调用接口的参数对象，比如createOrder updateOrder 等
 * 可调用http/rpc服务接口使用
 * @author xugang
 */
public class BaseParam implements Serializable{

    private String sourceIp;
    private String appKey;
    private String operator;
    private String business;

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }
}
