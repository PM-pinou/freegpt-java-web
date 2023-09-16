package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.UserLog.UserLog;
import com.chat.base.mapper.UserLogMapper;
import com.chat.base.service.IUserLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixin
 * @since 2023-06-01
 */
@Service
public class UserLogServiceImpl extends ServiceImpl<UserLogMapper, UserLog> implements IUserLogService {
    @Autowired
    private  UserLogMapper userLogMapper;


    public IPage<UserLog> queryEntitiesWithPagination(int current, int size, QueryWrapper<UserLog> queryWrapper) {
        Page<UserLog> page = new Page<>(current, size,20);
        return userLogMapper.queryEntitiesWithPagination(page, queryWrapper);
    }

}
