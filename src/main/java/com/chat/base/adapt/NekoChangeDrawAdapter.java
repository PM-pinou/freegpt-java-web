package com.chat.base.adapt;

import com.alibaba.fastjson.JSONObject;
import com.chat.base.bean.common.IMethodParamAndResultAdapter;
import com.chat.base.bean.dto.SubmitMJDto;
import com.chat.base.bean.vo.SubmitChangeDTO;
import com.chat.base.bean.vo.SubmitMJVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class NekoChangeDrawAdapter implements IMethodParamAndResultAdapter<SubmitChangeDTO, RequestBody, ResponseBody,String> {


    @Override
    public RequestBody convertParam(SubmitChangeDTO changeMJVo) {
        MediaType mediaType = MediaType.parse("application/json");
        return RequestBody.create(mediaType, JSONObject.toJSONString(changeMJVo));
    }


    @Override
    public String convertResult(ResponseBody result) {
        try {
            String resultTask = result.string();
            if (StringUtils.isNotBlank(resultTask)) {
                JSONObject jsonObject = JSONObject.parseObject(resultTask);
                String taskId = jsonObject.getString("result");
                return taskId;
            }
        } catch (Exception e) {
            log.info("convertResult error result={}", JSONObject.toJSONString(result));
        }
        return null;
    }
}
