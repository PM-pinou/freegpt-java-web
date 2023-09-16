package com.chat.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.bean.req.GetConsumptionRecordRequest;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.ConsumptionRecordsVo;
import com.chat.base.handler.ConsumptionRecordManager;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户的消费日志 前端控制器
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-15
 */
@RestController
public class ConsumptionRecordsController extends BaseController {

    @Autowired
    private ConsumptionRecordManager consumptionRecordManager;


    /**
     * 获取消费记录
     */
    @RequestMapping("/consumptionRecords/list")
    public ResultVO<IPage<ConsumptionRecordsVo>> getConsumptionRecords(@RequestBody GetConsumptionRecordRequest request){
        CacheGptApiTokenVo gptApiTokenVo = SessionUser.get().getGptApiTokenVo();
        request.setUserToken(gptApiTokenVo.getToken());
        return ResultVO.success(consumptionRecordManager.getConsumptionRecord(request));
    }

}

