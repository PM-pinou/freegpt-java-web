package com.chat.base.handler.billing;

import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.ModelBillingParamVo;
import com.chat.base.utils.ResultVO;

/**
 * 目前都下先写死入参，后期业务复杂了可以改动 泛型
 */
public interface ModelBillingService {


    /**
     *  付费的前置校验
     * @param vo
     * @param cost
     * @return
     */
    ResultVO<String> beforeBilling(CacheGptApiTokenVo vo, long cost,String tradeId);


    /**
     * 计费
     * 如果扣费失败了 都会将相应的额度置为 0
     * @param vo
     * @param cost
     * @return
     */
    boolean billing(CacheGptApiTokenVo vo, ModelBillingParamVo modelBillingParamVo, long cost,String tradeId);


    /**
     * 退费
     * @param vo
     * @param cost
     * @return
     */
    boolean upPilling(CacheGptApiTokenVo vo, ModelBillingParamVo modelBillingParamVo, long cost);

    /**
     * 计费模型
     * @return
     */
    String[] billingModel();

}
