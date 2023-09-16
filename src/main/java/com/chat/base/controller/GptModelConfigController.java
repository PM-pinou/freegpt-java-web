package com.chat.base.controller;


import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.req.GptModelConfigAddReq;
import com.chat.base.bean.req.GptModelConfigDelReq;
import com.chat.base.bean.req.GptModelReq;
import com.chat.base.bean.vo.GptModelConfigVo;
import com.chat.base.handler.GptModelConfigManager;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import com.chat.base.handler.WeightAlgorithmManager;
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
 * @author liuzilin
 * @since 2023-08-03
 */
@Slf4j
@RestController
public class GptModelConfigController extends BaseController {

    @Autowired
    private GptModelConfigManager gptModelConfigManager;
    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @PostMapping("admin/updateGptModelConfig")
    public ResultVO updateGptModelConfig(@RequestBody @Valid GptModelConfigAddReq updateReq){
        Boolean result = null;
        try {

            boolean admin = SessionUser.isAdmin();
            if(!admin){
                return ResultVO.fail("操作失败！当前登录账号不是管理账号！");
            }
            result = gptModelConfigManager.updateGptModelConfig(updateReq);
            if(result){
                // 更新所有在线用户
                // 构造入参
                GptModelConfigVo modelConfigVo = gptModelConfigManager.DtoToVO(updateReq);
                OpenAiProxyServiceFactory.updateModelService(modelConfigVo);
                weightAlgorithmManager.initAllOnlineUserAlgorithm();
                return ResultVO.success("更新成功！");
            }
        }catch (Exception e){
            log.error("updateGptModelConfig updateReq={}",updateReq,e);
        }finally {
            log.info("updateGptModelConfig updateReq={},result={}",updateReq,result);
        }
        return ResultVO.fail("更新失败！出现未知错误！");
    }

    @PostMapping("admin/addGptModelConfig")
    public ResultVO addGptModelConfig(@RequestBody @Valid GptModelConfigAddReq addReq){
        Boolean result = null;
        try {
            boolean admin = SessionUser.isAdmin();
            if(!admin){
                return ResultVO.fail("操作失败！当前登录账号不是管理账号！");
            }
            result = gptModelConfigManager.addGptModelConfig(addReq);
            if(result){
                return ResultVO.success("添加成功！");
            }
        }catch (Exception e){
            log.error("addGptModelConfig addReq={}",addReq,e);
        }finally {
            log.info("addGptModelConfig addReq={},result={}",addReq,result);
        }
        return ResultVO.fail("添加失败！出现未知错误！");
    }

    @PostMapping("admin/delGptModelConfig")
    public ResultVO delGptModelConfig(@RequestBody @Valid GptModelConfigDelReq delReq){
        Boolean result = null;
        try {
            boolean admin = SessionUser.isAdmin();
            if(!admin){
                return ResultVO.fail("操作失败！当前登录账号不是管理账号！");
            }
            if(CommonConstant.DEFAULT_CHANNEL_ID.equals(delReq.getId())){
                return ResultVO.fail("操作失败！默认渠道不允许删除！");
            }
            result = gptModelConfigManager.delGptModelConfig(delReq);
            if(result){
                // 更新所有在线用户
                weightAlgorithmManager.initAllOnlineUserAlgorithm();
                return ResultVO.success("删除成功！");
            }
        }catch (Exception e){
            log.error("delGptModelConfig addReq={}",delReq,e);
        }finally {
            log.info("delGptModelConfig addReq={},result={}",delReq,result);
        }
        return ResultVO.fail("删除失败！改模型被使用中，不能删除");
    }

    @RequestMapping("admin/queryGptModelConfig")
    @VisitLimit(value = {LimitEnum.IP},scope = CommonConstant.NO_LOGIN_SCOPE)
    private ResultVO<Object> queryGptModelConfig(@RequestBody @Valid GptModelReq req){
        log.info("queryGptModelConfig req = {}",req);
        try {
            return ResultVO.success(gptModelConfigManager.queryGptModelConfig(req));
        }catch (Exception e){
            log.error("queryGptModelConfig error req = {}",req,e);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

}

