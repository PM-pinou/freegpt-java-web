package com.chat.base.handler;

import com.chat.base.bean.common.BaseCodeEnum;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.gpt.ChatReq;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.ChatMessageResultVo;
import com.chat.base.handler.billing.ModelBillingFactory;
import com.chat.base.handler.billing.ModelBillingService;
import com.chat.base.handler.billing.impl.ModelBillingByBalanceImpl;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.handler.model.ChatModelProcessor;
import com.chat.base.handler.model.ChatModelProcessorFactory;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import com.chat.base.utils.ChatMessageCacheUtil;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.TokenUtil;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

/**
 * @author huyd
 * @date 2023/5/23 8:06 PM
 */
@Slf4j
@Component
public class AIChatManger {

    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @Autowired
    private UserManager userManager;


    @Autowired
    private ModelBillingByBalanceImpl modelBillingByBalance;

    @Autowired
    private PromptRecordManager promptRecordManager;


    /**
     * 用户没有登录 只记录 不计费
     * @param chatReq
     * @param response
     * @throws Exception
     */
    public void streamChatWithWebV3NoStatus(ChatReq chatReq, HttpServletResponse response) throws Exception {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        String model = chatReq.getModel();
        if (StringUtils.isNoneEmpty(model) &&  !"gpt-3.5-turbo".equals(model)){
            response.getOutputStream().write(BaseCodeEnum.NO_MODEL.getMsg().getBytes());
            response.getOutputStream().close();
            return;
        }

        Integer contentNumber = chatReq.getContentNumber();
        String user = chatReq.getConversationId();

        LinkedList<ChatMessage> userChatMessages = ChatMessageCacheUtil.getUserChatMessages(user, contentNumber);
        userChatMessages.add(new ChatMessage(RoleEnum.USER.getRoleName(), chatReq.getPrompt()));

        ChatMessageCacheUtil.getOkUserChatMessages(userChatMessages, model);
        if(userChatMessages.size()<=0){
            response.getOutputStream().write(BaseCodeEnum.TOKEN_OVER.getMsg().getBytes());
            response.getOutputStream().close();
            return;
        }
        userChatMessages.addFirst(new ChatMessage(RoleEnum.SYSTEM.getRoleName(), chatReq.getSystemMessage()));

        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService("1");
        if (proxyService == null) {
            response.getOutputStream().write(BaseCodeEnum.NO_MODEL.getMsg().getBytes());
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
                .build(), response.getOutputStream(), CommonConstant.SYS_TOKEN);

        if(streamChatCompletion!=null){
            ChatMessageCacheUtil.saveChatMessage(user,streamChatCompletion.getChatMessage());
            promptRecordManager.asyncAddPromptRecord(streamChatCompletion); // 用户的回话id 也是 gpt的api的用户身份
        }
    }


    /**
     * 应用系统 聊天
     * @param chatReq
     * @param cacheUserInfoVo
     * @param response
     * @throws IOException
     */
    public void chatStream(ChatReq chatReq, CacheUserInfoVo cacheUserInfoVo, HttpServletResponse response) throws IOException {

        String model = chatReq.getModel();
        ModelBillingService modelBillingService = ModelBillingFactory.getModelBillingService(model);
        if(modelBillingService==null){
            response.getOutputStream().write(BaseCodeEnum.MODEL_NO_OPEN.getMsg().getBytes());
            return;
        }
        //交易id
        String tradeId = UUID.randomUUID().toString();
        Integer contentNumber = chatReq.getContentNumber();
        String user = chatReq.getConversationId();

        LinkedList<ChatMessage> userChatMessages = ChatMessageCacheUtil.getUserChatMessages(user, contentNumber);
        userChatMessages.add(new ChatMessage(RoleEnum.USER.getRoleName(), chatReq.getPrompt()));
        ChatMessageCacheUtil.getOkUserChatMessages(userChatMessages, model);
        if(userChatMessages.size()<=0){
            response.getOutputStream().write(BaseCodeEnum.TOKEN_OVER.getMsg().getBytes());
            response.getOutputStream().close();
            return;
        }
        userChatMessages.addFirst(new ChatMessage(RoleEnum.SYSTEM.getRoleName(), chatReq.getSystemMessage()));

        String token = cacheUserInfoVo.getGptApiTokenVo().getToken();
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

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

        try {
            int tokenMessages = TokenUtil.countTokenMessages(userChatMessages, model);
            ResultVO<String> beforeBillingResult = modelBillingService.beforeBilling(cacheUserInfoVo.getGptApiTokenVo(), tokenMessages,tradeId);
            if(!beforeBillingResult.isOk()){
                response.getOutputStream().write(beforeBillingResult.getMsg().getBytes());
                return;
            }
            ChatMessageResultVo streamChatCompletion = proxyService.createStreamChatCompletion(ChatCompletionRequest.builder()
                    .model(model)
                    .messages(userChatMessages)
                    .user(user)
                    .temperature(chatReq.getTemperature())
                    .topP(chatReq.getTop_p())
                    .stream(true)
                    .build(), response.getOutputStream(), token);

            //更新用户余额
            boolean billingResult = userManager.costUserBalanceByChat(cacheUserInfoVo, streamChatCompletion, tradeId);
            if(!billingResult){
                Long advanceChargeAmount = modelBillingByBalance.getUserAdvanceChargeMap(cacheUserInfoVo.getId()).getOrDefault(tradeId, 0L);
                log.info("扣款失败 返回预扣款给用户  cacheUserInfoVo={}", cacheUserInfoVo);
                synchronized (cacheUserInfoVo.getClass()){
                    CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
                    gptApiTokenVo.setBalance(gptApiTokenVo.getBalance()+advanceChargeAmount);
                }
            }
            if(streamChatCompletion!=null){
                ChatMessageCacheUtil.saveChatMessage(user,streamChatCompletion.getChatMessage());
                promptRecordManager.asyncAddPromptRecord(streamChatCompletion); // 用户的回话id 也是 gpt的api的用户身份
            }
        }catch (Exception e){
            Long advanceChargeAmount = modelBillingByBalance.getUserAdvanceChargeMap(cacheUserInfoVo.getId()).getOrDefault(tradeId, 0L);
            log.error("回话实现错误，现在返回预扣款给用户 cacheUserInfoVo={}", cacheUserInfoVo,e);
            synchronized (cacheUserInfoVo.getClass()){
                CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
                gptApiTokenVo.setBalance(gptApiTokenVo.getBalance()+advanceChargeAmount);
            }
        } finally {
            modelBillingByBalance.removeUserTradeId(cacheUserInfoVo.getId(),tradeId);
        }
    }


