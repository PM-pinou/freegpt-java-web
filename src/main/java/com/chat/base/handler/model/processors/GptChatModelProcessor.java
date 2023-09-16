package com.chat.base.handler.model.processors;

import com.alibaba.fastjson2.JSONObject;
import com.chat.base.bean.common.BaseCodeEnum;
import com.chat.base.bean.vo.ChatMessageResultVo;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.handler.model.ChatModelProcessor;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import com.chat.base.utils.TokenUtil;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 适配所有的gpt模型的服务
 */
@Slf4j
@Component
public class GptChatModelProcessor implements ChatModelProcessor {


    private ChatCompletionChunk getChunk(String chunk){
        try {
            return JSONObject.parseObject(chunk, ChatCompletionChunk.class);
        }catch (Exception e){
            log.error("getChunk chunk="+chunk+", exception= {}",e);
        }
        return null;
    }


    @Override
    public ChatMessageResultVo chatStream(ChatCompletionRequest chatCompletionRequest, OutputStream os, String userToken, String modeId) throws IOException {

        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(modeId);
        if (proxyService == null) {
            os.write((String.format("data: %s\n\n", BaseCodeEnum.NO_MODEL.getMsg())).getBytes(Charset.defaultCharset()));
            return null;
        }

        chatCompletionRequest.setStream(true);
        chatCompletionRequest.setN(1);

        List<ChatMessage> messages = chatCompletionRequest.getMessages();
        int promptTokenNumber = TokenUtil.countTokenMessages(messages, chatCompletionRequest.getModel());
        List<ChatCompletionChunk> chunks = new ArrayList<>();
        try {
            proxyService.streamApiChatCompletion(chatCompletionRequest).doOnError(Throwable::printStackTrace).blockingForEach(chunk -> {
                try {
                    os.write((String.format("data: %s\n\n", chunk)).getBytes(Charset.defaultCharset()));
                    os.flush();
                    ChatCompletionChunk chatCompletionChunk = getChunk(chunk);
                    if(chatCompletionChunk!=null){
                        chunks.add(chatCompletionChunk);
                    }
                }catch (ClientAbortException e){
                    log.info("api用户主动断开连接");
                }catch (Exception e) {
                    log.error("回答报错 token={},url={},chunks.size={}",proxyService.getToken(), proxyService.getBASE_URL(),chunks.size(),e);
                }
            });
        }catch (Exception e) {
            log.error("answer failed token={},url={},chunks.size={}",proxyService.getToken(), proxyService.getBASE_URL(),chunks.size(),e);
        }
        ChatMessage chatMessage = new ChatMessage(RoleEnum.ASSISTANT.getRoleName(), chunks.stream()
                .flatMap(chunk -> chunk.getChoices().stream())
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .filter(Objects::nonNull)
                .collect(Collectors.joining()));

        int relyTokenNumber = TokenUtil.countTokenText(chatMessage.getContent(), chatCompletionRequest.getModel());

        return ChatMessageResultVo.builder()
                .chatContent(chatMessage.getContent())
                .chatMessage(chatMessage)
                .content(messages.get(messages.size()-1).getContent())
                .model(chatCompletionRequest.getModel())
                .user(chatCompletionRequest.getUser())
                .promptTokenNumber(promptTokenNumber)
                .relyTokenNumber(relyTokenNumber)
                .source("api-chat")
                .systemToken(proxyService.getToken())
                .userToken(userToken)
                .build();
    }

    @Override
    public boolean match(String model, String baseUrl) {
        return model.contains("gpt");
    }
}
