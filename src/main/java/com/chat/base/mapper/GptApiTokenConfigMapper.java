package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.GptApiTokenConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-04
 */
@Mapper
public interface GptApiTokenConfigMapper extends BaseMapper<GptApiTokenConfig> {

    default IPage<GptApiTokenConfig> queryEntitiesWithPagination(Page<GptApiTokenConfig> page, QueryWrapper<GptApiTokenConfig> queryWrapper) {
        return selectPage(page, queryWrapper);
    }


    @Select({
            "<script>" ,
            " SELECT * from gpt_api_token_config ",
            " WHERE  id in (" ,
            " <foreach item='id' index='index' collection='ids' separator=','>" ,
            " #{id}" ,
            " </foreach>) " ,
            "</script>"
    })
    List<GptApiTokenConfig> getGptApiTokenConfigByIds(@Param("ids") Set<Long> ids);


    @Select("select * from gpt_api_token_config where token = #{token} and status = 1  limit 1")
    GptApiTokenConfig getGptApiTokenConfigByToken(String token);


}
