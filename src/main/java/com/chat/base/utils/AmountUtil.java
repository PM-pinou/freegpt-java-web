package com.chat.base.utils;

import com.chat.base.bean.constants.CommonConstant;

import java.math.BigDecimal;

public class AmountUtil {

    /**
     * 创建用户的默认金额
     */
    public static final int  DEFAULT_AMOUNT =  2000000;// 0.2美元

    /**
     * 系统的金额 换算比例
     * @param cost
     * @return
     */
    public static String getTokenAmount(Long cost){
        if(cost==null || cost==0 )return "0";
        BigDecimal amount = new BigDecimal(String.valueOf(cost));
        BigDecimal quotient = amount.divide(CommonConstant.multipleAmount, CommonConstant.multipleAmountDigit, BigDecimal.ROUND_HALF_UP);
        return quotient.toString();
    }


    /**
     * 展示给用户看的额度，只保留前两位
     * @param cost
     * @return
     */
    public static String getUserAmount(Long cost){
        if(cost==null || cost==0 ) return "0";
        BigDecimal amount = new BigDecimal(String.valueOf(cost));
        BigDecimal quotient = amount.divide(CommonConstant.multipleAmount, 4, BigDecimal.ROUND_HALF_UP);
        return quotient.toString();
    }

    /**
     * 展示给用户看的额度，只保留前两位
     * @param cost
     * @return
     */
    public static String getUserRMBAmount(Long cost){
        if(cost==null || cost==0 ) return "0";
        BigDecimal amount = new BigDecimal(String.valueOf(cost));
        BigDecimal multipliedAmount = amount.multiply(new BigDecimal("7.22"));
        BigDecimal quotient = multipliedAmount.divide(CommonConstant.multipleAmount, 4, BigDecimal.ROUND_HALF_UP);
        return quotient.toString();
    }

}
