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
public class AdminUserByPageQO {

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

    /**
     * 名称（模糊搜索）
     */
    private String nickName;

    /**
     * 状态：1-正常，2-禁止登录
     */
    private Integer status;

    /**
     * 排序字段：id / points
     */
    private String sortField;

    /**
     * 排序方式：asc / desc
     */
    private String order;
}
