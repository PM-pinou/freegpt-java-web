package com.chat.base.handler.model.processors;

import com.chat.base.adapt.StarChangeDrawAdapter;
import com.chat.base.adapt.StarCreateDrawParamAdapter;
import com.chat.base.adapt.StarGetDrawAdapter;
import com.chat.base.bean.vo.SubmitChangeDTO;
import com.chat.base.bean.vo.SubmitMJVo;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.handler.model.MjDrawProcessor;
import com.chat.base.handler.model.bean.ModelBaseUrlConstants;
import com.chat.base.handler.model.bean.QueryDrawModelResult;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StarMjDrawProcessor implements MjDrawProcessor {

    @Autowired
    private StarCreateDrawParamAdapter starCreateDrawParamAdapter;
    @Autowired
    private StarGetDrawAdapter starGetDrawAdapter;
    @Autowired
    private StarChangeDrawAdapter starChangeDrawAdapter;

    @Override
    public String createDrawTask(SubmitMJVo submitMJVo,String modeId) {
        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(modeId);
        if (proxyService == null) {
            return null;
        }
        RequestBody param = starCreateDrawParamAdapter.convertParam(submitMJVo);
        ResponseBody result = proxyService.createMjTask(param);
        return starCreateDrawParamAdapter.convertResult(result);
    }

    @Override
    public String changeDraw(SubmitChangeDTO changeDTO, String modelId) {
        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(modelId);
        if (proxyService == null) {
            return null;
        }
        RequestBody requestBody = starChangeDrawAdapter.convertParam(changeDTO);
        ResponseBody responseBody = proxyService.changeMJTask(requestBody);
        return starChangeDrawAdapter.convertResult(responseBody);
    }


    @Override
    public QueryDrawModelResult getMjImageResultByTaskId(String taskId, String modelId) {

        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(modelId);
        if (proxyService == null) {
            return null;
        }
        ResponseBody result = proxyService.getMjTask(taskId);
        return starGetDrawAdapter.convertResult(result);

    }

    @Override
    public boolean match(String model, String baseUrl) {
        return ModelBaseUrlConstants.MJ_STRATMATE_API.getUrl().contains(baseUrl);
    }
}
