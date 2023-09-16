package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.UserAccessRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.base.bean.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户访问规则 Mapper 接口
 * </p>
 *
 * @author lixin
 * @since 2023-05-10
 */
@Mapper
public interface UserAccessRuleMapper extends BaseMapper<UserAccessRule> {

    @Select("select * from user_access_rule where user_id = #{userId}")
    List<UserAccessRule> queryByUserId(@Param("userId") Long userId);

    @Select({
            "<script>" ,
            " SELECT * from user_access_rule ",
            " WHERE  id in (" ,
            " <foreach item='id' index='index' collection='ids' separator=','>" ,
            " #{id}" ,
            " </foreach>) " ,
            "</script>"
    })
    List<UserAccessRule> queryByIds(@Param("ids") Set<Long> ids);



    default IPage<UserAccessRule> queryEntitiesWithPagination(Page<UserAccessRule> page, QueryWrapper<UserAccessRule> queryWrapper) {
        return selectPage(page, queryWrapper);
    }
}
