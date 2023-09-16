package com.chat.base.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class ConsumptionRecordsVo implements Serializable {

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
     * api_config的token
     */
    private String userToken;

    /**
     * 本次消费的值
     */
    private String cost;

    private String op;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
