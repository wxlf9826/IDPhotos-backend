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

/**
 * 不想去部署apollo配置中心，依靠数据库来存放配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_config")
public class SystemConfig {

    /**
     * 自增主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 配置名称
     */
    @Column(name = "config_key")
    private String configKey;

    /**
     * 配置值
     */
    @Column(name = "config_value")
    private String configValue;

    /**
     * 配置说明
     */
    @Column(name = "description")
    private String description;

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
