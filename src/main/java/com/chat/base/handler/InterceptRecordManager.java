package com.chat.base.handler;

import com.chat.base.bean.entity.InterceptRecord.InterceptRecord;
import com.chat.base.bean.vo.InterceptRecordVo;
import com.chat.base.service.impl.InterceptRecordServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class InterceptRecordManager {

    @Autowired
    private InterceptRecordServiceImpl service;

    /**
     * /api/userInfo/register
     * @param source
     * @return
     */
    public List<InterceptRecordVo> getIpBySource(String source,Integer number,Integer time){
        return service.getBaseMapper().getIpBySource(source,number,time);
    }

    public void addRecord(String ip,Long userId,String source,String reason){

        ThreadPoolManager.discernRecordPool.execute(()->{
            try {
                InterceptRecord entity = new InterceptRecord();
                entity.setCreateTime(LocalDateTime.now());
                entity.setIp(ip);
                entity.setSource(source);
                entity.setUserId(userId);
                entity.setReason(reason);
                service.save(entity);
            }catch (Exception e){
                log.error("addRecord source={}",source,e);
            }
        });
    }
}
