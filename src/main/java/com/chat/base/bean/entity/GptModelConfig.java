package com.chat.base.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gpt_model_config")
public class GptModelConfig {
    private static final long serialVersionUID=1L;

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
    private LocalDateTime createTime;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 来源名称
     */
    private String name;
}
