package org.xuanfeng.idphotosbackend.core.enums;

public enum ResultCodeEnum {

    SUCCESS(0, "成功"),

    SYSTEM_ERROR(10001, "系统异常"),
    SYSTEM_INFO_ERROR(10002, "系统信息异常"),
    PARAM_ERROR(10003, "参数错误"),

    WX_LOGIN_FAIL(20001, "微信登录失败，请求结果为空"),
    WX_USER_DELETED(20002, "您已申请注销，无法登录，可联系客服恢复"),
    WX_USER_USER_NOT_EXIST(20003, "用户不存在"),
    WX_USESR_NOT_LOGIN(20004, "未登录"),

    PHOTO_ERROR(30001, "请求图片AI异常"),
    S3_ERROR(30002, "s3异常"),
    POINTS_NOT_ENOUGH(30003, "积分不足"),
    IMAGE_SECURITY_CHECK_FAIL(30004, "图片安全检测不通过"),
    ;

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
