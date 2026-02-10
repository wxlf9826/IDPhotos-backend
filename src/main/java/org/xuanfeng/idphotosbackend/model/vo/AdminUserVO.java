package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserVO {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像链接
     */
    private String avatarUrl;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 状态：1-正常，2-禁止登录
     */
    private Integer status;

    /**
     * 注册时间
     */
    private String createTime;
}
