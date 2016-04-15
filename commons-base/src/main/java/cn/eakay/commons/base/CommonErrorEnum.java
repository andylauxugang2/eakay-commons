package cn.eakay.commons.base;

import lombok.Getter;

/**
 * File相关错误码
 *
 * @author xugang
 */
public enum CommonErrorEnum {


    PARAM_EMPTY_ERROR("001", ErrorMsg.PARAM_EMPTY_MSG),
    UNKOWN("UNKOWN", ErrorMsg.UNKOWN);

    public static final String NAMESPACE = "param";

    @Getter
    private String errorCode;
    @Getter
    private String errorMsg;

    CommonErrorEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public boolean isEqual(ResultDO rs) {
        return this.getErrorCode().equals(rs.getErrorCode());
    }

    public void fillResult(ResultDO rs) {
        rs.setErrorCode(getErrorCode());
        rs.setErrorMsg(getErrorMsg());
    }

}

class ErrorMsg {
    public static final String PARAM_EMPTY_MSG = "";
    public static final String UNKOWN = "UNKOWN";
}