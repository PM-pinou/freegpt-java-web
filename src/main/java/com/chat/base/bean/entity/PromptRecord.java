package com.chat.base.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author lixin
 * @since 2023-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("prompt_record")
public class PromptRecord implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    private String token;

    private String prompt;

    private String relyText;

    private String source;

    private String serviceType;

    private String conversationId;

    /**
     * 回复的token
     */
    private Integer relyToken;

    /**
     * prompt的token
     */
    private Integer promptToken;

    /**
     * 消耗的费用
     */
    private Long cost;

    private LocalDateTime createTime;

    private String sourceToken;

}
