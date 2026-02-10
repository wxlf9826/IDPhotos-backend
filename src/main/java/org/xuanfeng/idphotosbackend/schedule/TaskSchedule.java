package org.xuanfeng.idphotosbackend.schedule;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xuanfeng.idphotosbackend.service.biz.PhotoBizService;

import javax.annotation.Resource;

@Component
@Slf4j
public class TaskSchedule {

    @Resource
    private PhotoBizService photoBizService;

    //每天00:00清除7天前的记录
    @Scheduled(cron = "0 0 0 * * ?")
    public void delete7dayAgoPhoto() {
        log.info("TaskSchedule delete7dayAgoPhoto begin");
        // 往前7天的
        Integer dayBefore = -7;
        Integer count = photoBizService.deletePhoto(dayBefore);
        log.info("TaskSchedule delete7dayAgoPhoto finish, delete count:{}", count);
    }
}
