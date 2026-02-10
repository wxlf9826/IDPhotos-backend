package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserHistoryBO {

    /**
     * 记录id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 图片key
     */
    private String imageKey;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 日期（yyyy-MM-dd）
     */
    private String date;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 参数
     */
    private String useParam;
}
