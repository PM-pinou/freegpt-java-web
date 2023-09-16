package com.chat.base.controller;


import com.chat.base.bean.common.BaseCodeEnum;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.ConsumptionConstant;
import com.chat.base.bean.constants.DrawEnum;
import com.chat.base.bean.constants.ModelPriceEnum;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.entity.MjTaskInfo;
import com.chat.base.bean.req.MjTaskInfoByStatusReq;
import com.chat.base.bean.req.MjTaskInfoReq;
import com.chat.base.bean.req.MjTaskInfoUpdateReq;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.ChangeMJVo;
import com.chat.base.bean.vo.SubmitChangeDTO;
import com.chat.base.bean.vo.SubmitMJVo;
import com.chat.base.handler.DrawManager;
import com.chat.base.handler.DrawTaskInfoManager;
import com.chat.base.handler.GptModelConfigManager;
import com.chat.base.handler.UserManager;
import com.chat.base.handler.model.bean.QueryDrawModelResult;
import com.chat.base.utils.ImageUtil;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-22
 */
@RestController
@Slf4j
public class MjTaskInfoController extends BaseController{

    private static final String NO_MODEL_PERMISSION_MSG = "没有该模型的使用权限,请联系管理员";
    private static final String MODEL_EXCEPTION_MSG = "MJ模型异常,请联系管理员";
    private static final String TASK_CREATION_FAILED_MSG = "任务生成失败!";
    private static final String NO_RELATED_TASK_MSG = "没有相关任务!!";

    @Autowired
    private UserManager userManager;

    @Autowired
    private DrawTaskInfoManager drawTaskInfoManager;

    @Autowired
    private GptModelConfigManager gptModelConfigManager;

    @Autowired
    private DrawManager drawManager;

