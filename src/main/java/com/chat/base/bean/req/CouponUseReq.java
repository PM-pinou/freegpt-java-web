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
public class CouponUseReq {
    /**
     * 兑换码
     */
    @NotBlank(message = "兑换码不能为空")
    @Trimmed
    private String couponNo;
}
