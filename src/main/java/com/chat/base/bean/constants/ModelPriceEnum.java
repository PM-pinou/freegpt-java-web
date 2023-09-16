package com.chat.base.bean.constants;


import java.util.HashMap;
import java.util.Map;

/**
 * 目前系统支持的模型type
 */
public enum ModelPriceEnum {

    GPT_4( "gpt-4", 300L,600L,6000),
    GPT_4_0314( "gpt-4-0314",300L,600L,6000),
    GPT_4_0613( "gpt-4-0613", 300L,600L,6000),
    GPT_4_32K( "gpt-4-32k", 600L,1200L,8000),
    GPT_4_32K_0314( "gpt-4-32k-0314", 600L,1200L,8000),
    GPT_4_32K_0613( "gpt-4-32k-0613", 600L,1200L,8000),
    GPT_3_TURBO( "gpt-3.5-turbo", 15L,20L,3000),
    GPT_3_TURBO_0301( "gpt-3.5-turbo-0301", 15L,20L,3000),
    GPT_3_TURBO_0613( "gpt-3.5-turbo-0613", 15L,20L,3000),
    GPT_3_TURBO_16K( "gpt-3.5-turbo-16k",  30L,40L,3800),
    GPT_3_TURBO_16K_0613( "gpt-3.5-turbo-16k-0613", 30L,40L,3800),
    M_J( "Midjourney", 1L,0L,380000),//暂时免费开放
    claude( "claude-2", 15L,20L,3000);


    private String model;
    private Long inPrice;
    private Long outPrice;
    private Integer maxInTokenNumber; // 限制问题的token

    ModelPriceEnum(String model, Long inPrice,Long outPrice,Integer maxInTokenNumber) {
        this.model = model;
        this.inPrice = inPrice;
        this.outPrice = outPrice;
        this.maxInTokenNumber = maxInTokenNumber;
    }

    public final static Map<String,ModelPriceEnum> modelPriceMap = new HashMap<>();

    static {
        for (ModelPriceEnum value : values()) {
            modelPriceMap.put(value.getModel(),value);
        }
    }

    /**
     * 如果当模型不存在的时候，直接返回gpt3的计费规则
     * @param model
     * @return
     */
    public static ModelPriceEnum getModelPrice(String model){
        ModelPriceEnum modelPriceEnum = modelPriceMap.get(model);
        if(modelPriceEnum==null){
            return GPT_3_TURBO;
        }
        return modelPriceEnum;
    }


    public String getModel() {
        return model;
    }

    public Long getInPrice() {
        return inPrice;
    }

    public Long getOutPrice() {
        return outPrice;
    }

    public Integer getMaxInTokenNumber() {
        return maxInTokenNumber;
    }
}