    @PostMapping("/draw/createMjTask")
    public ResultVO<Object> createMjTask(@RequestBody @Valid SubmitMJVo submitMJVo) {
        HashMap<String, String> taskMap = new HashMap<>();
        String taskId = "";
        log.info("createMjImageTask submitDTO={}", submitMJVo);
        try {
            //预校验流程
            ResultVO<Object> resultVO = drawTaskInfoManager.preProcess();
            if (resultVO.getCode() != BaseCodeEnum.SUCCESS.getCode()) {
                return resultVO;
            }
            // 获取模型配置 获取不到抛异常
            Optional<GptModelConfig> modelConfig = drawManager.getMjModel(submitMJVo.getModel(), SessionUser.get());
            if (!modelConfig.isPresent()){
                return ResultVO.fail(NO_MODEL_PERMISSION_MSG);
            }
            GptModelConfig config = modelConfig.get();
            // 垫图转base64
            String orginPhoto = processFileName(submitMJVo);
            // 扣费校验
            Optional<String> preResult = drawManager.preCreateMjTask(config);
            if(preResult.isPresent()){
                return ResultVO.fail(preResult.get());
            }
            // 创建任务 创建失败抛异常
            taskId = createMjTask(submitMJVo, config);
            if(StringUtils.isBlank(taskId)){
               return ResultVO.fail("生成任务失败");
            }
            drawTaskInfoManager.addMjTaskInfo(config.getToken(),taskId,submitMJVo.getPrompt(), config.getId(), orginPhoto,"", DrawEnum.IMAGINE.getCode(),submitMJVo.getState(),"",0);
            taskMap.put("taskId", taskId);
            return ResultVO.success(taskMap);
        } catch (Exception e) {
            log.error("Error occurred while creating MJ image task", e);
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 前端可以根据这个接口获取单个任务的结果
     * @param taskId
     * @return
     */
    @GetMapping("/draw/getMjTask")
    public ResultVO getMjImageResultByTaskId(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return ResultVO.fail("taskId不为空!!");
        }

        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if (Objects.isNull(cacheUserInfoVo)) {
            return ResultVO.fail("没有登录请登录");
        }

        HashMap<String, String> taskMap = new HashMap<>();

        MjTaskInfoReq mjTaskInfoReq = new MjTaskInfoReq();
        mjTaskInfoReq.setTaskId(taskId);
        List<MjTaskInfo> taskInfos = drawTaskInfoManager.queryMjTask(mjTaskInfoReq);

        if (CollectionUtils.isEmpty(taskInfos)) {
            return ResultVO.fail("没有相关任务!!");
        }

        QueryDrawModelResult result = drawManager.getMjImage(taskId, taskInfos.get(0).getModelId());
        if (Objects.isNull(result)){
           return ResultVO.fail("查询不到相关任务!!");
        }
        log.info("getMjImageResultByTaskId taskId={}, taskResult={}", taskId, result);
        taskMap.put("status", StringUtils.defaultString(result.getStatus()));
        taskMap.put("progress", StringUtils.defaultString(result.getProgress()));
        if (StringUtils.isNotEmpty(result.getImageUrl())) {
            taskMap.put("imageUrl", StringUtils.defaultString(result.getImageUrl()));
        }
        return ResultVO.success(taskMap);
    }

    /**
     * @param changeMJVo
     * @return
     */
    @PostMapping("/draw/getMjTask/change")
    public ResultVO<Object> getChangeMjImageResultByTaskId(@RequestBody @Valid ChangeMJVo changeMJVo) {
        ResultVO<Object> resultVO = drawTaskInfoManager.preProcess();
        if (resultVO.getCode() != BaseCodeEnum.SUCCESS.getCode()) {
            return resultVO;
        }

        List<MjTaskInfo> taskInfos = getTaskInfos(changeMJVo);
        if (CollectionUtils.isEmpty(taskInfos)) {
            return ResultVO.fail(NO_RELATED_TASK_MSG);
        }

        HashMap<String, String> taskMap = new HashMap<>();
        taskMap.put("taskId", "");
        try {
            // 获取模型配置 获取不到抛异常
            GptModelConfig config = gptModelConfigManager.queryGptModelConfigByModelId(taskInfos.get(0).getModelId().toString());
            if (config == null){
                return ResultVO.fail(NO_MODEL_PERMISSION_MSG);
            }
            // 扣费校验
            Optional<String> preResult = drawManager.preCreateMjTask(config);
            if(preResult.isPresent()){
                return ResultVO.fail(preResult.get());
            }
            SubmitChangeDTO submitDTO = createSubmitDTO(changeMJVo);
            Optional<String> changeResult = drawManager.changeMjTask(submitDTO,config);
            if (StringUtils.isBlank(changeResult.orElse(""))) {
                return ResultVO.success(taskMap);
            }
            processTaskResult(taskMap, changeResult, changeMJVo, taskInfos);
        } catch (Exception e) {
            log.error("getChangeMjImageByTaskId error taskId={}", changeMJVo.getTaskId(), e);
        }
        return ResultVO.success(taskMap);
    }

    /**
     * q
     */
    @RequestMapping("/draw/queryTaskInfo")
    public ResultVO<Object> queryTaskInfo(@RequestBody @Valid MjTaskInfoReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("查询失败");
        }
        return ResultVO.success(drawTaskInfoManager.queryMjTask(req));

    }


