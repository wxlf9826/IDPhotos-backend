package org.xuanfeng.idphotosbackend.config;

import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.model.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = {CommonException.class})
	public ResponseResult<Object> businessExceptionHandler(HttpServletRequest req, CommonException ce) throws Exception {
		ResponseResult<Object> result = new ResponseResult<>();
		result.setCode(ce.getCodeEnum() != null ? ce.getCodeEnum().getCode() : ResultCodeEnum.SYSTEM_ERROR.getCode());
		result.setMessage(ce.getMessage());
		log.info("抛出通用异常：{}", ce.getMessage(), ce);
		return result;
	}

	/**
	 * 参数校验异常处理
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		StringBuilder sb = new StringBuilder();
		bindingResult.getFieldErrors().forEach(error -> sb.append(error.getDefaultMessage()).append(";"));
		String message = sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "参数校验失败";
		log.error("参数校验异常: {}", message);
		return ResponseResult.error(400, message);
	}

	/**
	 * 拦截 Sa-Token 异常
	 */
	@ExceptionHandler(NotLoginException.class)
	public ResponseResult<Object> notLoginExceptionHandler(NotLoginException nle) {
		// 打印日志，方便排查
		log.warn("用户未登录异常: {}", nle.getMessage());

		// 判断场景值（Token无效、过期、被顶下线等）
		String message = "请先登录";
		if (nle.getType().equals(NotLoginException.NOT_TOKEN)) {
			message = "未携带 Token";
		} else if (nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
			message = "Token 无效";
		} else if (nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
			message = "Token 已过期";
		} else if (nle.getType().equals(NotLoginException.BE_REPLACED)) {
			message = "您的账号已在另一台设备登录";
		}

		return ResponseResult.error(401, message); // 401 是标准的未认证状态码
	}

	/**
	 * 拦截参数校验异常 (比如 @RequestParam 缺失)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseResult<Object> handleRuntimeException(Exception e) {
		log.error("系统未知异常: ", e);
		return ResponseResult.error(500, e.getMessage());
	}

}
