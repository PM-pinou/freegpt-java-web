package com.chat.base.controller;


import com.alibaba.fastjson.JSON;
import com.chat.base.bean.req.ChannelModelReq;
import com.chat.base.handler.ChannelConfigManager;
import com.chat.base.handler.ChannelModelConfigManager;
import com.chat.base.handler.GptModelConfigManager;
import com.chat.base.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

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
public class ChannelModelConfigController extends BaseController {

    @Autowired
    private ChannelModelConfigManager channelModelConfigManager;

    @Autowired
    private GptModelConfigManager gptModelConfigManager;

    @RequestMapping("/channelModelConfig/addChannelModelConfig")
    private ResultVO<Object> addChannelModelConfig(@RequestBody @Valid ChannelModelReq req){
        if (Objects.isNull(req)){
            return ResultVO.fail("添加通道模型失败，对象为空");
        }
        log.info("addChannelModelConfig req = {}", JSON.toJSONString(req));
        // 判断模式是否还是有效
        boolean checkModel = gptModelConfigManager.checkModelIsAffect(req.getModelConfigId());
        if (!checkModel){
            return ResultVO.fail("添加通道模型失败，模型数据异常");
        }
        Boolean result = channelModelConfigManager.addChannelModelConfig(req);
        log.info("addChannelModelConfig req ={} , result = {}",JSON.toJSONString(req),result);
        return ResultVO.success("添加通道模型成功");
    }

    @RequestMapping("/channelModelConfig/delChannelModelConfig")
    private ResultVO<Object> delChannelModelConfig(@RequestBody @Valid ChannelModelReq req){
        if (Objects.isNull(req)){
            return ResultVO.fail("删除通道模型失败，对象为空");
        }
        log.info("delChannelModelConfig req = {}", JSON.toJSONString(req));

        Boolean result = channelModelConfigManager.delChannelModelConfig(req);
        log.info("delChannelModelConfig req ={} , result = {}",JSON.toJSONString(req),result);
        return ResultVO.success("删除通道模型成功");
    }
}

