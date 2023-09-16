package com.chat.base.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.MjTaskInfo;
import com.chat.base.bean.req.MjTaskInfoReq;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.MjTaskInfoVo;
import com.chat.base.bean.req.*;
import com.chat.base.service.impl.MjTaskInfoServiceImpl;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author liuzilin
 * @since 2023-08-21
 */
@Slf4j
@Component
public class DrawTaskInfoManager {


    @Autowired
    private MjTaskInfoServiceImpl mjTaskInfoService;


    public int getTodayTaskCount(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        return mjTaskInfoService.getBaseMapper().selectTodayTaskCount(startOfDay,now);
    }


    public Boolean addMjTaskInfo(String token,String taskId,String prompt,Long modelId,String orginPhoto,String orginId,Integer type,String state,String action,Integer index){
        if(StringUtils.isBlank(taskId)){
            log.info("addMjTaskInfo fail req is null req={}",taskId);
            return false;
        }
        MjTaskInfo mjTaskInfo = new MjTaskInfo();
        mjTaskInfo.setTaskId(taskId);
        mjTaskInfo.setUserId(SessionUser.getUserId());
        mjTaskInfo.setStatus(1);
        mjTaskInfo.setCreateTime(LocalDateTime.now());
        mjTaskInfo.setUseToken(token);
        mjTaskInfo.setPrompt(prompt);
        mjTaskInfo.setParentPhoto(orginPhoto);
        mjTaskInfo.setParentId(orginId);
        mjTaskInfo.setParentIndex(index);
        mjTaskInfo.setType(type);
        mjTaskInfo.setModelId(modelId);
        mjTaskInfo.setAction(action);
        mjTaskInfo.setState(state);
        return mjTaskInfoService.save(mjTaskInfo);
    }



    public Boolean updateMjTaskInfo(MjTaskInfo mjTaskInfo){
        if(Objects.isNull(mjTaskInfo) ){
            log.info("updateMjTaskInfo fail req is null req={}",mjTaskInfo);
            return false;
        }
        return mjTaskInfoService.updateById(mjTaskInfo);
    }

    public List<MjTaskInfo> queryMjTaskIInProgress(){
        QueryWrapper<MjTaskInfo> queryWrapper = new QueryWrapper<>();
        MjTaskInfo mjTaskInfo = new MjTaskInfo();
        mjTaskInfo.setStatus(1);
        queryWrapper.setEntity(mjTaskInfo);
        return mjTaskInfoService.list(queryWrapper);
    }


    public List<MjTaskInfo> queryMjTaskIInProgressByUserId(Long userId){
        QueryWrapper<MjTaskInfo> queryWrapper = new QueryWrapper<>();
        MjTaskInfo mjTaskInfo = new MjTaskInfo();
        mjTaskInfo.setStatus(1);
        mjTaskInfo.setUserId(userId);
        queryWrapper.setEntity(mjTaskInfo);
        return mjTaskInfoService.list(queryWrapper);
    }

