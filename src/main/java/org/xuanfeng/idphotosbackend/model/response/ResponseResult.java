package org.xuanfeng.idphotosbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ResponseResult<T> implements Serializable {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    public ResponseResult(Integer code, String message, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String message) {
        this(code, message, null);
    }

    public ResponseResult(Integer code) {
        this(code, null);
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(200, "操作成功", null);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "操作成功", data);
    }

    public static <T> ResponseResult<T> error(Integer code, String message) {
        return new ResponseResult<>(code, message, null);
    }

}