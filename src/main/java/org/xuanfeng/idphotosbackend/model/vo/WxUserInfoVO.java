package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxUserInfoVO {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像链接
     */
    private String avatarUrl;

    /**
     * 注册时间
     */
    private String createTime;

    /**
     * 积分
     */
    private Integer points;
}