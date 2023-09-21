package com.chat.base.controller;


import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.constants.OpEnum;
import com.chat.base.bean.constants.UserLevelEnum;
import com.chat.base.bean.dto.ToEmailDto;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.req.*;
import com.chat.base.bean.vo.CacheUserInfoSecretVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.handler.UserLogManager;
import com.chat.base.handler.UserManager;
import com.chat.base.service.MailService;
import com.chat.base.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lixin
 * @since 2023-05-07
 */
@Slf4j
@RestController
public class UserInfoController extends BaseController{

    @Autowired
    private UserManager userManager;


    @Autowired
    private UserLogManager userLogManager;

    @Autowired
    public MailService mailService;

    /**
     * 获取已登录的用户信息
     * @return
     */
    @GetMapping("/userInfo/info")
    public ResultVO getUserInfo(){
        Optional<CacheUserInfoVo> userInfoVO = SessionUser.getUserInfoVO();
        if(userInfoVO.isPresent()){
            CacheUserInfoVo vo = new CacheUserInfoVo();
            BeanUtils.copyProperties(userInfoVO.get(),vo);
            vo.setGptModelConfigsMap(null);
            vo.getGptApiTokenVo().setBalanceStr(AmountUtil.getUserAmount(vo.getGptApiTokenVo().getBalance()));
            vo.setUserLevel(UserLevelEnum.getDescByUserLevel(vo.getUserLevel()));
            return ResultVO.success(vo);
        }
        return ResultVO.success();
    }


    /**
     * 更新用户信息
     * @param updateReq
     * @return
     */
    @PostMapping("/userInfo/update")
    public ResultVO update(UserUpdateReq updateReq){
        Boolean result=null;
        try {
            boolean admin = SessionUser.isAdmin();
            if(!admin){
                return ResultVO.fail("操作失败！当前登录账号不是管理账号！");
            }
            String ip = HttpUtil.getIpAddress();
            UserInfo userInfo = userManager.updateUserInfo(updateReq, ip);
            if(userInfo==null){
                return ResultVO.fail("更新用户信息失败！");
            }
            return ResultVO.success("更新成功");
        }catch (Exception e){
            log.error("update updateReq={}",updateReq,e);
        }finally {
            log.info("update updateReq={},result={}",updateReq,result);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

    /**
     * 退出登录
     * @return
     */
    @GetMapping("/userInfo/loginOut")
    public ResultVO loginOut(){
        //不管如何直接放回退出成功信息给前端
        return ResultVO.success();
    }


    /**
     * 登录
     * @param loginReq
     * @return
     */
    @PostMapping("/userInfo/login")
    public ResultVO login(@RequestBody @Valid UserInfoLoginReq loginReq, HttpServletResponse response){

        try {
            String account = loginReq.getAccount();
            if( RegexUtil.validateEmail(account) || RegexUtil.validatePhone(account)){
                String ip = HttpUtil.getIpAddress();
                CacheUserInfoVo infoVo = userManager.login(loginReq,ip);
                if(infoVo!=null){
                    userLogManager.addUserLog(loginReq.getAppName(),String.valueOf(infoVo.getId()), OpEnum.LOGIN.getOp(),ip, HttpUtil.browserName());
                    CacheUserInfoSecretVo cacheUserInfoSecretVo = new CacheUserInfoSecretVo();
                    BeanUtils.copyProperties(infoVo,cacheUserInfoSecretVo);
                    return ResultVO.success(cacheUserInfoSecretVo);
                }
                return ResultVO.fail("账号或者密码错误!");
            }
            return ResultVO.fail("账号格式不正确！");
        }catch (Exception e){
            log.error("login error loginReq={}",loginReq,e);
        }finally {
            log.info("login loginReq={}",loginReq);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

    @GetMapping("/userInfo/test")
    public ResultVO test(@Param("model")String model){
        return ResultVO.success(userManager.test(model));
    }

    /**
     * 登录
     * @param loginReq
     * @return
     */
    @PostMapping("/userInfo/admin/login")
    public ResultVO adminLogin(@RequestBody @Valid UserInfoLoginReq loginReq, HttpServletResponse response){

        try {
            String account = loginReq.getAccount();
            boolean admin = SessionUser.isAdmin(account);
            if(!admin){
                return ResultVO.fail("操作失败！当前登录账号不是管理账号！");
            }
            String ip = HttpUtil.getIpAddress();
            CacheUserInfoVo infoVo = userManager.login(loginReq,ip);
            if(infoVo!=null){
                return ResultVO.success(infoVo);
            }
        }catch (Exception e){
            log.error("adminLogin error loginReq={}",loginReq,e);
        }finally {
            log.info("adminLogin loginReq={}",loginReq);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

    /**
     * 用户注册
     * @param registerReq
     * @return
     */
    @VisitLimit(value = {LimitEnum.IP})
    @PostMapping("/userInfo/register")
    public ResultVO register(@RequestBody @Valid UserInfoRegisterReq registerReq){
        log.info("register registerReq = {}",registerReq);
        try {
            String account = registerReq.getAccount();
            if(RegexUtil.validateEmail(account)){
                String ip = HttpUtil.getIpAddress();
                String verification = CacheUtil.getVerification(registerReq.getAccount());
                if(null==verification || !verification.toLowerCase().equals( registerReq.getVerificationCode().toLowerCase())){
                    return ResultVO.fail("验证码错误!");
                }
                return userManager.register(registerReq,ip);
            }else if(RegexUtil.validatePhone(account)){
                // TODO 这里需要写短信验证码逻辑
                String ip = HttpUtil.getIpAddress();
                return userManager.register(registerReq,ip);
            }
            return ResultVO.fail("账号格式不正确！");
        }catch (Exception e){
            log.error("register error registerReq = {}",registerReq,e);
        }finally {
            log.info("register registerReq = {}",registerReq);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }


    @RequestMapping("/userInfo/admin/queryUserInfo")
    @VisitLimit(value = {LimitEnum.IP},scope = CommonConstant.NO_LOGIN_SCOPE)
    private ResultVO<Object> queryUserLog(@RequestBody @Valid UserQueryReq req){
        log.info("queryUserLog req = {}",req);
        try {
            return ResultVO.success(userManager.queryUserInfo(req));
        }catch (Exception e){
            log.error("register error req = {}",req,e);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }
    /**
     * 发送邮箱验证码
     */
    @RequestMapping("/userInfo/register/send/verification")
//    @VisitLimit(value = {LimitEnum.IP},scope = CommonConstant.NO_LOGIN_SCOPE)
    private ResultVO<Object> queryUserLog(@RequestBody @Valid UserInfoRegisterVerificationReq verificationReq){
        log.info("verificationReq verificationReq = {}",verificationReq);
        String account = verificationReq.getAccount();
        if(RegexUtil.validateEmail(account)){
            Boolean aBoolean = mailService.sendEmailVerCode(ToEmailDto.builder().tos(account).build());
            return ResultVO.success(aBoolean);
        }else if (RegexUtil.validatePhone(account)){

        }
        return ResultVO.fail("服务器繁忙!请联系作者");
    }

    @RequestMapping("/userInfo/register/send/verification/get")
    private ResultVO<Object> verification(@RequestBody UserInfoRegisterVerificationReq verificationReq){
        log.info("verificationReq verificationReq = {}",verificationReq);
        String verification = CacheUtil.getVerification(verificationReq.getAccount());
        return ResultVO.success(verification);
    }
    @RequestMapping("/userInfo/register/send/verification/getAll")
    private ResultVO<Object> verificationAll(){
        return ResultVO.success(CacheUtil.getVerificationAll());
    }

}

