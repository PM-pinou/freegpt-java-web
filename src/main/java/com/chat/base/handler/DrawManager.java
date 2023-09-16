package com.chat.base.handler;

import com.alibaba.fastjson.JSONObject;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.ConsumptionConstant;
import com.chat.base.bean.constants.ModelPriceEnum;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.SubmitChangeDTO;
import com.chat.base.bean.vo.SubmitMJVo;
import com.chat.base.handler.billing.ModelBillingFactory;
import com.chat.base.handler.billing.ModelBillingService;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.handler.model.MjDrawProcessor;
import com.chat.base.handler.model.MjDrawProcessorFactory;
import com.chat.base.handler.model.bean.QueryDrawModelResult;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class DrawManager {


    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @Autowired
    private GptModelConfigManager gptModelConfigManager;

    @Autowired
    private UserManager userManager;


    public Optional<GptModelConfig> getMjModel(String model, CacheUserInfoVo cacheUserInfoVo) {
        return weightAlgorithmManager.round(cacheUserInfoVo, model);
    }

    public Optional<String> preCreateMjTask(GptModelConfig modelConfig){
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        ModelBillingService modelBillingService = ModelBillingFactory.getModelBillingService(modelConfig.getModel());
        if(modelBillingService==null){
            return Optional.of("没获取到模型");
        }
        String tradeId = UUID.randomUUID().toString();
        ResultVO<String> beforeBillingResult = modelBillingService.beforeBilling(cacheUserInfoVo.getGptApiTokenVo(), ModelPriceEnum.M_J.getInPrice(),tradeId);
        if(!beforeBillingResult.isOk()){
            return Optional.of("余额不足");
        }

        boolean billing = userManager.updateUserBalance(cacheUserInfoVo,modelConfig.getModel(),ModelPriceEnum.M_J.getInPrice(),modelConfig.getToken(), ConsumptionConstant.MJ_TYPE, CommonConstant.CONSUME,tradeId);
        if(!billing){
            return Optional.of("扣费失败，请联系管理员");
        }
        return Optional.empty();
    }



    public Optional<String> createMjTask(SubmitMJVo submitMJVo, GptModelConfig modelConfig) throws Exception {

        MjDrawProcessor mjDrawProcessor = MjDrawProcessorFactory.getMjDrawProcessorByBaseUrl(modelConfig.getBaseUrl());
        if(mjDrawProcessor==null){
            log.error("createMjTask error no processors modelConfig={}",modelConfig);
            return Optional.empty();
        }
        String resultTask = mjDrawProcessor.createDrawTask(submitMJVo, String.valueOf(modelConfig.getId()));
        if (StringUtils.isBlank(resultTask)){
            throw new Exception("调用绘画接口失败");
        }
        JSONObject jsonObject = JSONObject.parseObject(resultTask);
        String taskId = jsonObject.getString("result");
        if (StringUtils.isBlank(taskId)) {
            return Optional.empty();
        }
        return Optional.of(taskId);
    }


    public Optional<String> changeMjTask(SubmitChangeDTO submitChangeDTO, GptModelConfig config) throws Exception {

        MjDrawProcessor mjDrawProcessor = MjDrawProcessorFactory.getMjDrawProcessorByBaseUrl(config.getBaseUrl());
        if(mjDrawProcessor==null){
            log.error("createMjTask error no processors modelConfig={}",config);
            return Optional.empty();
        }
        String resultTask = mjDrawProcessor.changeDraw(submitChangeDTO, config.getId().toString());
        if (StringUtils.isBlank(resultTask)){
            throw new Exception("调用绘画接口失败");
        }
        return Optional.of(resultTask);
    }


    public QueryDrawModelResult getMjImage(String taskId, Long modelId){
        try {
            GptModelConfig config = gptModelConfigManager.queryGptModelConfigByModelId(modelId.toString());
            if (Objects.isNull(config)){
                return null;
            }
            MjDrawProcessor mjDrawProcessor = MjDrawProcessorFactory.getMjDrawProcessorByBaseUrl(config.getBaseUrl());
            if(Objects.isNull(mjDrawProcessor)){
                log.error("createMjTask error no processors modelConfig={}",config);
                return null;
            }
            QueryDrawModelResult queryDrawModelResult = mjDrawProcessor.getMjImageResultByTaskId(taskId,modelId.toString());
            return queryDrawModelResult;
        }catch (Exception e){
            log.error("getMjImage error ",e);
        }
        return null;
    }


}
