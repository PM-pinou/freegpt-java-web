package com.chat.base.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.ConsumptionRecords;
import com.chat.base.bean.req.GetConsumptionRecordRequest;
import com.chat.base.bean.vo.ConsumptionRecordsVo;
import com.chat.base.bean.vo.GptConsumptionRecordVo;
import com.chat.base.service.impl.ConsumptionRecordsServiceImpl;
import com.chat.base.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ConsumptionRecordManager {

    @Autowired
    private ConsumptionRecordsServiceImpl consumptionRecordsService;


    public IPage<ConsumptionRecordsVo> getConsumptionRecord(GetConsumptionRecordRequest req){
        ConsumptionRecords entity = new ConsumptionRecords();
        entity.setUserToken(req.getUserToken());
        QueryWrapper<ConsumptionRecords> wrapper = new QueryWrapper<>();
        wrapper.setEntity(entity);
        wrapper.orderByDesc("create_time");
        IPage<ConsumptionRecords> recordsIPage = consumptionRecordsService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), wrapper);
        List<ConsumptionRecordsVo> recordsVos = new ArrayList<>();
        for (ConsumptionRecords record : recordsIPage.getRecords()) {
            ConsumptionRecordsVo vo = new ConsumptionRecordsVo();
            BeanUtils.copyProperties(record,vo);
            vo.setCost(AmountUtil.getUserAmount(Long.parseLong(vo.getCost())));
            vo.setCostBefore(AmountUtil.getUserAmount(Long.parseLong(vo.getCostBefore())));
            vo.setCostAfter(AmountUtil.getUserAmount(Long.parseLong(vo.getCostAfter())));
            recordsVos.add(vo);
        }
        IPage<ConsumptionRecordsVo> recordsVoIPage = new Page<>();
        recordsVoIPage.setRecords(recordsVos);
        recordsVoIPage.setTotal(recordsIPage.getTotal());
        return recordsVoIPage;
    }


    /**
     * todo 在用户消费完之后插入消费记录表
     * 新增用户消费记录
     */
    public boolean addGptConsumptionRecord(GptConsumptionRecordVo vo){
        ConsumptionRecords entity = new ConsumptionRecords();

        entity.setCostAfter(vo.getAfter());
        entity.setCostBefore(vo.getBefore());
        entity.setCost(vo.getCost());
        entity.setCreateTime(LocalDateTime.now());
        entity.setOp(vo.getOp());
        entity.setUserToken(vo.getUserToken());
        entity.setSystemToken(vo.getSystemToken());
        entity.setType(vo.getType());
        return consumptionRecordsService.save(entity);
    }


    public void asyncAddGptConsumptionRecord(GptConsumptionRecordVo vo){
        ThreadPoolManager.consumptionRecordPool.execute(()->{
            boolean result = false;
            try {
                result = addGptConsumptionRecord(vo);
            }finally {
                log.info("asyncAddGptConsumptionRecord add result={},vo={} ",result,vo);
            }
        });
    }

}
