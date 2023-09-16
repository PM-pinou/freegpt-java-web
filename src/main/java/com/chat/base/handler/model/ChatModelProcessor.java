package com.chat.base.handler.model;

import com.chat.base.bean.vo.ChatMessageResultVo;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;

import java.io.IOException;
import java.io.OutputStream;

public interface ChatModelProcessor {

    /**
     * gpt聊天
     * @param chatCompletionRequest
     * @param os
     * @param userToken
     * @param modelId
     * @return
     */
    ChatMessageResultVo chatStream(ChatCompletionRequest chatCompletionRequest, OutputStream os, String userToken, String modelId) throws IOException;

    /**
     * 模型匹配规则
     * @param model
     * @param baseUrl
     * @return
     */
    boolean match(String model,String baseUrl);


    /**
     * 越小的越优先
     * @return
     */
    default int order(){
        return -1;
    }

}
