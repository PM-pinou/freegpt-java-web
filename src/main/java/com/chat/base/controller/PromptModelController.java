package com.chat.base.controller;


import com.chat.base.bean.vo.PromptModelVo;
import com.chat.base.handler.PromptModelManager;
import com.chat.base.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
public class PromptModelController extends BaseController {

    @Autowired
    private PromptModelManager promptModelManager;

    @GetMapping("/prompt/getPromptGroup")
    public ResultVO<List<PromptModelVo>> getPromptGroup() {
        try {
            List<PromptModelVo> modelVos = promptModelManager.getPromptGroup();
            return ResultVO.success(modelVos);
        } catch (Exception e) {
            log.error("getPromptGroup error",e);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

}

