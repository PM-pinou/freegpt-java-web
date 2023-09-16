package com.chat.base.bean.req;

import com.chat.base.bean.annotation.Trimmed;
import lombok.Data;

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
public class CouponReq {

    /**
     * 兑换金额
     */
    private Long couponAmount;

    /**
     * 使用状态
     */
    private Integer status;

    /**
     * 兑换卷生成人
     */
    private String account;

    /**
     * 兑换卷使用人
     */
    private String useAccount;

    /**
     * 兑换卷号码
     */
    @Trimmed
    private String couponNo;

    private Integer  page = 1;

    private Integer pageSize = 10;
}
