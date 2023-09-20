package com.chat.base.controller;

import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import com.chat.base.service.impl.GptModelConfigServiceImpl;
import com.chat.base.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
public class GptBillingUsageController extends BaseController {

    @Autowired
    private GptModelConfigServiceImpl gptModelConfigService;


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
        Map<String, ChatBaseOpenAiProxyService> serviceMap = OpenAiProxyServiceFactory.getServiceMap();

        for (ChatBaseOpenAiProxyService value : serviceMap.values()) {
            if (modelConfig.getId().equals(value.getId())) {
                log.info("getAllTokenBillingUsage token={},left={},right={}", value.token, left, right);
                String billingUsage = value.billingUsage(left, right);
                hashMap.put(value.token, billingUsage);
                break;
            }
        }
        return ResultVO.success(hashMap);
    }
}
