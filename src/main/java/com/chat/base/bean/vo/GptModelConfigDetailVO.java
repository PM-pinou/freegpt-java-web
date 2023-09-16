package com.chat.base.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GptModelConfigDetailVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
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
     * 权重
     */
    private Integer weight;
    /**
     * 状态 1： 开启 ， 0： 禁用
     */
    private Integer status;
    /**
     * 新增时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 来源名称
     */
    private String name;

    /**
     * 模型保定的通道id
     */
    private List<String> channelIds;
}
