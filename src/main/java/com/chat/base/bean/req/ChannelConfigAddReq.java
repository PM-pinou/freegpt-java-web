package com.chat.base.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Data
public class ChannelConfigAddReq {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 通道名称
     */
    @NotEmpty(message = "名称不能为空")
    private String name;

    /**
     * 通道对应模型类型
     */
    @NotEmpty(message = "模型不能为空")
    private String modelType;

    /**
     * 1:有效 0：无效
     */
    private Integer status;


}
