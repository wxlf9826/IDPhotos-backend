package org.xuanfeng.idphotosbackend.model.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user_history")
public class AppUserHistory {

    /**
     * 自增主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户id，对于app_user表的主键id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 图片key
     */
    @Column(name = "image_key")
    private String imageKey;

    /**
     * 日期，只保留到日，比如2025-09-12
     */
    @Column(name = "time")
    private String time;

    /**
     * 使用参数
     */
    @Column(name = "use_param")
    private String useParam;

    /**
     * 逻辑删除标志 0-无效，1-有效
     */
    @Column(name = "state")
    private Integer state;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}
