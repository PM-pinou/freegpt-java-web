package com.chat.base.adapt;

import com.alibaba.fastjson.JSONObject;
import com.chat.base.bean.common.IMethodParamAndResultAdapter;
import com.chat.base.bean.dto.SubmitMJDto;
import com.chat.base.bean.vo.SubmitMJVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class NekoCreateDrawParamAdapter implements IMethodParamAndResultAdapter<SubmitMJVo, RequestBody, ResponseBody,String> {


    @Override
    public RequestBody convertParam(SubmitMJVo submitMJVo) {
        MediaType mediaType = MediaType.parse("application/json");
        SubmitMJDto submitMJDto = new SubmitMJDto();
        submitMJDto.setPrompt(submitMJVo.getPrompt().trim());
        submitMJDto.setNotifyHook(submitMJVo.getNotifyHook());
        if (StringUtils.isNotBlank(submitMJVo.getFileName())) {
            submitMJDto.setBase64Array(Collections.singletonList(submitMJVo.getFileName()));
        }
        submitMJDto.setState(submitMJVo.getState());
        return RequestBody.create(mediaType, JSONObject.toJSONString(submitMJDto));
    }

    @Override
    public String convertResult(ResponseBody result) {
        try {
            String resultTask = result.string();
            if (StringUtils.isNotBlank(resultTask)) {
                return resultTask;
            }
        } catch (Exception e) {
            log.info("convertResult error result={}", JSONObject.toJSONString(result));
        }
        return null;
    }
}
