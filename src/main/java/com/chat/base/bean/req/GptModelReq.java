package com.chat.base.bean.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GptModelReq {

    private Long id;
    /**
     * 访问地址
     */
    private String baseUrl;
    /**
     * 访问token
     */
    private String token;
    /**
     * 模型标识
     */
    private String model;
    /**
     * 模型名称
     */
    private String name;
    /**
     * 权重
     */
    private Integer weight;
    /**
     * 状态 1： 开启 ， 0： 禁用
     */
    private Integer status;
    /**
     * 创建开始时间
     */
    private LocalDateTime createTime;
    /**
     * 创建结束时间
     */
    private LocalDateTime endTime;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 更新开始时间
     */
    private LocalDateTime updateCreateTime;
    /**
     * 更新结束时间
     */
    private LocalDateTime updateEndTime;
    /**
     * 更新人
     */
    private String updateUser;
}
