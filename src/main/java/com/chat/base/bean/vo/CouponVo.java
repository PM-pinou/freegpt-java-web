package com.chat.base.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @since 2023-08-13
 */
@Data
public class CouponVo{


    /**
     * 兑换卷号码
     */
    private String couponNo;

    /**
     * 兑换卷生成人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String account;

    /**
     * 兑换卷使用人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String useAccount;
    /**
     * 1:未使用 0：已使用 -1:失效
     */
    private Integer status;

    /**
     * 兑换金额
     */
    private String couponAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 兑换卷失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useEndTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
