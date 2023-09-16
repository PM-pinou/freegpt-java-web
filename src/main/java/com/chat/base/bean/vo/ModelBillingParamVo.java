package com.chat.base.bean.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ModelBillingParamVo {

    /**
     * 模型
     */
    private String model;
    /**
     * 模型的参数
     */
    private String token;

    /**
     * 消费说明
     */
    private String op;

    private Integer type;
}
