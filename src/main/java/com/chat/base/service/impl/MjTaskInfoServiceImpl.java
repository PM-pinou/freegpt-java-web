package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.MjTaskInfo;
import com.chat.base.mapper.MjTaskInfoMapper;
import com.chat.base.service.IMjTaskInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-25
 */
@Service
public class MjTaskInfoServiceImpl extends ServiceImpl<MjTaskInfoMapper, MjTaskInfo> implements IMjTaskInfoService {

    @Autowired
    private MjTaskInfoMapper mapper;

    public IPage<MjTaskInfo> queryEntitiesWithPagination(int current, int size, QueryWrapper<MjTaskInfo> queryWrapper) {
        Page<MjTaskInfo> page = new Page<>(current, size,20);
        return mapper.queryEntitiesWithPagination(page, queryWrapper);
    }
}
