package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.ConsumptionRecords;

/**
 * <p>
 * 用户的消费日志 Mapper 接口
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-15
 */
public interface ConsumptionRecordsMapper extends BaseMapper<ConsumptionRecords> {

    default IPage<ConsumptionRecords> queryEntitiesWithPagination(Page<ConsumptionRecords> page, QueryWrapper<ConsumptionRecords> queryWrapper) {
        return selectPage(page, queryWrapper);
    }
}
