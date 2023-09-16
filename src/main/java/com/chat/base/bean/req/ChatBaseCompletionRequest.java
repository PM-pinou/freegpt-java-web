package com.chat.base.bean.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatBaseCompletionRequest {

    private String model;

    private List<ChatMessage> messages;

    private double top_p = 1;

    private double temperature = 0.8;

    private Integer n = 1;

    private Boolean stream = true;

    private List<String> stop;

    private Integer max_tokens;

    private Double presence_penalty;

    private Double frequency_penalty;

    private Map<String, Integer> logit_bias;

    private String user = RoleEnum.USER.getRoleName();
}