    @GetMapping("/draw/queryTaskInfoByTaskId")
    public ResultVO<Object> queryTaskInfo(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return ResultVO.fail("taskId 为空");
        }
        return ResultVO.success(drawTaskInfoManager.queryMjTaskByTaskId(taskId));

    }

    /**
     * 前端直接根据状态获取单个用户的所有图片结果
     * @param req
     * @return
     */
    @PostMapping("/draw/queryDrawTaskInfo")
    public ResultVO<Object> queryDrawTaskInfo(@RequestBody @Valid MjTaskInfoByStatusReq req) {
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if (Objects.isNull(req)){
            return ResultVO.success();
        }
        if (Objects.isNull(cacheUserInfoVo)) {
            return ResultVO.fail("没有登录请登录");
        }
        req.setUserId(SessionUser.getUserId());
        return ResultVO.success(drawTaskInfoManager.queryMjTaskIInProgressByStatus(req));
    }

    /**
     * 运营后台搜索
     * @param req
     * @return
     */
    @PostMapping("admin/draw/queryDrawTaskInfo")
    public ResultVO<Object> adminQueryDrawTaskInfo(@RequestBody @Valid MjTaskInfoByStatusReq req) {
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if (Objects.isNull(req)){
            return ResultVO.success();
        }

        if (Objects.isNull(cacheUserInfoVo)) {
            return ResultVO.fail("没有登录请登录");
        }
        return ResultVO.success(drawTaskInfoManager.queryMjTaskIInProgressByStatus(req));
    }

    @PostMapping("/draw/updateTaskInfoStatus")
    public ResultVO<Object> updateTaskInfoStatus(@RequestBody @Valid MjTaskInfoUpdateReq req) {
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if (Objects.isNull(req.getId()) || Objects.isNull(req.getStatus())){
            return ResultVO.fail("id 或 status 为空");
        }
        if (Objects.isNull(cacheUserInfoVo)) {
            return ResultVO.fail("没有登录请登录");
        }

        MjTaskInfo mjTaskInfo = new MjTaskInfo();
        mjTaskInfo.setId(req.getId());
        mjTaskInfo.setStatus(req.getStatus());
        return ResultVO.success(drawTaskInfoManager.updateMjTaskInfo(mjTaskInfo));
    }


    /**
     * 图片转成base64
     * @param submitMJVo
     * @return
     */
    private String processFileName(SubmitMJVo submitMJVo) {
        String orginPhoto = "";
        if (StringUtils.isNotBlank(submitMJVo.getFileName())) {
            orginPhoto = submitMJVo.getFileName();
            String base64Str = "data:image/jpeg;base64," + ImageUtil.convertImageToBase64Str(submitMJVo.getFileName());
            submitMJVo.setFileName(base64Str);
        }
        return orginPhoto;
    }



    /**
     * 创建任务
     * @param submitMJVo
     * @param config
     * @return
     * @throws Exception
     */
    private String createMjTask(SubmitMJVo submitMJVo, GptModelConfig config){
        try {
            Optional<String> createMjTaskResult = drawManager.createMjTask(submitMJVo, config);
            if(createMjTaskResult.isPresent() && StringUtils.isNotEmpty(createMjTaskResult.get())){
                log.info("createMjTask success submitMjVo={},taskId={}",submitMJVo.getPrompt(),createMjTaskResult.get());
                return createMjTaskResult.get();
            }
        }catch (Exception e){
            log.error("createMjTask error submitMjVo={}",submitMJVo,e);
        }
        boolean result = userManager.updateUserBalance(SessionUser.get(), ModelPriceEnum.M_J.getModel(), ModelPriceEnum.M_J.getInPrice(), "mj-token",
                ConsumptionConstant.MJ_TYPE, CommonConstant.RETURN_MONEY, UUID.randomUUID().toString());
        log.info("createMjTask return money result={},prompt={}",result,submitMJVo.getPrompt());
        return null;
    }

    /**
     * 查询任务
     * @param changeMJVo
     * @return
     */
    private List<MjTaskInfo> getTaskInfos(ChangeMJVo changeMJVo) {
        MjTaskInfoReq mjTaskInfoReq = new MjTaskInfoReq();
        mjTaskInfoReq.setTaskId(changeMJVo.getTaskId());
        return drawTaskInfoManager.queryMjTask(mjTaskInfoReq);
    }

    private SubmitChangeDTO createSubmitDTO(ChangeMJVo changeMJVo) {
        SubmitChangeDTO submitDTO = new SubmitChangeDTO();
        submitDTO.setIndex(changeMJVo.getIndex());
        submitDTO.setTaskId(String.valueOf(changeMJVo.getTaskId()));
        submitDTO.setAction(changeMJVo.getAction());
        submitDTO.setNotifyHook(changeMJVo.getNotifyHook());
        submitDTO.setState(changeMJVo.getState());
        return submitDTO;
    }

    /**
     * 生成
     * @param taskMap
     * @param createMjTaskResult
     * @param changeMJVo
     * @param taskInfos
     */
    private void processTaskResult(HashMap<String, String> taskMap, Optional<String> changeResult, ChangeMJVo changeMJVo, List<MjTaskInfo> taskInfos) {
        if (changeResult.isPresent()) {
            String taskId = changeResult.get();
            taskMap.put("taskId", taskId);
            Integer action = DrawEnum.getDescByUserLevel(changeMJVo.getAction());
            drawTaskInfoManager.addMjTaskInfo(taskInfos.get(0).getUseToken(),taskId, "",taskInfos.get(0).getModelId(),"",changeMJVo.getTaskId(), action
                    ,changeMJVo.getState(),changeMJVo.getAction(),changeMJVo.getIndex());
        }
    }

}

