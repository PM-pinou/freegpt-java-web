package com.chat.base.job;

import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.ConsumptionConstant;
import com.chat.base.bean.constants.ModelPriceEnum;
import com.chat.base.bean.entity.MjTaskInfo;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.handler.DrawManager;
import com.chat.base.handler.DrawTaskInfoManager;
import com.chat.base.handler.UserManager;
import com.chat.base.handler.model.bean.QueryDrawModelResult;
import com.chat.base.service.impl.UserInfoServiceImpl;
import com.chat.base.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class MyTaskStatusJob {

    @Autowired
    private DrawTaskInfoManager drawTaskInfoManager;
    @Autowired
    private UserInfoServiceImpl userInfoService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private DrawManager drawManager;
    @PostConstruct
    public void init(){
        checkTaskStatus();
    }

    /**
     * todo 待优化，这个定时器应该需要根据规则的时间进行配置来调度，目前暂时和写死
     */
    @Scheduled(cron = "*/20 * * * * *")
    public void checkTaskStatus() {
        List<MjTaskInfo> mjTaskInfos = drawTaskInfoManager.queryMjTaskIInProgress();
        for (MjTaskInfo mjTaskInfo : mjTaskInfos) {
            try {
                Boolean overTimeProcess = overTimeProcess(mjTaskInfo);
                if (!overTimeProcess){
                    continue;
                }
                QueryDrawModelResult result = drawManager.getMjImage(mjTaskInfo.getTaskId(), mjTaskInfo.getModelId());
                if (Objects.nonNull(result)) {
                    String imageUrl = result.getImageUrl();
                    String status = result.getStatus();
                    if (StringUtils.isBlank(imageUrl) || StringUtils.isBlank(status)) {
                        continue;
                    }

                    imageUrl = imageUrl.replace("https://cdn.discordapp.com", "http://cdn.liulinlin.top");
                    mjTaskInfo.setUpdateTime(LocalDateTime.now());
                    mjTaskInfo.setTaskUrl(imageUrl);
                    switch (status) {
                        case "SUCCESS":
                            mjTaskInfo.setStatus(2);
                            mjTaskInfo.setFinishTime(LocalDateTime.now());
                            break;
                        case "IN_PROGRESS":
                            break;
                        case "FAILURE":
                            handleFailure(mjTaskInfo);
                            mjTaskInfo.setStatus(-1);
                            break;
                    }
                    Boolean updateResult = drawTaskInfoManager.updateMjTaskInfo(mjTaskInfo);
                    log.info("checkTaskStatus taskId={},status={},updateResult={}",mjTaskInfo.getTaskId(),mjTaskInfo.getStatus(),updateResult);
                }
            }catch (Exception e){
                log.error("MyTaskStatusJob error mjTaskInfo={}",mjTaskInfo,e);
            }
        }
    }

    private Boolean handleFailure(MjTaskInfo mjTaskInfo) {
        CacheUserInfoVo cacheUserInfoVo = CacheUtil.getIfPresent(mjTaskInfo.getUserId().toString());
        if (cacheUserInfoVo == null) {
            UserInfo userInfo = userInfoService.queryUserInfoById(mjTaskInfo.getUserId());
            if (Objects.nonNull(userInfo)) {
                cacheUserInfoVo = userManager.getCacheUserInfoVoByUserByUserBean(userInfo, "");
            } else {
                log.warn("handleFailure no such user userId={}",mjTaskInfo.getUserId());
                return false;
            }
        }
        String tradeId = UUID.randomUUID().toString();
        //mj 这里的模型入参先这样写
        boolean result = userManager.updateUserBalance(cacheUserInfoVo,ModelPriceEnum.M_J.getModel(), ModelPriceEnum.M_J.getInPrice(), mjTaskInfo.getUseToken(), ConsumptionConstant.MJ_TYPE, CommonConstant.RETURN_MONEY,tradeId);
        log.info("increaseCacheBalance taskId={}, result ={}",mjTaskInfo.getTaskId(),result);
        return true;
    }

    private Boolean overTimeProcess(MjTaskInfo mjTaskInfo){
        //判断当前时间是否比创建时间大10分钟
        Duration duration = Duration.between(mjTaskInfo.getCreateTime(), LocalDateTime.now());
        if (duration.toMinutes() >= 10){
            Boolean handledFailure = handleFailure(mjTaskInfo);
            mjTaskInfo.setUpdateTime(LocalDateTime.now());
            // 失败状态
            mjTaskInfo.setStatus(CommonConstant.DRAW_FAIL);
            Boolean updateResult = drawTaskInfoManager.updateMjTaskInfo(mjTaskInfo);
            log.warn("overTimeProcess over tem minutes taskId={},handledFailure={},updateResult={}",mjTaskInfo.getTaskId(),handledFailure,updateResult);
            return false;
        }
        return true;
    }


}
