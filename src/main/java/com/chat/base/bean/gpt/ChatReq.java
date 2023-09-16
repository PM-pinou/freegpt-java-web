package com.chat.base.bean.gpt;

import com.chat.base.bean.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReq {

    @NotEmpty(message = "传入的模型不能为空")
    private String model;

    @NotEmpty(message = "提示词不能为空")
    private String prompt;

    @NotEmpty(message = "回话ID不能为空")
    private String conversationId;

    private String userId;

    private String appName;

    /**
     * gpt的默认设置
     */
    private String systemMessage =  "You are ChatGPT, a large language model trained by OpenAI. Follow the user's instructions carefully. Respond using markdown.";

    private double top_p = 1;

    private double temperature = 0.8;

    /**
     * 上下文的条数
     */
    private Integer contentNumber = CommonConstant.CONTENT_NUMBER;

}
