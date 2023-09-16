package com.chat.base.controller;


import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.handler.TokenChannelConfigManager;
import com.chat.base.utils.ResultVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@RestController
public class TokenChannelConfigController extends BaseController {

    @Autowired
    private TokenChannelConfigManager tokenChannelConfigManager;

    @GetMapping("/tokenChannelConfig/queryChannelIdsByToken")
    private ResultVO<Object> queryChannelIdsByToken(@Param("token")String token){
        if (StringUtils.isNotBlank(token)){
           return ResultVO.success( tokenChannelConfigManager.queryChannelIdsByToken(token));
        }
        return ResultVO.success(Collections.emptyList());
    }

}

