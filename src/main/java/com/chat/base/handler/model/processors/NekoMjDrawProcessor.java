package com.chat.base.handler.model.processors;

import com.chat.base.adapt.NekoChangeDrawAdapter;
import com.chat.base.adapt.NekoCreateDrawParamAdapter;
import com.chat.base.adapt.NekoGetDrawAdapter;
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
public class NekoMjDrawProcessor implements MjDrawProcessor {

    @Autowired
    private NekoCreateDrawParamAdapter nekoCreateDrawParamAdapter;
    @Autowired
    private NekoGetDrawAdapter nekoGetDrawAdapter;
    @Autowired
    private NekoChangeDrawAdapter nekoChangeDrawAdapter;

    @Override
    public String createDrawTask(SubmitMJVo submitMJVo,String modeId) {
        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(modeId);
        if (proxyService == null) {
            return null;
        }
        RequestBody param = nekoCreateDrawParamAdapter.convertParam(submitMJVo);
        ResponseBody result = proxyService.createMjTask(param);
        return nekoCreateDrawParamAdapter.convertResult(result);
    }

    @Override
    public String changeDraw(SubmitChangeDTO submitChangeDTO, String modelId) {
        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(modelId);
        if (proxyService == null) {
            return null;
        }
        RequestBody requestBody = nekoChangeDrawAdapter.convertParam(submitChangeDTO);
        ResponseBody responseBody = proxyService.changeMJTask(requestBody);
        return nekoChangeDrawAdapter.convertResult(responseBody);
    }

    @Override
    public QueryDrawModelResult getMjImageResultByTaskId(String taskId, String modelId) {
        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.createProxyService(modelId);
        if (proxyService == null) {
            return null;
        }
        ResponseBody result = proxyService.getMjTask(taskId);
        return nekoGetDrawAdapter.convertResult(result);
    }

    @Override
    public boolean match(String model, String baseUrl) {
        return ModelBaseUrlConstants.MJ_NEKO_API.getUrl().contains(baseUrl);
    }
}
