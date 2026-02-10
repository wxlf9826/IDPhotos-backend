package org.xuanfeng.idphotosbackend.model.vo;

import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PageListVO<T> {

	/**
	 * 总数
	 */
	private Long total;

	/**
	 * 当前页码
	 */
	private Integer pageNum;

	/**
	 * 单页大小
	 */
	private Integer pageSize;

	/**
	 * 是否有下一页
	 */
	private Boolean hasMore;

	/**
	 * 数据
	 */
	private List<T> list;

}
