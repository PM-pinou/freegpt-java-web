package com.chat.base.record;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.RunnerTest;
import com.chat.base.bean.req.GetConsumptionRecordRequest;
import com.chat.base.bean.vo.ConsumptionRecordsVo;
import com.chat.base.handler.ConsumptionRecordManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConsumptionRecordManagerTest extends RunnerTest {

    @Autowired
    ConsumptionRecordManager consumptionRecordManager;

    @Test
    public void test(){

        GetConsumptionRecordRequest recordRequest = new GetConsumptionRecordRequest();

        IPage<ConsumptionRecordsVo> consumptionRecord = consumptionRecordManager.getConsumptionRecord(recordRequest);
        System.out.println(consumptionRecord);
    }

}
