package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPointHistoryBO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 积分类型编码
     */
    private String type;

    /**
     * 积分类型描述
     */
    private String changeType;

    /**
     * 变动分值
     */
    private Integer changeAmount;

    /**
     * 变动后积分
     */
    private Integer totalPoints;

    /**
     * 创建时间
     */
    private String createTime;
}
