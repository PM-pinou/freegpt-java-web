package com.chat.base.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("channel_config")
public class ChannelConfig implements Serializable {

    private static final long serialVersionUID=1L;

    @JsonSerialize(using = ToStringSerializer.class)
      private Long id;

    /**
     * 通道名称
     */
    private String name;

    /**
     * 通道对应模型类型
     */
    private String modelType;

    /**
     * 1:有效 0：无效
     */
    private Integer status;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;


}
