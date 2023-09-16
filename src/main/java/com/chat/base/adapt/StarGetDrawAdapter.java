package com.chat.base.adapt;

import com.alibaba.fastjson.JSONObject;
import com.chat.base.bean.common.IMethodParamAndResultAdapter;
import com.chat.base.bean.vo.SubmitMJVo;
import com.chat.base.handler.model.bean.QueryDrawModelResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StarGetDrawAdapter implements IMethodParamAndResultAdapter<SubmitMJVo, RequestBody, ResponseBody,QueryDrawModelResult> {


    @Override
    public RequestBody convertParam(SubmitMJVo submitMJVo) {
      return null;
    }

    @Override
    public QueryDrawModelResult convertResult(ResponseBody result) {
        QueryDrawModelResult queryDrawModelResult = new QueryDrawModelResult();
        try {
            String resultTask = result.string();
            if (StringUtils.isNotBlank(resultTask)) {

                JSONObject jsonObject = JSONObject.parseObject(resultTask);
                queryDrawModelResult.setImageUrl(jsonObject.getString("imageUrl"));
                queryDrawModelResult.setProgress(jsonObject.getString("progress"));
                queryDrawModelResult.setStatus(jsonObject.getString("status"));
                return queryDrawModelResult;
            }
        } catch (Exception e) {
            log.info("StarGetDrawAdapter convertResult error result={}", JSONObject.toJSONString(result));
        }
        return queryDrawModelResult;
    }
}
