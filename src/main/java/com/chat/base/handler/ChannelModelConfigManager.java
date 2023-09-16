package com.chat.base.handler;

import com.chat.base.bean.entity.ChannelModelConfig;
import com.chat.base.bean.req.ChannelModelReq;
import com.chat.base.service.impl.ChannelModelConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuzilin
 * @since 2023-08-03
 */
@Slf4j
@Component
public class ChannelModelConfigManager {

    @Autowired
    private ChannelModelConfigServiceImpl configService;

    public Boolean addChannelModelConfig(ChannelModelReq req){
        List<ChannelModelConfig> channelModelConfigs = req.getModelConfigId().stream()
                .map(modelId -> {
                    ChannelModelConfig channelModelConfig = new ChannelModelConfig();
                    channelModelConfig.setChannelConfigId(req.getChannelConfigId());
                    channelModelConfig.setModelConfigId(modelId);
                    return channelModelConfig;
                })
                .collect(Collectors.toList());
        Boolean result = configService.saveBatch(channelModelConfigs);
        if (!result){
            throw new RuntimeException("Transaction rollback");
        }
        return true;
    }


    public Boolean delChannelModelConfig(ChannelModelReq req){
        List<ChannelModelConfig> channelModelConfigs = req.getModelConfigId().stream()
                .map(modelId -> {
                    ChannelModelConfig channelModelConfig = new ChannelModelConfig();
                    channelModelConfig.setChannelConfigId(req.getChannelConfigId());
                    channelModelConfig.setModelConfigId(modelId);
                    return channelModelConfig;
                })
                .collect(Collectors.toList());
        configService.removeByChannelIds(channelModelConfigs);
        return true;
    }

    public List<String> queryModelIdsByChannelIds(List<String> channelIds) {
        if (CollectionUtils.isNotEmpty(channelIds)) {
            return configService.getModeslIdsByChannelIds(channelIds);
        }
        return Collections.emptyList();
    }
}
