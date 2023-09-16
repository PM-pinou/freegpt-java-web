package com.chat.base.bean.gpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiChatReq {

    @NotEmpty
    private String model;
    @NotEmpty
    private String prompt;
    @NotEmpty
    private String conversationId;
    @NotEmpty
    private String token;
    @NotEmpty
    private String proxyUrl;

    private double top_p = 1;

    private double temperature = 0.8;
}
