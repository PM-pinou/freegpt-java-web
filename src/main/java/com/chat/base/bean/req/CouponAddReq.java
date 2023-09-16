package com.chat.base.bean.req;

import java.time.LocalDateTime;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-13
 */
@Data
public class CouponAddReq {

    /**
     * 兑换金额
     */
    @NotNull(message = "兑换金额不能为空")
    private Long couponAmount;

}
