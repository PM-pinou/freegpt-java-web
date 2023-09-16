package com.chat.base.handler.billing.impl;

import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.ModelBillingParamVo;
import com.chat.base.handler.billing.ModelBillingService;
import com.chat.base.utils.ResultVO;
import org.springframework.stereotype.Component;

/**
 * 通过用户次数收费的
 */
@Component
public class ModelBillingByNumberImpl implements ModelBillingService {

    @Override
    public ResultVO<String> beforeBilling(CacheGptApiTokenVo vo, long cost,String tradeId) {
        Long visitNumber = vo.getVisitNumber();
        if(visitNumber>0){
            return ResultVO.success();
        }
        return ResultVO.fail("访问次数不足");
    }

    @Override
    public boolean billing(CacheGptApiTokenVo vo, ModelBillingParamVo modelBillingParamVo, long cost,String tradeId) {
        cost = 1L;
        Long visitNumber = vo.getVisitNumber();
        if(visitNumber > 0){
            synchronized(vo.getClass()){
                visitNumber = vo.getVisitNumber();
                long after = visitNumber - cost;
                if( visitNumber>0 &&  after>= 0 ){
                    vo.setVisitNumber(after);
                    return true; // 消费成功
                }
            }
        }
        vo.setVisitNumber(0L);
        return false;
    }

    @Override
    public boolean upPilling(CacheGptApiTokenVo vo, ModelBillingParamVo modelBillingParamVo, long cost) {
        cost = 1L;
        Long visitNumber = vo.getVisitNumber();
        if(visitNumber > 0){
            synchronized(vo.getClass()){
                visitNumber = vo.getVisitNumber();
                long after = visitNumber + cost;
                if( visitNumber>0 &&  after>= 0 ){
                    vo.setVisitNumber(after);
                    return true; // 消费成功
                }
            }
        }
        vo.setVisitNumber(0L);
        return false;
    }

    /**
     * 目前模型3.5 turbo通过次数来计费
     * @return
     */
    @Override
    public String[] billingModel() {
        return new String[]{};
    }
}
