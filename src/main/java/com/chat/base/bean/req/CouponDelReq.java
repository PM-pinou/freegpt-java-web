package com.chat.base.bean.req;

import com.chat.base.bean.annotation.Trimmed;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-13
 */
@Data
public class CouponDelReq {

    /**
     * 消费卷id
     */
    private Long id;

    /**
     * 兑换卷生成人
     */
    private String account;

    /**
     * 兑换卷号码
     */
    @Trimmed
    private String couponNo;

    /**
     * 兑换金额
     */
    private Long couponAmount;

}
