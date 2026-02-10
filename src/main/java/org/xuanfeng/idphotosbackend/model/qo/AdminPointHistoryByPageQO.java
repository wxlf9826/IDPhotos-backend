package org.xuanfeng.idphotosbackend.model.qo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPointHistoryByPageQO {

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
    @Min(value = 1, message = "页码必须大于0")
    @NotNull(message = "页码不能为空")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @Min(1)
    @Max(50)
    @NotNull(message = "每页大小不能为空")
    private Integer pageSize;
}
