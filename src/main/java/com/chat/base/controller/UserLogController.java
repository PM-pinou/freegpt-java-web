package com.chat.base.controller;


import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.req.UserLogReq;
import com.chat.base.handler.UserLogManager;
import com.chat.base.utils.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lixin
 * @since 2023-06-01
 */
@RestController
public class UserLogController extends BaseController {

    @Autowired
    private UserLogManager userLogManager;

    @RequestMapping("/admin/queryUserLog")
    private ResultVO<Object> queryUserLog(@RequestBody @Valid UserLogReq req){
        return  ResultVO.success(userLogManager.queryUserLog(req));
    }

    @GetMapping("/admin/queryOnlineUser")
    private ResultVO<Object> queryOnlineUser(){
        return  ResultVO.success(userLogManager.queryOnlineUserInfo());
    }


    @GetMapping("/admin/queryUserData")
    private  ResultVO<Object> queryUserData(){
    return ResultVO.success(userLogManager.queryUserDataInfo());
    }
}

