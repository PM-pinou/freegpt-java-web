package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.UserLog.UserLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lixin
 * @since 2023-06-01
 */
public interface UserLogMapper extends BaseMapper<UserLog> {

    default IPage<UserLog> queryEntitiesWithPagination(Page<UserLog> page, QueryWrapper<UserLog> queryWrapper) {
        return selectPage(page, queryWrapper);
    }
}
