package com.chat.base.bean.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.ModelPriceEnum;
import com.chat.base.bean.entity.PromptRecord;
import com.chat.base.utils.AmountUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author lixin
 * @since 2023-05-17
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromptRecordVo implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    private String token;

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
     * 消耗的费用 需要转为美元为计算单位
     */
    private String cost;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String sourceToken;


    public static PromptRecordVo entityToVo(PromptRecord entity){
        Long cost = entity.getCost();
        String costStr = AmountUtil.getTokenAmount(cost);
        return PromptRecordVo.builder()
                .serviceType(entity.getServiceType())
                .source(entity.getSource())
                .sourceToken(entity.getSourceToken())
                .promptToken(entity.getPromptToken())
                .relyToken(entity.getRelyToken())
                .createTime(entity.getCreateTime())
                .id(entity.getId())
                .token(entity.getToken())
                .sourceToken(entity.getSourceToken())
                .cost(costStr).build();
    }

    public static PromptRecordVo entityUserToVo(PromptRecord entity){
        Long cost = entity.getCost();
        String costStr = AmountUtil.getTokenAmount(cost);
        return PromptRecordVo.builder()
                .serviceType(entity.getServiceType().replace("gpt","model"))
                .source(entity.getSource())
                .promptToken(entity.getPromptToken())
                .relyToken(entity.getRelyToken())
                .createTime(entity.getCreateTime())
                .id(entity.getId())
                .token(entity.getToken())
                .cost(costStr).build();
    }

}
