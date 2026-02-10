package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryBO {

    /**
     * id
     */
    private Long id;

    /**
     * 图片key
     */
    private String imageKey;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 日期 如：2025-12-15
     */
    private String date;

    /**
     * 创建时间 如：2025-12-25 11:12:12
     */
    private String createTime;

    /**
     * 参数
     */
    private String useParam;

}
