package com.chat.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.entity.PromptRecord;
import com.chat.base.bean.req.PromptRecordReq;
import com.chat.base.bean.req.PromptRecordUserReq;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.PromptRecordResult;
import com.chat.base.bean.vo.PromptRecordVo;
import com.chat.base.handler.PromptRecordManager;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lixin
 * @since 2023-05-17
 */
@RestController
@Slf4j
public class PromptRecordController extends BaseController {

    @Autowired
    private PromptRecordManager promptRecordManager;

    @RequestMapping("admin/queryPromptRecord")
    private ResultVO<IPage<PromptRecordVo>> queryPromptRecord(@RequestBody @Valid PromptRecordReq req){
        log.info("queryPromptRecordByToken req = {}",req);
        try {
            IPage<PromptRecord> recordIPage = promptRecordManager.queryPromptRecord(req);
            IPage<PromptRecordVo> recordIPageVo = new Page<>();
            recordIPageVo.setTotal(recordIPage.getTotal());
            recordIPageVo.setRecords(recordIPage.getRecords().stream().map(PromptRecordVo::entityToVo).collect(Collectors.toList()));
            return ResultVO.success(recordIPageVo);
        }catch (Exception e){
            log.error("register error req = {}",req,e);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

    @RequestMapping("/user/queryPromptRecordByToken")
    private ResultVO<PromptRecordResult> queryPromptRecordByToken(@RequestBody @Valid PromptRecordUserReq req){
        log.info("queryPromptRecordByToken req = {}",req);
        try {
            Optional<CacheUserInfoVo> userInfoVO = SessionUser.getUserInfoVO();
            if (userInfoVO.isPresent()) {
                CacheUserInfoVo cacheUserInfoVo = userInfoVO.get();
                String token = cacheUserInfoVo.getGptApiTokenVo().getToken();
                req.setToken(token);
                PromptRecordResult result = promptRecordManager.queUserPromptRecord(req);
                return ResultVO.success(result);
            }
        }catch (Exception e){
            log.error("register error req = {}",req,e);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }


}

