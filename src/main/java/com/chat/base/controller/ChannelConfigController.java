package com.chat.base.controller;


import com.alibaba.fastjson.JSON;
import com.chat.base.bean.req.ChannelConfigAddReq;
import com.chat.base.bean.req.ChannelConfigReq;
import com.chat.base.bean.req.ChannelConfigDelReq;
import com.chat.base.handler.ChannelConfigManager;
import com.chat.base.utils.ResultVO;
import com.chat.base.handler.WeightAlgorithmManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class ChannelConfigController extends BaseController {

    @Autowired
    private ChannelConfigManager channelConfigManager;
    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @RequestMapping("admin/getChannelConfig")
    private ResultVO<Object> getChannelConfig(@RequestBody @Valid ChannelConfigReq req) {
        log.info("getChannelConfig req ={}", JSON.toJSONString(req));
        if(Objects.isNull(req)){
            return ResultVO.fail("req is null");
        }
        return ResultVO.success(channelConfigManager.getChannelConfig(req));
    }

    @RequestMapping("admin/addChannelConfig")
    private ResultVO<Object> addChannelConfig(@RequestBody @Valid ChannelConfigAddReq req) {
        log.info("addChannelConfig req ={}", JSON.toJSONString(req));
        if(Objects.isNull(req)){
            return ResultVO.fail("req is null");
        }
        Boolean result = channelConfigManager.addChannelConfig(req);
        if(!result){
            return ResultVO.fail("添加通道失败！");
        }
        return ResultVO.success();
    }


    @RequestMapping("admin/updateChannelConfig")
    private ResultVO<Object> updateChannelConfig(@RequestBody @Valid ChannelConfigAddReq req) {
        log.info("updateChannelConfig req ={}", JSON.toJSONString(req));
        if(Objects.isNull(req)){
            return ResultVO.fail("req is null");
        }
        Boolean result = channelConfigManager.updateChannelConfig(req);
        if(!result){
            return ResultVO.fail("更改通道失败！");
        }
        weightAlgorithmManager.initAllOnlineUserAlgorithm();
        return ResultVO.success();
    }


    @RequestMapping("admin/delChannelConfig")
    private ResultVO<Object> delChannelConfig(@RequestBody @Valid ChannelConfigDelReq delReq) {
        log.info("delChannelConfig delReq ={}", JSON.toJSONString(delReq));
        if(Objects.isNull(delReq)){
            return ResultVO.fail("delReq is null");
        }
        Boolean result = channelConfigManager.delChannelConfig(delReq);
        if(!result){
            return ResultVO.fail("删除通道失败！[通道被模型引用 或 系统异常]");
        }
        return ResultVO.success();
    }
}

