package org.xuanfeng.idphotosbackend.model.request;


import lombok.*;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HistoryByPageRequest {

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 页码
	 */
	private Integer pageNum;

	/**
	 * 每页大小
	 */
	private Integer pageSize;

	/**
	 * 是否需要统计总数
	 */
	private boolean needCount;

	/**
	 * 排序方式
	 */
	private OrderEnum orderEnum;
}
