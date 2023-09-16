package com.chat.base.bean.req;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GptModelConfigAddReq {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 访问地址
     */
    @NotEmpty(message = "访问地址不能为空")
    private String baseUrl;

    /**
     * 访问token
     */
    @NotEmpty(message = "访问token不能为空")
    private String token;
    /**
     * 模型标识
     */
    @NotEmpty(message = "模型标识不能为空")
    private String model;

    /**
     * 名称
     */
    @NotEmpty(message = "名称不能为空")
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
     * 模型保定的通道id
     */
    private List<Long> channelIds;
}
