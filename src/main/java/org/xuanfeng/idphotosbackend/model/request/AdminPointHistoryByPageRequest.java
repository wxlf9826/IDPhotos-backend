package org.xuanfeng.idphotosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminPointHistoryByPageRequest {

    /**
     * 用户id（可选）
     */
    private Long userId;

    /**
     * 积分类型（可选）
     */
    private String type;

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
