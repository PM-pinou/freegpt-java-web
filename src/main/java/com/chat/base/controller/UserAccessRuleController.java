package com.chat.base.controller;


import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.req.UserAccessRuleReq;
import com.chat.base.bean.req.UserAccessRuleUpdateReq;
import com.chat.base.handler.access.UserAccessHandler;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 用户访问规则 前端控制器
 * </p>
 *
 * @author lixin
 * @since 2023-05-10
 */
@Slf4j
@RestController
public class UserAccessRuleController extends BaseController {

    @Autowired
    private UserAccessHandler userAccessHandler;

    @PostMapping("/userAccessRule/update")
    public ResultVO updateUserAccessRule(@RequestBody @Valid UserAccessRuleUpdateReq updateReq){
        Boolean result = null;
        try {

            boolean admin = SessionUser.isAdmin();
            if(!admin){
                return ResultVO.fail("操作失败！当前登录账号不是管理账号！");
            }
            result = userAccessHandler.updateUserAccess(updateReq);
            if(result){
                return ResultVO.success("更新成功！");
            }
        }catch (Exception e){
            log.error("updateUserAccessRule updateReq={}",updateReq,e);
        }finally {
            log.info("updateUserAccessRule updateReq={},result={}",updateReq,result);
        }
        return ResultVO.fail("更新失败！出现未知错误！");
    }


    @RequestMapping("/userAccessRule/admin/queryUserAccess")
    private ResultVO<Object> queryUserAccess(@RequestBody @Valid UserAccessRuleReq req){
        log.info("queryUserAccess req = {}",req);
        try {
            return ResultVO.success(userAccessHandler.queryUserAccess(req));
        }catch (Exception e){
            log.error("queryUserAccess error req = {}",req,e);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

}

