package com.chat.base.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Data
public class GptApiTokenConfigReq  {


    private Integer page = 1;


    private Integer pageSize = 20;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 用户账号
     */
    private String account;
    /**
     * token名称
     */
    private String token;



    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 1:有效 0：无效
     */
    private Integer status;


}
