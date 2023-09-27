package com.chat.base.controller;

import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.common.BaseCodeEnum;
import com.chat.base.bean.constants.*;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.vo.*;
import com.chat.base.bean.entity.PromptModel;
import com.chat.base.bean.gpt.ApiChatReq;
import com.chat.base.bean.gpt.ChatReq;
import com.chat.base.bean.req.CompletionReq;
import com.chat.base.handler.*;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import com.chat.base.utils.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.enums.RoleEnum;
import io.github.asleepyfish.exception.ChatGPTException;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author huyd
 * @date 2023/5/5 11:19 PM
 */
@Slf4j
@RestController
public class AIChatController extends BaseController {


    @Autowired
    private UserLogManager userLogManager;

    @Autowired
    private AIChatManger AIChatManger;

    @Autowired
    private PromptModelManager promptModelManager;


    @Autowired
    private DrawTaskInfoManager drawTaskInfoManager;

    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @Value("${file-token-path}")
    private String mjTokenPath;

    private static Cache<String, ChatBaseOpenAiProxyService> cache = CacheBuilder.newBuilder().initialCapacity(10).maximumSize(1000).expireAfterWrite(1000, TimeUnit.SECONDS).build();


    @VisitLimit(value = {LimitEnum.IP}, scope = CommonConstant.NO_LOGIN_SCOPE)
    @PostMapping("/chat/streamChatWithWeb/V3")
    public void streamChatWithWebV3(@RequestBody @Valid ChatReq chatReq, HttpServletResponse response) throws Exception {
        String ip = HttpUtil.getIpAddress();
        String browserName = HttpUtil.browserName();
        Long id = SessionUser.getUserId();
        String conversationId = chatReq.getConversationId();
        String userId = id == null ? conversationId : String.valueOf(id);

        ModelPriceEnum modelPriceEnum = ModelPriceEnum.modelPriceMap.get(chatReq.getModel());
        if (modelPriceEnum == null) {
            response.getOutputStream().write(BaseCodeEnum.MODEL_NO_OPEN.getMsg().getBytes());
            return;
        }

        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        try {
            if (Objects.nonNull(cacheUserInfoVo) && Objects.nonNull(cacheUserInfoVo.getGptApiTokenVo())) {
                AIChatManger.chatStream(chatReq, cacheUserInfoVo, response);
            } else {
                AIChatManger.streamChatWithWebV3NoStatus(chatReq, response);
            }
        } catch (ChatGPTException e) {
            // 用户主动停掉回答
            log.error("streamChatWithWebV3 user error chatReq={} ", chatReq, e);
        } catch (Exception e) {
            log.error("streamChatWithWebV3 error chatReq={} ", chatReq, e);
            userLogManager.addUserLog(chatReq.getAppName(), userId, OpEnum.GPT3.getOp(), ip, browserName);
            response.getOutputStream().write(BaseCodeEnum.SERVER_BUSY.getMsg().getBytes());
        } finally {
            response.getOutputStream().close();
        }
    }

    /**
     * 验证gpt的token效果
     *
     * @param chatReq
     * @param response
     * @throws Exception
     */
    @PostMapping("/chat/streamChatWithWeb/api/chat")
    public void streamChatWithApiChatWeb(@RequestBody @Valid ApiChatReq chatReq, HttpServletResponse response) throws Exception {
        String ip = HttpUtil.getIpAddress();
        String browserName = HttpUtil.browserName();
        String uid = chatReq.getToken();
        try {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            String model = StringUtils.isNoneEmpty(chatReq.getModel()) ? chatReq.getModel() : "gpt-3.5-turbo";

            ChatBaseOpenAiProxyService proxyService = cache.get(chatReq.getToken() + model, () ->
                    OpenAiProxyServiceFactory.getService(chatReq.getToken(), chatReq.getProxyUrl(), model));

            Integer contentNumber = CommonConstant.CONTENT_NUMBER;
            String user = chatReq.getConversationId();

            LinkedList<ChatMessage> userChatMessages = ChatMessageCacheUtil.getUserChatMessages(user, contentNumber);
            userChatMessages.add(new ChatMessage(RoleEnum.USER.getRoleName(), chatReq.getPrompt()));
            ChatMessageCacheUtil.getOkUserChatMessages(userChatMessages, model);
            if (userChatMessages.size() <= 0) {
                response.getOutputStream().write(BaseCodeEnum.TOKEN_OVER.getMsg().getBytes());
                response.getOutputStream().close();
                return;
            }

            ChatMessageResultVo streamChatCompletion = proxyService.createStreamChatCompletion(ChatCompletionRequest.builder()
                    .model(model)
                    .messages(userChatMessages)
                    .user(user)
                    .temperature(chatReq.getTemperature())
                    .topP(chatReq.getTop_p())
                    .stream(true)
                    .build(), response.getOutputStream(), uid);
            if(streamChatCompletion!=null){
                ChatMessageCacheUtil.saveChatMessage(user,streamChatCompletion.getChatMessage());
            }

        } catch (ChatGPTException e) {
            // 用户主动停掉回答
            log.error("streamChatWithWebV3 user error chatReq={} ", chatReq, e);
            response.getOutputStream().write(BaseCodeEnum.TERMINATE.getMsg().getBytes());
        } catch (Exception e) {
            log.error("streamChatWithWebV3 error chatReq={} ", chatReq, e);
            userLogManager.addUserLog("BlueCatApiChat", uid, OpEnum.GPT3.getOp(), ip, browserName);
            response.getOutputStream().write(BaseCodeEnum.SERVER_BUSY.getMsg().getBytes());
        } finally {
            response.getOutputStream().close();
        }
    }

    @PostMapping("/chat/streamChatWithWeb/completion")
    public void completion(@RequestBody @Validated CompletionReq completionReq, HttpServletResponse response) throws IOException {

        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if (cacheUserInfoVo == null) {
            response.getOutputStream().write("请登录之后再使用！".getBytes());
            return;
        }
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        StringBuilder builder = new StringBuilder();
        PromptModel prompt = promptModelManager.getPromptById(Long.parseLong(completionReq.getModelId()));
        if (prompt == null || StringUtils.isBlank(prompt.getContent())) {
            response.getOutputStream().write("模板已过期，请联系管理员".getBytes());
            return;
        }
        builder.append(prompt.getContent()).append("\n");
        builder.append(completionReq.getContent());
        String uid = UUID.randomUUID().toString();
        String model = StringUtils.isNoneEmpty(completionReq.getModel()) ? completionReq.getModel() : "gpt-3.5-turbo";

        Optional<GptModelConfig> modelConfig = weightAlgorithmManager.round(cacheUserInfoVo, model);
        if (!modelConfig.isPresent()) {
            response.getOutputStream().write(BaseCodeEnum.NO_MODEL_ROLE.getMsg().getBytes());
            return;
        }

        GptModelConfig gptModelConfig = modelConfig.get();
        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(gptModelConfig.getId().toString());
        if (proxyService == null) {
            response.getOutputStream().write(BaseCodeEnum.NO_MODEL.getMsg().getBytes());
            response.getOutputStream().close();
            return;
        }

        LinkedList<ChatMessage> userChatMessages = new LinkedList<>();
        userChatMessages.add(new ChatMessage(RoleEnum.USER.getRoleName(), builder.toString()));

        proxyService.createStreamChatCompletion(ChatCompletionRequest.builder()
                .model(model)
                .messages(userChatMessages)
                .user(uid)
                .temperature(1.0)
                .topP(1.0)
                .stream(true)
                .build(), response.getOutputStream(), cacheUserInfoVo.getGptApiTokenVo().getToken());
    }

}
