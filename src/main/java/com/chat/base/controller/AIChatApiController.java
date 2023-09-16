package com.chat.base.controller;

import com.chat.base.bean.common.BaseCodeEnum;
import com.chat.base.bean.entity.GptApiTokenConfig;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.req.ChatBaseCompletionRequest;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.handler.AIChatManger;
import com.chat.base.handler.GptApiTokenConfigManager;
import com.chat.base.handler.UserManager;
import com.chat.base.utils.CacheUtil;
import com.chat.base.utils.HttpUtil;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


/**
 * 用于平台api的转发
 * 支持平台内部用户token转发 也可以用户外部转发
 */
@Slf4j
@RestController
public class AIChatApiController {


    @Autowired
    private GptApiTokenConfigManager gptApiTokenConfigManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private AIChatManger aiChatManger;


    /**
     * 转发gpt
     * @param req
     * @param request
     * @param response
     * @throws IOException
     */
    @CrossOrigin
    @PostMapping(value = "/v1/chat/completions",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void api(@Valid @RequestBody ChatBaseCompletionRequest req, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Authorization");
        if(StringUtils.isEmpty(token)){
            response.getOutputStream().write(BaseCodeEnum.NO_TOKEN_PARAM.getMsg().getBytes());
            return ;
        }
        List<ChatMessage> messages = req.getMessages();
        if(CollectionUtils.isEmpty(messages)){
            response.getOutputStream().write(BaseCodeEnum.NO_CHAT_MESSAGE.getMsg().getBytes());
            return ;
        }

        token = token.replace("Bearer","").trim();
        GptApiTokenConfig configByToken = gptApiTokenConfigManager.getGptApiTokenConfigByToken(token);
        if(configByToken==null){
            response.getOutputStream().write(BaseCodeEnum.NO_TOKEN.getMsg().getBytes());
            return ;
        }
        CacheUserInfoVo cacheUserInfoVo = CacheUtil.getIfPresent(String.valueOf(configByToken.getUserId()));
        if (cacheUserInfoVo == null) {
            String ip = HttpUtil.getIpAddress();
            //用户还未登陆
            UserInfo userInfo = userManager.queryUserInfoById(configByToken.getUserId());
            cacheUserInfoVo = userManager.getCacheUserInfoVoByUserByUserBean(userInfo, ip);
        }
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        BeanUtils.copyProperties(req,chatCompletionRequest);
        chatCompletionRequest.setTopP(req.getTop_p());
        chatCompletionRequest.setMaxTokens(req.getMax_tokens());
        chatCompletionRequest.setPresencePenalty(req.getPresence_penalty());
        chatCompletionRequest.setFrequencyPenalty(req.getFrequency_penalty());
        chatCompletionRequest.setLogitBias(req.getLogit_bias());
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        aiChatManger.chatStream(chatCompletionRequest,cacheUserInfoVo,response);
    }

//    /**
//     * 转发gpt
//     * @param req
//     * @param request
//     * @param response
//     * @throws IOException
//     */
//    @PostMapping(value = "/v1/chat/completions",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter api(@Valid @RequestBody ChatBaseCompletionRequest req, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SseEmitter emitter = new SseEmitter();
//        String token = request.getHeader("Authorization");
//        if(StringUtils.isEmpty(token)){
//            response.getOutputStream().write(BaseCodeEnum.NO_TOKEN_PARAM.getMsg().getBytes());
//            return emitter;
//        }
//        token = token.replace("Bearer","").trim();
//        GptApiTokenConfig configByToken = gptApiTokenConfigManager.getGptApiTokenConfigByToken(token);
//        if(configByToken==null){
//            response.getOutputStream().write(BaseCodeEnum.NO_TOKEN.getMsg().getBytes());
//            return emitter;
//        }
//        CacheUserInfoVo cacheUserInfoVo = null;
//        String sessionUser = CacheUtil.getIfPresent(String.valueOf(configByToken.getUserId()));
//        log.info("LoginInterceptor sessionUser ={}", sessionUser);
//        if (sessionUser != null) {
//            cacheUserInfoVo = JSONObject.parseObject(sessionUser, CacheUserInfoVo.class);
//            CacheUtil.put(String.valueOf(configByToken.getUserId()), sessionUser);
//        }else{
//            String ip = HttpUtil.getIpAddress();
//            //用户还未登陆
//            UserInfo userInfo = userManager.queryUserInfoById(configByToken.getUserId());
//            cacheUserInfoVo = userManager.getCacheUserInfoVoByUserByUserBean(userInfo, ip);
//            CacheUtil.put(String.valueOf(configByToken.getUserId()), JSONObject.toJSONString(cacheUserInfoVo));
//        }
//        SessionUser.setSessionUserInfo(cacheUserInfoVo);
//        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
//        BeanUtils.copyProperties(req,chatCompletionRequest);
//        log.info("api ={}",req);
//        chatCompletionRequest.setTopP(req.getTop_p());
//        chatCompletionRequest.setMaxTokens(req.getMax_tokens());
//        chatCompletionRequest.setPresencePenalty(req.getPresence_penalty());
//        chatCompletionRequest.setFrequencyPenalty(req.getFrequency_penalty());
//        chatCompletionRequest.setLogitBias(req.getLogit_bias());
//        response.setContentType("text/event-stream");
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Cache-Control", "no-cache");
//        openAIManger.streamChatWithWebV3(chatCompletionRequest,cacheUserInfoVo,emitter);
//        return emitter;
//    }

}
