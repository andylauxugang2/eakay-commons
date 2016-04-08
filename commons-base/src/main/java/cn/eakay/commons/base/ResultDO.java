package cn.eakay.commons.base;

import java.io.Serializable;

/**
 * 结果对象 供业务逻辑组件和逻辑组件层rpc接口调用方使用
 * Created by xugang on 16/4/6.
 */
public class ResultDO implements Serializable {

    private boolean success;

    /**
     * 错误码和错误描述由各业务方定义
     */
    private String errorCode;
    private String errorMsg;

    public ResultDO(){
        success = true;
    }

    public void setErrorInfo(String errorCode, String errorMsg) {
        setErrorCode(errorCode);
        setErrorMsg(errorMsg);
    }

    public void setErrorCode(String errorCode) {
        this.setSuccess(false);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFailure() {
        return !success;
    }

    public void format(String arg1) {
        this.setErrorMsg(String.format(this.getErrorMsg(), arg1));
        this.setErrorMsg(String.format(this.getErrorMsg(), arg1));
    }

    public void format(String... arguments) {
        this.setErrorMsg(String.format(this.getErrorMsg(), arguments));
        this.setErrorMsg(String.format(this.getErrorMsg(), arguments));
    }

    @Override
    public String toString() {
        return "ResultDO{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
