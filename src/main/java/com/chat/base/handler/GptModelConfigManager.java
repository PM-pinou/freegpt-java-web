package com.chat.base.handler;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.req.GptModelConfigDelReq;
import com.chat.base.bean.vo.GptModelConfigDetailVO;
import com.chat.base.bean.vo.GptModelConfigVo;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.service.impl.ChannelModelConfigServiceImpl;
import com.chat.base.service.impl.GptModelConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.bean.req.GptModelConfigAddReq;
import com.chat.base.bean.req.GptModelReq;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author liuzilin
 * @since 2023-08-03
 */
@Slf4j
@Component
public class GptModelConfigManager {

    @Autowired
    private GptModelConfigServiceImpl gptModelConfigService;

    @Autowired
    private ChannelModelConfigServiceImpl channelModelConfigService;

    @Autowired
    private GptModelConfigManager gptModelConfigManager;

    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    /**
     * 获取所有有效的token
     * @return
     */
    public List<GptModelConfigVo> getAllValidGptConfig(){
        List<GptModelConfig> gptModelConfigs = gptModelConfigService.getBaseMapper().getAllValidGptConfig();
        return gptModelConfigs.stream().map(e ->
                GptModelConfigVo.builder()
                .baseUrl(e.getBaseUrl())
                .model(e.getModel())
                .token(e.getToken())
                .id(e.getId())
                .name(e.getName())
                .weight(e.getWeight()).build()).collect(Collectors.toList());
    }

    public List<GptModelConfig> queryGptModelConfigByModelIds(List<String> ids){
        return gptModelConfigService.listByIds(ids);
    }

    public GptModelConfig queryGptModelConfigByModelId(String modelId){
        return gptModelConfigService.getById(modelId);
    }

    public IPage<GptModelConfigDetailVO> queryGptModelConfig(GptModelReq req){
        try {
            GptModelConfig gptModelConfig = new GptModelConfig();
            gptModelConfig.setWeight(req.getWeight());
            gptModelConfig.setModel(req.getModel());
            gptModelConfig.setName(req.getName());
            gptModelConfig.setId(req.getId());
            gptModelConfig.setStatus(req.getStatus());
            gptModelConfig.setToken(req.getToken());
            QueryWrapper<GptModelConfig> queryWrapper = new QueryWrapper<>();
            if (req.getCreateTime() !=null && req.getEndTime() !=null){
                queryWrapper.between("create_time",req.getCreateTime(),req.getEndTime());
            }
            if (req.getUpdateCreateTime() !=null && req.getUpdateEndTime() !=null){
                queryWrapper.between("create_time",req.getUpdateCreateTime(),req.getUpdateEndTime());
            }
            queryWrapper.orderByDesc("create_time");
            queryWrapper.setEntity(gptModelConfig);
            IPage<GptModelConfig> gptModelConfigIPage = gptModelConfigService.queryEntitiesWithPagination(1, 50, queryWrapper);
            return PageDtoToVO(gptModelConfigIPage);

        }catch (Exception e){
            log.error("addSystemLog error req={}",req,e);
        }
        return null;
    }



    private GptModelConfig updateReqToEntity(GptModelConfigAddReq updateReq){
        GptModelConfig entity = new GptModelConfig();
        entity.setId(updateReq.getId());
        entity.setBaseUrl(updateReq.getBaseUrl());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdateUser(SessionUser.getAccount());
        entity.setModel(updateReq.getModel());
        entity.setStatus(updateReq.getStatus());
        entity.setToken(updateReq.getToken());
        entity.setName(updateReq.getName());
        entity.setWeight(updateReq.getWeight());
        return entity;
    }



    private GptModelConfig addReqToEntity(GptModelConfigAddReq addReq){
        GptModelConfig entity = new GptModelConfig();
        entity.setBaseUrl(addReq.getBaseUrl());
        entity.setCreateTime(LocalDateTime.now());
        entity.setCreateUser(SessionUser.getAccount());
        entity.setModel(addReq.getModel());
        entity.setToken(addReq.getToken());
        entity.setName(addReq.getName());
        entity.setWeight(addReq.getWeight());
        return entity;
    }

