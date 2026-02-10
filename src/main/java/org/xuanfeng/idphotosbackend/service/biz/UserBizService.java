package org.xuanfeng.idphotosbackend.service.biz;

import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.model.bo.UserHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.PointHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.WxLoginBO;
import org.xuanfeng.idphotosbackend.model.bo.WxUserInfoBO;
import org.xuanfeng.idphotosbackend.model.request.HistoryByPageRequest;

public interface UserBizService {

    /**
     * 微信登录
     *
     * @param code 授权码
     * @return 登录信息
     */
    WxLoginBO wxLogin(String code);

    /**
     * 更新用户信息
     *
     * @param file     头像文件
     * @param nickName 昵称
     * @param userId   用户id
     */
    void updateUserInfo(MultipartFile file, String nickName, Long userId);

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    WxUserInfoBO getUserInfo(Long userId);

    /**
     * 获取历史记录
     *
     * @return 结果
     */
    PageInfo<UserHistoryBO> getUserHistory(HistoryByPageRequest request);

    /**
     * 删除单个记录
     *
     * @param historyId 历史id
     * @return 删除数量
     */
    Integer deleteHistory(Long historyId);

    /**
     * 获取积分历史记录
     *
     * @return 结果
     */
    PageInfo<PointHistoryBO> getUserPointHistory(HistoryByPageRequest request);
}
