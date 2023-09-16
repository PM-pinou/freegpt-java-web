package com.chat.base.bean.vo;

import com.chat.base.bean.entity.GptModelConfig;
import lombok.Data;

import java.util.List;

@Data
public class CacheGptModelConfigVo {

    private List<GptModelConfig> gptModelConfigs;


    /**
     * 当前权重
     */
    private Integer currentWeight;

    /**
     * 当前下标
     */
    private Integer currentIndex;

    /**
     * 总模型数
     */
    private Integer totalModel;

    /**
     * 最大权重
     */
    private Integer maxWeight;

    /**
     * 最大公约数
     */
    private Integer gcdWeight;

}

