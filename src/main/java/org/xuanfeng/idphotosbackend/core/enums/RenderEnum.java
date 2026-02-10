package org.xuanfeng.idphotosbackend.core.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RenderEnum {

	/**
	 * 纯色
	 */
	PURE("pure", 0),

	/**
	 * 上下
	 */
	UPDOWN("updown", 1),

	/**
	 * 中心
	 */
	CENTER("center", 2),

	;

	/**
	 * 类型
	 */
	private final String type;

	/**
	 * 序号
	 */
	private final Integer number;

	/**
	 * 根据类型获取枚举
	 *
	 * @param type 类型
	 * @return 枚举
	 */
	public static RenderEnum getByType(String type) {
		for (RenderEnum renderEnum : values()) {
			if (renderEnum.getType().equals(type)) {
				return renderEnum;
			}
		}
		return null;
	}

}
