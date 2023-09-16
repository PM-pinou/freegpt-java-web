package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.GptApiTokenConfig;
import com.chat.base.bean.entity.UserAccessRule;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.mapper.GptApiTokenConfigMapper;
import com.chat.base.service.IGptApiTokenConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-04
 */
@Slf4j
@Service
public class GptApiTokenConfigServiceImpl extends ServiceImpl<GptApiTokenConfigMapper, GptApiTokenConfig> implements IGptApiTokenConfigService {


    public List<GptApiTokenConfig> getGptApiTokenConfigByIds(Set<Long> ids){
        return this.baseMapper.getGptApiTokenConfigByIds(ids);
    }



    public IPage<GptApiTokenConfig> queryEntitiesWithPagination(int current, int size, QueryWrapper<GptApiTokenConfig> queryWrapper) {
        Page<GptApiTokenConfig> page = new Page<>(current, size,20);
        return this.baseMapper.queryEntitiesWithPagination(page, queryWrapper);
    }

    public Boolean addGptApiTokenConfig(GptApiTokenConfig config){
       return this.baseMapper.insert(config) == 1;
    }


    public GptApiTokenConfig queryApiTokenByUserId(Long userId){
        QueryWrapper<GptApiTokenConfig> wrapper = new QueryWrapper<>();
        GptApiTokenConfig gptApiTokenConfig = new GptApiTokenConfig();
        gptApiTokenConfig.setUserId(userId);
        gptApiTokenConfig.setStatus(1);
        wrapper.setEntity(gptApiTokenConfig);
       return this.baseMapper.selectOne(wrapper);
    }


    /**
     * 批量更新 用户访问配置
     * @param gptApiTokenConfigs
     * @return
     */
    public boolean batchUpdate(List<GptApiTokenConfig> gptApiTokenConfigs){
        if(CollectionUtils.isEmpty(gptApiTokenConfigs)){
            log.info("batchUpdate gptApiTokenConfigs is empty");
            return false;
        }
        return this.updateBatchById(gptApiTokenConfigs);
    }

}
