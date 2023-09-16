package com.chat.base.controller;

import com.chat.base.bean.constants.ModelServiceTypeConstant;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import com.chat.base.service.impl.GptModelConfigServiceImpl;
import com.chat.base.utils.ResultVO;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
public class GptBillingUsageController extends BaseController {

    @Autowired
    private GptModelConfigServiceImpl gptModelConfigService;

    public static void main(String[] args) {
        log.info("getAllTokenBillingUsage start");
        ChatBaseOpenAiProxyService proxyService =  OpenAiProxyServiceFactory.getService("sk-bY1rpBVhQWFCyQxZ44E7758e73Df4e599276F7C132CeA96a", "http://apiii.Monica.plus", ModelServiceTypeConstant.CHAT_GPT_MODEL4);

        // sk-0d3568a197a047729fbc616952ae9596
        for (int j=0;j<1;j++){
            int finalJ = j;
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 1; i++) {//9.0160
                    int finalI = i;
                    long startTime = System.currentTimeMillis();
                    try {
                        log.info(finalJ +"线程" + finalI + "开始");

                        LinkedList<ChatMessage> userChatMessages = new LinkedList<>();
                        userChatMessages.add(new ChatMessage(RoleEnum.USER.getRoleName(), "你好"));
                        proxyService.createStreamChatCompletion(ChatCompletionRequest.builder()
                                .model(ModelServiceTypeConstant.CHAT_GPT_MODEL4)
                                .messages(userChatMessages)
                                .user(UUID.randomUUID().toString())
                                .temperature(1.0)
                                .topP(1.0)
                                .stream(true)
                                .build(), System.out, null);
                    } catch (Exception e) {
//                        log.error("error",e);
                    }
                    log.info(finalJ+"线程" + finalI + "结束"+"cost="+(System.currentTimeMillis()-startTime));
                }
            });
            thread.start();
        }
        try {
            Thread.sleep(200000000);
        }catch (Exception e){

        }
    }


    /**
     * gpt的账号
     */
    @GetMapping("/admin/gpt/billing")
    public ResultVO getAllTokenBillingUsage(@RequestParam("configId") Long configId){
        Map<String,String> hashMap = new LinkedHashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String left = startDate.format(formatter);
        String right = endDate.format(formatter);
        GptModelConfig modelConfig = gptModelConfigService.getGptModelConfig(configId);
        if(modelConfig==null){
            return ResultVO.fail("暂无该配置渠道，请确定渠道id是否正确");
        }
        Map<String, List<ChatBaseOpenAiProxyService>> serviceMap = OpenAiProxyServiceFactory.getServiceMap();
        serviceMap.forEach((key, value) -> {
            for (ChatBaseOpenAiProxyService service : value) {
                if (modelConfig.getId().equals(service.getId())) {
                    log.info("getAllTokenBillingUsage token={},left={},right={}", service.token, left, right);
                    String billingUsage = service.billingUsage(left, right);
                    hashMap.put(service.token, billingUsage);
                    break;
                }
            }
        });
        return ResultVO.success(hashMap);
    }
}
