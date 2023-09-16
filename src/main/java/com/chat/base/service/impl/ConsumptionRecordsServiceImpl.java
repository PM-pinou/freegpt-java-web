package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.base.bean.entity.ConsumptionRecords;
import com.chat.base.mapper.ConsumptionRecordsMapper;
import com.chat.base.service.IConsumptionRecordsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户的消费日志 服务实现类
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-15
 */
@Service
public class ConsumptionRecordsServiceImpl extends ServiceImpl<ConsumptionRecordsMapper, ConsumptionRecords> implements IConsumptionRecordsService {


    public IPage< ConsumptionRecords> queryEntitiesWithPagination(int current, int size, QueryWrapper<ConsumptionRecords> queryWrapper) {
        Page<ConsumptionRecords> page = new Page<>(current, size,20);
        return this.getBaseMapper().queryEntitiesWithPagination(page, queryWrapper);
    }
}
