package org.xuanfeng.idphotosbackend.core.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PointTypeEnum {

    /**
     * 制作消耗
     */
    MAKE("make", "制作消耗"),

    /**
     * 观看广告奖励
     */
    ADVERTISING("advertising", "观看广告奖励"),

    /**
     * 管理员奖励
     */
    REWARD("reward", "管理员奖励"),

    /**
     * 签到
     */
    CHECKIN("checkin", "签到"),

	;

    /**
     * 类型
     */
    private final String type;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据类型获取枚举
     *
     * @param type 类型
     * @return 枚举
     */
    public static PointTypeEnum getByType(String type) {
        for (PointTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

}
