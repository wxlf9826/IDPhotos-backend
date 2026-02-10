package org.xuanfeng.idphotosbackend.model.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserStatusUpdateRequest {

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 用户状态：1-正常，2-禁止登录
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
