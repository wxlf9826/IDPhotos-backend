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
@Table(name = "point_history")
public class PointHistory {

    /**
     * 自增主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 变动数值：比如生成消耗则为 -1，签到奖励则为 +10
     */
    @Column(name = "change_amount")
    private Integer changeAmount;

    /**
     * 变动类型：比如制作消耗，观看广告奖励，管理员充值，见枚举PointTypeEnum
     */
    @Column(name = "type")
    private String type;

    /**
     * 最新总积分
     */
    @Column(name = "total_points")
    private Integer totalPoints;

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