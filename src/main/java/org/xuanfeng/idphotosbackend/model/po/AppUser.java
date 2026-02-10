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
@Table(name = "app_user")
public class AppUser {

    /**
     * 自增主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * openid
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 用户名字
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 用户头像key（s3中）
     */
    @Column(name = "avatar_key")
    private String avatarKey;

    /**
     * 用户积分
     */
    @Column(name = "points")
    private Integer points;

    /**
     * 用户状态：1-正常，2-禁止登录
     */
    @Column(name = "status")
    private Integer status;

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
