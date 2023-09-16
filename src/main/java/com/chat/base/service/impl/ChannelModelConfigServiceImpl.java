package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chat.base.bean.entity.ChannelModelConfig;
import com.chat.base.bean.req.ChannelModelReq;
import com.chat.base.mapper.ChannelModelConfigMapper;
import com.chat.base.service.IChannelModelConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Service
public class ChannelModelConfigServiceImpl extends ServiceImpl<ChannelModelConfigMapper, ChannelModelConfig> implements IChannelModelConfigService {

    @Autowired
    private ChannelModelConfigMapper channelModelConfigMapper;

    public void removeByChannelIds(List<ChannelModelConfig> channelModelConfigs){
        channelModelConfigs.stream().forEach( channelModelConfig ->{
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.setEntity(channelModelConfig);
            this.baseMapper.delete(wrapper);
        } );
    }

    public void removeByModelIds(Long modelId){
        QueryWrapper<ChannelModelConfig> wrapper = new QueryWrapper<>();
        ChannelModelConfig config = new ChannelModelConfig();
        config.setModelConfigId(modelId);
        wrapper.setEntity(config);
        this.baseMapper.delete(wrapper);
    }


    public Boolean insertChannelIds(Long modelId,List<Long> channelIds){
        List<ChannelModelConfig> modelConfigs = new ArrayList<>();
        channelIds.stream().forEach(channelId -> {
            ChannelModelConfig modelConfig = new ChannelModelConfig();
            modelConfig.setModelConfigId(modelId);
            modelConfig.setChannelConfigId(channelId);
            modelConfigs.add(modelConfig);
        });
       return this.saveBatch(modelConfigs);
    }

    public Boolean isBandingChannel(Long modelId){
        ChannelModelConfig channelModelConfig = new ChannelModelConfig();
        channelModelConfig.setModelConfigId(modelId);
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.setEntity(channelModelConfig);
        return this.baseMapper.selectList(wrapper).size() >0 ;
    }

    public List<String> getChannelIdsByModelId(Long modelId){
        ChannelModelConfig channelModelConfig = new ChannelModelConfig();
        channelModelConfig.setModelConfigId(modelId);
        QueryWrapper<ChannelModelConfig> wrapper = new QueryWrapper<>();
        wrapper.setEntity(channelModelConfig);

        List<ChannelModelConfig> lists = this.baseMapper.selectList(wrapper);
        List<String> channelIds = new ArrayList<>();
        lists.stream().forEach(list -> channelIds.add(list.getChannelConfigId().toString()));
        return channelIds;
    }
    public List<String> getModeslIdsByChannelIds(List<String> channelIds){
        return  channelModelConfigMapper.getModeslIdsByChannelIds(channelIds);
    }



}
