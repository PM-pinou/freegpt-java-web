package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.entity.PromptRecord;
import com.chat.base.mapper.GptModelConfigMapper;
import com.chat.base.mapper.PromptRecordMapper;
import com.chat.base.service.IGptModelConfigService;
import com.chat.base.service.IPromptRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.chat.base.mapper.GptModelConfigMapper;
import com.chat.base.service.IGptModelConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixin
 * @since 2023-08-01
 */
@Service
public class GptModelConfigServiceImpl extends ServiceImpl<GptModelConfigMapper, GptModelConfig> implements IGptModelConfigService {

    @Autowired
    private GptModelConfigMapper gptModelConfigMapper;

    public IPage<GptModelConfig> queryEntitiesWithPagination(int current, int size, QueryWrapper<GptModelConfig> queryWrapper) {
        Page<GptModelConfig> page = new Page<>(current, size,20);
        return gptModelConfigMapper.queryEntitiesWithPagination(page, queryWrapper);
    }


    public Boolean checkModelIsAffect(List<Long> modelIds){
        List<GptModelConfig> gptModelConfigs = gptModelConfigMapper.checkModelIsAffect(modelIds);
        if (gptModelConfigs.size() != modelIds.size()){
            return false;
        }
        return true;
    }

    public GptModelConfig getGptModelConfig(Long id){
        List<GptModelConfig> gptModelConfigs = gptModelConfigMapper.checkModelIsAffect(Arrays.asList(id));
        if(CollectionUtils.isNotEmpty(gptModelConfigs)){
            return gptModelConfigs.get(0);
        }
        return null;
    }


}
