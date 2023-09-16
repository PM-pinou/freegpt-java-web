package com.chat.base.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GptModelConfigVo {

    // 模型的id
    private Long id;

    /**
     * 模型标识
     */
    private String model;

    /**
     * 请求的token
     */
    private String token;

    /**
     * 请求的地址
     */
    private String baseUrl;

    /**
     * 权重
     */
    private Integer weight = 1;

    /**
     * 来源名称
     */
    private String name;

}
