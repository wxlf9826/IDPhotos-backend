package org.xuanfeng.idphotosbackend.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;

/**
 * 通用业务异常
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class CommonException extends RuntimeException {

    /**
     * 返回错误码
     */
    private ResultCodeEnum codeEnum;

    /**
     * 错误描述
     */
    private String message;


    public CommonException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum == null ? "" : resultCodeEnum.getMessage());
        this.message = resultCodeEnum == null ? "" : resultCodeEnum.getMessage();
        this.codeEnum = resultCodeEnum;
    }

    public CommonException(String message) {
        super(message);
        this.message = message;
    }

    public CommonException(Exception exception) {
        super(exception);
    }

    public CommonException(String message, Exception exception) {
        super(message, exception);
        this.message = message;
    }

    public CommonException(ResultCodeEnum resultCodeEnum, Exception exception) {
        super(resultCodeEnum == null ? "" : resultCodeEnum.getMessage(), exception);
        this.codeEnum = resultCodeEnum;
    }

    public CommonException(ResultCodeEnum resultCodeEnum, String message) {
        super(message);
        this.codeEnum = resultCodeEnum;
        this.message = message;
    }

}