package org.xuanfeng.idphotosbackend.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WxGrantTypeEnum {

	/**
     * 自主授权
	 */
	AUTHORIZATION_CODE("authorization_code", "自主授权"),

	;

	/**
	 * 类型
	 */
	private final String type;

	/**
	 * 描述
	 */
	private final String desc;

}