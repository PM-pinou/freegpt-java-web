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
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 兑换卷号码
     */
    private String couponNo;

    /**
     * 兑换卷生成人
     */
    private String account;

    /**
     * 1:未使用 0：已使用 -1:失效
     */
    private Integer status;

    /**
     * 兑换金额
     */
    private Long couponAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 兑换卷失效时间
     */
    private LocalDateTime useEndTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 兑换卷使用人
     */
    private String useAccount;


}
