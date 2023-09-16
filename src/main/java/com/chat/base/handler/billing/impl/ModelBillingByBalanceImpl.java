package com.chat.base.handler.billing.impl;

import com.chat.base.bean.constants.ModelPriceEnum;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.GptConsumptionRecordVo;
import com.chat.base.bean.vo.ModelBillingParamVo;
import com.chat.base.handler.ConsumptionRecordManager;
import com.chat.base.handler.billing.ModelBillingService;
import com.chat.base.utils.CacheUtil;
import com.chat.base.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过用户余额计费
 */
@Slf4j
@Component
public class ModelBillingByBalanceImpl implements ModelBillingService {


    private static  final Map<Long, ConcurrentHashMap<String,Long>> userAdvanceChargeMap = new ConcurrentHashMap<>();


    public ConcurrentHashMap<String, Long> getUserAdvanceChargeMap(Long userId){
        ConcurrentHashMap<String, Long> map = userAdvanceChargeMap.get(userId);
        if(map!=null){
            return map;
        }
        synchronized (ModelBillingByBalanceImpl.class){
            map = userAdvanceChargeMap.get(userId);
            if(map==null){
                map = new ConcurrentHashMap<String, Long>(300);
                userAdvanceChargeMap.put(userId,map);
            }
        }
        return map;
    }


    public void removeUserTradeId(Long userId,String tradeId){
        userAdvanceChargeMap.get(userId).remove(tradeId);
    }



    /**
     * 用户每一次请求的预付款
     */
    private static final long advanceChargeAmount = 1000000;

    @Autowired
    private ConsumptionRecordManager consumptionRecordManager;

    @Override
    public ResultVO<String> beforeBilling(CacheGptApiTokenVo vo, long cost,String tradeId) {

        if(cost<0){
            log.error("beforeBilling error cost is null");
            return ResultVO.fail("参数错误");
        }
        long balance = vo.getBalance();
        if(cost>balance){
            return ResultVO.fail("余额不足");
        }
        CacheUserInfoVo cacheUserInfoVo = CacheUtil.getIfPresent(String.valueOf(vo.getUserId()));
        // todo 如果多实例部署的话 这里需要改成 分布式锁
        synchronized(cacheUserInfoVo.getClass()){
            ConcurrentHashMap<String, Long> advanceChargeMap = userAdvanceChargeMap.getOrDefault(vo.getUserId(), getUserAdvanceChargeMap(vo.getUserId()));
            //用户余额大于本次最低的消费
            if(vo.getBalance()>=cost){
                long advanceChargeAmountAfter = vo.getBalance() - advanceChargeAmount; //用户的预付之后的余额
                if(advanceChargeAmountAfter>0){
                    //当预付款之后的余额还大于0
                    vo.setBalance(advanceChargeAmountAfter);
                    advanceChargeMap.put(tradeId, advanceChargeAmount);
                    return ResultVO.success();
                }else{
                    //当预付款之后的余额小于等于0
                    advanceChargeMap.put(tradeId, vo.getBalance());
                    vo.setBalance(0);//将当前用户余额设置为0
                    return ResultVO.success();
                }
            }
        }
        return ResultVO.fail("当前余额不支持本次请求，请保持额度充足");
    }

    @Override
    public boolean billing(CacheGptApiTokenVo vo, ModelBillingParamVo modelBillingParamVo, long cost,String tradeId) {
        log.info("billing vo={} cost={},modelBillingParamVo={}",vo,cost,modelBillingParamVo);
        if(vo==null || cost<0 )return false;

        CacheUserInfoVo cacheUserInfoVo = CacheUtil.getIfPresent(String.valueOf(vo.getUserId()));

        if(vo.getBalance() > 0 || advanceChargeAmount > 0){
            // todo 如果多实例部署的话 这里需要改成 分布式锁
            synchronized(cacheUserInfoVo.getClass()){
                ConcurrentHashMap<String, Long> advanceChargeMap = userAdvanceChargeMap.get(vo.getUserId());

                Long advanceChargeAmount = advanceChargeMap.getOrDefault(tradeId, 0L);
                long after = vo.getBalance() + advanceChargeAmount - cost;
                long beforeBalance = vo.getBalance() + advanceChargeAmount;
                vo.setBalance(after);
                advanceChargeMap.remove(tradeId);
                GptConsumptionRecordVo recordVo = GptConsumptionRecordVo.builder()
                        .after(String.valueOf(after))
                        .before(String.valueOf(beforeBalance))
                        .cost(String.valueOf(cost))
                        .systemToken(modelBillingParamVo.getToken())
                        .userToken(vo.getToken())
                        .type(modelBillingParamVo.getType())
                        .op(modelBillingParamVo.getOp()).build();
                // 添加用户消费记录
                consumptionRecordManager.asyncAddGptConsumptionRecord(recordVo);
                return true; // 消费成功
            }
        }
        log.error("billing error vo={}",vo);
        return false;
    }

    @Override
    public boolean upPilling(CacheGptApiTokenVo vo, ModelBillingParamVo modelBillingParamVo, long cost) {
        log.info("upPilling vo={} cost={},modelBillingParamVo={}",vo,cost,modelBillingParamVo);
        if(vo==null || cost<0 )return false;
        Long balance = vo.getBalance();

        if( balance>0 ){
            // todo 如果多实例部署的话 这里需要改成 分布式锁
            CacheUserInfoVo cacheUserInfoVo = CacheUtil.getIfPresent(String.valueOf(vo.getUserId()));
            synchronized(cacheUserInfoVo.getClass()){
                balance = vo.getBalance();
                long after = balance + cost;
                vo.setBalance(after);
                GptConsumptionRecordVo recordVo = GptConsumptionRecordVo.builder()
                        .after(String.valueOf(after))
                        .before(String.valueOf(balance))
                        .cost(String.valueOf(cost))
                        .systemToken(modelBillingParamVo.getToken())
                        .userToken(vo.getToken())
                        .type(modelBillingParamVo.getType())
                        .op(modelBillingParamVo.getOp()).build();
                // 添加用户消费记录
                consumptionRecordManager.asyncAddGptConsumptionRecord(recordVo);
                return true; // 消费成功
            }
        }
        return false;
    }

    /**
     * 目前模型3.5 turbo-16k gpt-4 通过次数来计费
     * @return
     */
    @Override
    public String[] billingModel() {
        String[]models = new String[ModelPriceEnum.values().length];
        for (int i = 0; i < ModelPriceEnum.values().length; i++) {
            models[i] = ModelPriceEnum.values()[i].getModel();
        }
        // 这里放开所有配置的模型
        return models;
//        return new String[]{ModelPriceEnum.GPT_3_TURBO_16K.getModel(),ModelPriceEnum.GPT_4.getModel(),ModelPriceEnum.GPT_4_32K.getModel(),ModelPriceEnum.GPT_3_TURBO.getModel(),ModelPriceEnum.M_J.getModel()};
    }
}
