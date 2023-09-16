package com.chat.base.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("coupon_order_snapshoot")
public class CouponOrderSnapshoot implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 兑换卷号码
     */
    private String couponNo;

    /**
     * 兑换卷使用人
     */
    private Long userId;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 订单id
     */
    private Long orderId;


}
