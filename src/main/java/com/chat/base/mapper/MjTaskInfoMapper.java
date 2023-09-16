package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.MjTaskInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-25
 */
@Mapper
public interface MjTaskInfoMapper extends BaseMapper<MjTaskInfo> {

    default IPage<MjTaskInfo> queryEntitiesWithPagination(Page<MjTaskInfo> page, QueryWrapper<MjTaskInfo> queryWrapper) {
        return selectPage(page, queryWrapper);
    }

    @Select("select count(1) from mj_task_info where create_time>= #{startTime} and create_time <=#{endTime}  ")
    int selectTodayTaskCount(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime);


}
