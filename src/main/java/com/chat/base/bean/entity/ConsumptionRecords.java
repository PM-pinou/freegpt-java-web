package com.chat.base.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户的消费日志
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("consumption_records")
public class ConsumptionRecords implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 消费之前的值
     */
    private String costBefore;

    /**
     * 消费之前
     */
    private String costAfter;

    /**
     * 系统渠道的token
     */
    private String systemToken;

    /**
     * api_config的token
     */
    private String userToken;

    /**
     * 本次消费的值
     */
    private String cost;

    private String op;

    private LocalDateTime createTime;

    /**
     * 消费记录类型
        1: 会话
        2:兑换码生成
        3:绘画
     */
    private Integer type;

    private String bizId;


}
