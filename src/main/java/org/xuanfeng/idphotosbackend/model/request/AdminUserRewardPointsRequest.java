package org.xuanfeng.idphotosbackend.model.request;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRewardPointsRequest {

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 赠送积分
     */
    @NotNull(message = "赠送积分不能为空")
    @Min(value = 1, message = "赠送积分必须大于0")
    private Integer points;
}