    public IPage<MjTaskInfoVo>  queryMjTaskIInProgressByStatus(MjTaskInfoByStatusReq req){
        if (Objects.isNull(req)){
            return new Page<>();
        }
        QueryWrapper<MjTaskInfo> queryWrapper = new QueryWrapper<>();
        MjTaskInfo mjTaskInfo = new MjTaskInfo();
        mjTaskInfo.setTaskId(req.getTaskId());
        mjTaskInfo.setUserId(req.getUserId());
        queryWrapper.setEntity(mjTaskInfo);
        if(Objects.nonNull(req.getCreateTime()) && Objects.nonNull(req.getEndTime())){
            queryWrapper.between("finish_time",req.getCreateTime(),req.getEndTime());
        }
        if(CollectionUtils.isNotEmpty(req.getStatus())){
            queryWrapper.in("status",req.getStatus());
        }
        queryWrapper.orderByDesc("finish_time");
        IPage<MjTaskInfo> mjTaskInfoIPage = mjTaskInfoService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), queryWrapper);
        return  pageDtoToVo(mjTaskInfoIPage);
    }


    public List<MjTaskInfo> queryMjTask(MjTaskInfoReq req){
        QueryWrapper<MjTaskInfo> queryWrapper = new QueryWrapper<>();
        MjTaskInfo mjTaskInfo = new MjTaskInfo();
        mjTaskInfo.setStatus(req.getStatus());
        mjTaskInfo.setUseToken(req.getUseToken());
        mjTaskInfo.setTaskId(req.getTaskId());
        mjTaskInfo.setId(req.getId());
        mjTaskInfo.setUserId(req.getUserId());
        if(Objects.nonNull(req.getCreateTime()) && Objects.nonNull(req.getEndTime())){
            queryWrapper.between("create_time",req.getCreateTime(),req.getEndTime());
        }
        queryWrapper.setEntity(mjTaskInfo);
        return mjTaskInfoService.list(queryWrapper);
    }

    public MjTaskInfoVo queryMjTaskByTaskId(String taskId){
        QueryWrapper<MjTaskInfo> queryWrapper = new QueryWrapper<>();
        MjTaskInfo mjTaskInfo = new MjTaskInfo();
        mjTaskInfo.setTaskId(taskId);
        queryWrapper.setEntity(mjTaskInfo);
        MjTaskInfo mjTask = mjTaskInfoService.getOne(queryWrapper);
        return  dtoToVo(mjTask);
    }


    public ResultVO<Object> preProcess(){
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if (Objects.isNull(cacheUserInfoVo)) {
            return ResultVO.fail("没有登录请登录");
        }

        List<MjTaskInfo> mjTaskInfos = queryMjTaskIInProgressByUserId(SessionUser.getUserId());
        if (CollectionUtils.isNotEmpty(mjTaskInfos)) {
            return ResultVO.fail("您有任务正在执行,请稍后重试");
        }
        return ResultVO.success();
    }

    private MjTaskInfoVo dtoToVo(MjTaskInfo mjTaskInfo){
            MjTaskInfoVo mjTaskInfoVo = new MjTaskInfoVo();
            mjTaskInfoVo.setId(mjTaskInfo.getId());
            mjTaskInfoVo.setUserId(mjTaskInfo.getUserId());
            mjTaskInfoVo.setTaskUrl(mjTaskInfo.getTaskUrl());
            mjTaskInfoVo.setStatus(mjTaskInfo.getStatus());
            mjTaskInfoVo.setTaskId(mjTaskInfo.getTaskId());
            mjTaskInfoVo.setCreateTime(mjTaskInfo.getCreateTime());
            mjTaskInfoVo.setFinishTime(mjTaskInfo.getFinishTime());
            mjTaskInfoVo.setUpdateTime(mjTaskInfo.getUpdateTime());
            mjTaskInfoVo.setType(mjTaskInfo.getType());
            mjTaskInfoVo.setParentId(mjTaskInfo.getParentId());
            mjTaskInfoVo.setParentPhoto(mjTaskInfo.getParentPhoto());
            mjTaskInfoVo.setPrompt(mjTaskInfo.getPrompt());
            mjTaskInfoVo.setState(mjTaskInfo.getState());
            mjTaskInfoVo.setAction(mjTaskInfo.getAction());
            mjTaskInfoVo.setParentIndex(mjTaskInfo.getParentIndex());
        return mjTaskInfoVo;
    }

    private IPage<MjTaskInfoVo> pageDtoToVo( IPage<MjTaskInfo> taskInfoIPage){
        IPage<MjTaskInfoVo> iPage = new Page();

        List<MjTaskInfoVo> mjTaskInfoVos = new ArrayList<>();
        for (MjTaskInfo mjTaskInfo : taskInfoIPage.getRecords()) {
            MjTaskInfoVo mjTaskInfoVo = new MjTaskInfoVo();
            mjTaskInfoVo.setId(mjTaskInfo.getId());
            mjTaskInfoVo.setUserId(mjTaskInfo.getUserId());
            mjTaskInfoVo.setTaskUrl(mjTaskInfo.getTaskUrl());
            mjTaskInfoVo.setStatus(mjTaskInfo.getStatus());
            mjTaskInfoVo.setTaskId(mjTaskInfo.getTaskId());
            mjTaskInfoVo.setCreateTime(mjTaskInfo.getCreateTime());
            mjTaskInfoVo.setFinishTime(mjTaskInfo.getFinishTime());
            mjTaskInfoVo.setUpdateTime(mjTaskInfo.getUpdateTime());
            mjTaskInfoVo.setType(mjTaskInfo.getType());
            mjTaskInfoVo.setParentId(mjTaskInfo.getParentId());
            mjTaskInfoVo.setParentPhoto(mjTaskInfo.getParentPhoto());
            mjTaskInfoVo.setPrompt(mjTaskInfo.getPrompt());
            mjTaskInfoVo.setParentIndex(mjTaskInfo.getParentIndex());
            mjTaskInfoVo.setState(mjTaskInfo.getState());
            mjTaskInfoVo.setAction(mjTaskInfo.getAction());
            mjTaskInfoVos.add(mjTaskInfoVo);
        }
        iPage.setRecords(mjTaskInfoVos);
        iPage.setCurrent(taskInfoIPage.getCurrent());
        iPage.setTotal(taskInfoIPage.getTotal());
        iPage.setSize(taskInfoIPage.getSize());
        iPage.setPages(taskInfoIPage.getPages());

        return iPage;
    }
}
