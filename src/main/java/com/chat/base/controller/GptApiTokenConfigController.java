package com.chat.base.controller;


import com.alibaba.fastjson.JSON;
import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.req.GptApiTokenConfigAddReq;
import com.chat.base.bean.req.GptApiTokenConfigReq;
import com.chat.base.handler.GptApiTokenConfigManager;
import com.chat.base.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@RestController
@Slf4j
public class GptApiTokenConfigController extends BaseController {

    @Autowired
    private GptApiTokenConfigManager gptApiTokenConfigManager;

    @RequestMapping("/gptApiTokenConfig/getGptAilTokenConfig")
    private ResultVO<Object> getGptAilTokenConfig(@RequestBody @Valid GptApiTokenConfigReq req) {
        log.info("getGptAilTokenConfig req ={}", JSON.toJSONString(req));
        if(Objects.isNull(req)){
            return ResultVO.fail("req is null");
        }
        return ResultVO.success(gptApiTokenConfigManager.queryGptApiTokens(req));
    }


    @RequestMapping("/gptApiTokenConfig/updateGptAilTokenConfig")
    private ResultVO<Object> updateGptAilTokenConfig(@RequestBody @Valid GptApiTokenConfigAddReq req) {
        log.info("updateGptAilTokenConfig req ={}", JSON.toJSONString(req));
        if(Objects.isNull(req)){
            return ResultVO.fail("req is null");
        }
        Boolean result = gptApiTokenConfigManager.updateGptApiToken(req);
        if(result){
            return ResultVO.success("更新成功！");
        }
        return ResultVO.fail("更新失败");
    }

}

