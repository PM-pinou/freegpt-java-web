package com.chat.base.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GptConsumptionRecordVo {

    /**
     * 消费之前的值
     */
    private String before;

    /**
     * 消费之后
     */
    private String after;

    /**
     * api_config的token
     */
    private String userToken;

    /**
     * 系统渠道的token
     */
    private String systemToken;

    /**
     * 本次消费的值
     */
    private String cost;

    /**
     * 操作描述
     */
    private String op;

    private Integer type;
}
