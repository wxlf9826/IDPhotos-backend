package org.xuanfeng.idphotosbackend.core.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderEnum {

	ASC("asc", "升序"),

	DESC("desc", "降序"),

	;

	/**
	 * 顺序
	 */
	private final String order;

	/**
	 * 描述
	 */
	private final String desc;

	/**
	 * 根据顺序获取枚举
	 *
	 * @param order 顺序
	 * @return 枚举
	 */
	public static OrderEnum getByOrder(String order) {
		for (OrderEnum orderEnum : values()) {
			if (orderEnum.getOrder().equals(order)) {
				return orderEnum;
			}
		}
		return null;
	}

}
