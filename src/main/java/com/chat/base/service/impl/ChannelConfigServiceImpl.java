package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.ChannelConfig;
import com.chat.base.bean.entity.GptApiTokenConfig;
import com.chat.base.mapper.ChannelConfigMapper;
import com.chat.base.service.IChannelConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Service
public class ChannelConfigServiceImpl extends ServiceImpl<ChannelConfigMapper, ChannelConfig> implements IChannelConfigService {
    public IPage<ChannelConfig> queryEntitiesWithPagination(int current, int size, QueryWrapper<ChannelConfig> queryWrapper) {
        Page<ChannelConfig> page = new Page<>(current, size,20);
        return this.baseMapper.queryEntitiesWithPagination(page, queryWrapper);
    }

}
