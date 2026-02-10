package org.xuanfeng.idphotosbackend.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StateEnum {

	/**
	 * 有效
	 */
	VALID(1, "有效"),

	/**
	 * 无效
	 */
	INVALID(0, "无效"),

	;

	private final Integer code;

	private final String desc;

}