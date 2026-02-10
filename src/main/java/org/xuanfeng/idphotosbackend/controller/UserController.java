package org.xuanfeng.idphotosbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;
import org.xuanfeng.idphotosbackend.model.bo.PointHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.UserHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.WxLoginBO;
import org.xuanfeng.idphotosbackend.model.bo.WxUserInfoBO;
import org.xuanfeng.idphotosbackend.model.qo.HistoryByPageQO;
import org.xuanfeng.idphotosbackend.model.request.HistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.response.ResponseResult;
import org.xuanfeng.idphotosbackend.model.vo.*;
import org.xuanfeng.idphotosbackend.service.biz.UserBizService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserBizService userBizService;

    @GetMapping("/login")
    public ResponseResult<WxLoginVO> login(@RequestParam String code) {
        WxLoginBO wxLoginBO = userBizService.wxLogin(code);
        return ResponseResult.success(WxLoginVO.builder()
                .openId(wxLoginBO.getOpenId())
                .token(wxLoginBO.getToken())
                .adUnitId(wxLoginBO.getAdUnitId())
                .build());
    }

    @PostMapping("/info/update")
    public ResponseResult<Void> updateUserInfo(@RequestParam(name = "file", required = false) MultipartFile file,
                                               @RequestParam(name = "nickName", required = false) String nickName) {
        Long userId = StpUtil.getLoginIdAsLong();
        userBizService.updateUserInfo(file, nickName, userId);
        return ResponseResult.success();
    }

    @GetMapping("/info")
    public ResponseResult<WxUserInfoVO> getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        WxUserInfoBO userInfo = userBizService.getUserInfo(userId);
        return ResponseResult.success(WxUserInfoVO.builder()
                .avatarUrl(userInfo.getAvatarUrl())
                .nickName(userInfo.getNickName())
                .createTime(userInfo.getCreateTime())
                .points(userInfo.getPoints())
                .build());
    }

    @PostMapping("/history")
    public ResponseResult<PageListVO<UserHistoryVO>> getUserHistory(@RequestBody @Valid HistoryByPageQO historyByPageQO) {
        Long userId = StpUtil.getLoginIdAsLong();

        historyByPageQO.setUserId(userId);
        PageInfo<UserHistoryBO> pageInfo = userBizService.getUserHistory(HistoryByPageRequest.builder()
                .userId(userId)
                .needCount(true)
                .orderEnum(OrderEnum.DESC)
                .pageNum(historyByPageQO.getPageNum())
                .pageSize(historyByPageQO.getPageSize())
                .build());

        return ResponseResult.success(PageListVO.<UserHistoryVO>builder()
                .hasMore(pageInfo.isHasNextPage())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .total(pageInfo.getTotal())
                .list(pageInfo.getList().stream().map(bo -> {
                    UserHistoryVO vo = new UserHistoryVO();
                    BeanUtils.copyProperties(bo, vo);
                    return vo;
                }).collect(Collectors.toList()))
                .build());
    }

    @GetMapping("/history/delete")
    public ResponseResult<Integer> deleteHistory(Long historyId) {
        Long userId = StpUtil.getLoginIdAsLong();

        Integer count = userBizService.deleteHistory(historyId);
        return ResponseResult.success(count);
    }

    @PostMapping("/point/history")
    public ResponseResult<PageListVO<PointHistoryVO>> getUserPointHistory(@RequestBody @Valid HistoryByPageQO historyByPageQO) {
        Long userId = StpUtil.getLoginIdAsLong();

        historyByPageQO.setUserId(userId);
        PageInfo<PointHistoryBO> pageInfo = userBizService.getUserPointHistory(HistoryByPageRequest.builder()
                .userId(userId)
                .needCount(true)
                .orderEnum(OrderEnum.DESC)
                .pageNum(historyByPageQO.getPageNum())
                .pageSize(historyByPageQO.getPageSize())
                .build());

        return ResponseResult.success(PageListVO.<PointHistoryVO>builder()
                .hasMore(pageInfo.isHasNextPage())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .total(pageInfo.getTotal())
                .list(pageInfo.getList().stream().map(bo -> {
                    PointHistoryVO vo = new PointHistoryVO();
                    BeanUtils.copyProperties(bo, vo);
                    return vo;
                }).collect(Collectors.toList()))
                .build());
    }

}
