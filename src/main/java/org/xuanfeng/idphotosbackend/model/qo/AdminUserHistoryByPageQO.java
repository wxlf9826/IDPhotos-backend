package org.xuanfeng.idphotosbackend.model.qo;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserHistoryByPageQO {

    /**
     * 用户id（可选）
     */
    private Long userId;

    /**
     * 开始日期（yyyy-MM-dd，可选）
     */
    private String startDate;

    /**
     * 结束日期（yyyy-MM-dd，可选）
     */
    private String endDate;

    /**
     * 页码
     */
    @Min(value = 1, message = "页码必须大于0")
    @NotNull(message = "页码不能为空")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @Min(1)
    @Max(20)
    @NotNull(message = "每页大小不能为空")
    private Integer pageSize;
}
