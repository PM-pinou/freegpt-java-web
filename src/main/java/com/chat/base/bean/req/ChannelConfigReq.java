package com.chat.base.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Data
public class ChannelConfigReq {


    private Integer page = 1;


    private Integer pageSize = 20;

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


}
