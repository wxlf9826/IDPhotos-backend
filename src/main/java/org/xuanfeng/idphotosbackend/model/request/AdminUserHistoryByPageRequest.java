package org.xuanfeng.idphotosbackend.model.request;

import lombok.*;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;

import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserHistoryByPageRequest {

    /**
     * 用户id（可选）
     */
    private Long userId;

    /**
     * 开始时间（可选）
     */
    private Date startTime;

    /**
     * 结束时间（可选）
     */
    private Date endTime;

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