    /**
     * 更新
     * @param updateReq
     * @return
     */
    @Transactional
    public boolean updateGptModelConfig(GptModelConfigAddReq updateReq){
        GptModelConfig entity = updateReqToEntity(updateReq);
        entity.setUpdateUser(SessionUser.getAccount());
        entity.setUpdateTime(LocalDateTime.now());
        channelModelConfigService.removeByModelIds(updateReq.getId());
        channelModelConfigService.insertChannelIds(updateReq.getId(),updateReq.getChannelIds());
        return gptModelConfigService.updateBatchById(Collections.singletonList(entity));
    }


    /**
     * 添加
     * @param addReq
     * @return
     */
    @Transactional
    public boolean addGptModelConfig(GptModelConfigAddReq addReq){
        GptModelConfig entity = addReqToEntity(addReq);
        boolean saveResult = gptModelConfigService.save(entity);
        Boolean insertResult = channelModelConfigService.insertChannelIds(entity.getId(), addReq.getChannelIds());

        if (saveResult && insertResult){
            addReq.setId(entity.getId());
            GptModelConfigVo modelConfigVo = gptModelConfigManager.DtoToVO(addReq);
            OpenAiProxyServiceFactory.addModelService(modelConfigVo);
            weightAlgorithmManager.initAllOnlineUserAlgorithm();
            return true;
        }else {
            throw new RuntimeException("rollBalk");
        }
    }

    /**
     * 删除
     * @param delReq
     * @return
     */
    public boolean delGptModelConfig(GptModelConfigDelReq delReq){
        // 先判断是否有在引用
        Boolean isUsing = channelModelConfigService.isBandingChannel(delReq.getId());
        if(isUsing){
            return false;
        }
        //没有则可以删除
        return gptModelConfigService.removeById(delReq.getId());
    }


    /**
     * 判断模型是否生效
     */
    public boolean checkModelIsAffect(List<Long> modelIds){
        return gptModelConfigService.checkModelIsAffect(modelIds);
    }


    private IPage<GptModelConfigDetailVO> PageDtoToVO(IPage<GptModelConfig> configIPage){
        IPage<GptModelConfigDetailVO> iPage = new Page();

        List<GptModelConfigDetailVO> vos = new ArrayList<>();
        for (GptModelConfig dto : configIPage.getRecords()) {
            GptModelConfigDetailVO vo = new GptModelConfigDetailVO();
            List<String> byModelIds = channelModelConfigService.getChannelIdsByModelId(dto.getId());
            vo.setBaseUrl(dto.getBaseUrl());
            vo.setChannelIds(byModelIds);
            vo.setCreateTime(dto.getCreateTime());
            vo.setCreateUser(dto.getCreateUser());
            vo.setId(dto.getId().toString());
            vo.setModel(dto.getModel());
            vo.setStatus(dto.getStatus());
            vo.setToken(dto.getToken());
            vo.setName(dto.getName());
            vo.setUpdateTime(dto.getUpdateTime());
            vo.setUpdateUser(dto.getUpdateUser());
            vo.setWeight(dto.getWeight());
            vos.add(vo);
        }
        iPage.setRecords(vos);
        iPage.setCurrent(configIPage.getCurrent());
        iPage.setTotal(configIPage.getTotal());
        iPage.setSize(configIPage.getSize());
        iPage.setPages(configIPage.getPages());
        return iPage;
    }



    public GptModelConfigVo DtoToVO(GptModelConfigAddReq config){
        GptModelConfigVo configDetailVO = new GptModelConfigVo();
        configDetailVO.setModel(config.getModel());
        configDetailVO.setId(config.getId());
        configDetailVO.setToken(config.getToken());
        configDetailVO.setBaseUrl(config.getBaseUrl());
        configDetailVO.setWeight(config.getWeight());
        return configDetailVO;
    }

}