    /**
     * 官方的接口
     *
     * @param chatReq
     * @param cacheUserInfoVo
     * @throws IOException
     */
    public void chatStream(ChatCompletionRequest chatReq, CacheUserInfoVo cacheUserInfoVo, HttpServletResponse response) throws IOException {
        String model = chatReq.getModel();
        ModelBillingService modelBillingService = ModelBillingFactory.getModelBillingService(model);
        if(modelBillingService==null){
            return;
        }
        //交易id
        String tradeId = UUID.randomUUID().toString();

        String token = cacheUserInfoVo.getGptApiTokenVo().getToken();

        Optional<GptModelConfig> modelConfig = weightAlgorithmManager.round(cacheUserInfoVo, model);
        if (!modelConfig.isPresent()) {
            response.getOutputStream().write((String.format("data: %s\n\n", BaseCodeEnum.NO_MODEL_ROLE.getMsg())).getBytes(Charset.defaultCharset()));
            return;
        }
        GptModelConfig gptModelConfig = modelConfig.get();

        try {
            ResultVO<String> beforeBillingResult = modelBillingService.beforeBilling(cacheUserInfoVo.getGptApiTokenVo(),
                    TokenUtil.countTokenMessages(chatReq.getMessages(), model),tradeId);
            if(!beforeBillingResult.isOk()){
                response.getOutputStream().write((String.format("data: %s\n\n", beforeBillingResult.getMsg())).getBytes(Charset.defaultCharset()));
                return;
            }
            ChatModelProcessor chatModelProcessor = ChatModelProcessorFactory.getChatModelProcessor(model);

            assert chatModelProcessor != null;
            ChatMessageResultVo streamChatCompletion = chatModelProcessor.chatStream(chatReq, response.getOutputStream(), token,gptModelConfig.getId().toString());

            //更新用户余额
            boolean billingResult = userManager.costUserBalanceByChat(cacheUserInfoVo, streamChatCompletion, tradeId);
            if(!billingResult){
                Long advanceChargeAmount = modelBillingByBalance.getUserAdvanceChargeMap(cacheUserInfoVo.getId()).getOrDefault(tradeId, 0L);
                log.info("扣款失败 返回预扣款给用户  cacheUserInfoVo={}", cacheUserInfoVo);
                synchronized (cacheUserInfoVo.getClass()){
                    CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
                    gptApiTokenVo.setBalance(gptApiTokenVo.getBalance()+advanceChargeAmount);
                }
            }
            promptRecordManager.asyncAddPromptRecord(streamChatCompletion); // 用户的回话id 也是 gpt的api的用户身份
        }catch (Exception e){
            Long advanceChargeAmount = modelBillingByBalance.getUserAdvanceChargeMap(cacheUserInfoVo.getId()).getOrDefault(tradeId, 0L);
            log.error("回话实现错误，现在返回预扣款给用户 cacheUserInfoVo={}", cacheUserInfoVo,e);
            synchronized (cacheUserInfoVo.getClass()){
                CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
                gptApiTokenVo.setBalance(gptApiTokenVo.getBalance()+advanceChargeAmount);
            }
        } finally {
            modelBillingByBalance.removeUserTradeId(cacheUserInfoVo.getId(),tradeId);
        }

    }



}
