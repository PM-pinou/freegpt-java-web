package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.ChannelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.base.bean.entity.GptApiTokenConfig;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
public interface ChannelConfigMapper extends BaseMapper<ChannelConfig> {

    default IPage<ChannelConfig> queryEntitiesWithPagination(Page<ChannelConfig> page, QueryWrapper<ChannelConfig> queryWrapper) {
        return selectPage(page, queryWrapper);
    }
}
