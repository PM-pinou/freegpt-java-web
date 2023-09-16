package com.chat.base.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.bean.entity.ChannelConfig;
import com.chat.base.bean.entity.GptApiTokenConfig;
import com.chat.base.bean.req.ChannelConfigAddReq;
import com.chat.base.bean.req.ChannelConfigDelReq;
import com.chat.base.bean.req.ChannelConfigReq;
import com.chat.base.bean.req.GptApiTokenConfigReq;
import com.chat.base.bean.vo.GptApiTokenConfigVo;
import com.chat.base.service.impl.ChannelConfigServiceImpl;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author liuzilin
 * @since 2023-08-03
 */
@Slf4j
@Component
public class ChannelConfigManager {

    @Autowired
    private ChannelConfigServiceImpl configService;
    @Autowired
    private ChannelModelConfigManager channelModelConfigManager;


    public IPage<ChannelConfig> getChannelConfig(ChannelConfigReq req){
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setId(req.getId());
        channelConfig.setName(req.getName());
        channelConfig.setStatus(req.getStatus());
        channelConfig.setModelType(req.getModelType());
        QueryWrapper<ChannelConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(channelConfig);
        return configService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), queryWrapper);
    }


    public Boolean addChannelConfig(ChannelConfigAddReq req){
        if(Objects.isNull(req)){
            log.info("addChannelConfig fail req is null req={}",req);
            return false;
        }
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setModelType(req.getModelType());
        channelConfig.setName(req.getName());
        channelConfig.setStatus(1);
        channelConfig.setCreateTime(LocalDateTime.now());
        channelConfig.setCreateUser(SessionUser.getAccount());
        return configService.save(channelConfig);
    }



    public Boolean updateChannelConfig(ChannelConfigAddReq req){
        if(Objects.isNull(req)){
            log.info("updateChannelConfig fail req is null req={}",req);
            return false;
        }
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setId(req.getId());
        channelConfig.setModelType(req.getModelType());
        channelConfig.setName(req.getName());
        channelConfig.setStatus(req.getStatus());
        channelConfig.setUpdateTime(LocalDateTime.now());
        channelConfig.setUpdateUser(SessionUser.getAccount());
        return configService.updateById(channelConfig);
    }

    public Boolean delChannelConfig(ChannelConfigDelReq req){
        List<String> modelId = channelModelConfigManager.queryModelIdsByChannelIds(Collections.singletonList(req.getId()));
        if(CollectionUtils.isNotEmpty(modelId)){
            return false;
        }
        return   configService.removeById(req.getId());
    }
}
