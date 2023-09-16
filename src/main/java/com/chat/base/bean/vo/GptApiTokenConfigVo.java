package com.chat.base.bean.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class GptApiTokenConfigVo implements Serializable {

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
    private Long visitNumber;

    /**
     * 余额
     */
    private Long balance;

    private String balanceStr;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 1:有效 0：无效
     */
    private Integer status;


}
