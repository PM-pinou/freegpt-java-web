package com.chat.base.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
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
public class CacheGptApiTokenVo implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * token名称
     */
    private String name;

    private String token;

    /**
     * 通道集合
     */
    private List<String> channelIds;

    /**
     * 访问次数
     */
    private volatile Long visitNumber;

    /**
     * 余额
     */
    private volatile long balance;

    /**
     * 余额str
     */
    private String balanceStr;

    /**
     * 1:有效 0：无效
     */
    private Integer status;

}
