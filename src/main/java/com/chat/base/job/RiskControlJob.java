package com.chat.base.job;

import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.vo.InterceptRecordVo;
import com.chat.base.handler.InterceptRecordManager;
import com.chat.base.utils.RiskControlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class RiskControlJob {

    @Autowired
    private InterceptRecordManager interceptRecordManager;

    @PostConstruct
    public void init(){
        checkUserBehavior();
    }

    /**
     * todo 待优化，这个定时器应该需要根据规则的时间进行配置来调度，目前暂时和写死
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void checkUserBehavior(){
        try {
            List<InterceptRecordVo> interceptRecordVos = interceptRecordManager.getIpBySource("/api/userInfo/register", LimitEnum.IP.getNumber(),10);
            for (InterceptRecordVo interceptRecordVo : interceptRecordVos) {
                if(interceptRecordVo.getIp()!=null){
                    RiskControlUtil.put(interceptRecordVo.getIp());
                }
                if(interceptRecordVo.getUserId()!=null){
                    RiskControlUtil.put(String.valueOf(interceptRecordVo.getUserId()));
                }
                log.info("checkUserBehavior vo={}",interceptRecordVo);
            }
        }catch (Exception e){
            log.error("checkUserBehavior error ",e);
        }
    }
}
