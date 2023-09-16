package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.PromptRecord;
import com.chat.base.bean.entity.UserLog.UserLog;
import com.chat.base.mapper.PromptRecordMapper;
import com.chat.base.service.IPromptRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixin
 * @since 2023-05-17
 */
@Service
public class PromptRecordServiceImpl extends ServiceImpl<PromptRecordMapper, PromptRecord> implements IPromptRecordService {

    @Autowired
    private  PromptRecordMapper promptRecordMapper;


    public IPage<PromptRecord> queryEntitiesWithPagination(int current, int size, QueryWrapper<PromptRecord> queryWrapper) {
        Page<PromptRecord> page = new Page<>(current, size,20);
        return promptRecordMapper.queryEntitiesWithPagination(page, queryWrapper);
    }
}
