package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.entity.UserAccessRule;
import com.chat.base.handler.GptModelConfigManager;
import com.chat.base.handler.PromptRecordManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lixin
 * @since 2023-08-01
 */
@Mapper
public interface GptModelConfigMapper extends BaseMapper<GptModelConfig> {

    @Select("select * from gpt_model_config where status = 1 ")
    List<GptModelConfig> getAllValidGptConfig();


    default IPage<GptModelConfig> queryEntitiesWithPagination(Page<GptModelConfig> page, QueryWrapper<GptModelConfig> queryWrapper) {
        return selectPage(page, queryWrapper);
    }



    @Select(
            "<script>" +
                    " SELECT id FROM `gpt_model_config` "+
                    " WHERE  id in (" +
                    " <foreach item='id' index='index' collection='ids' separator=','>" +
                    " #{id}" +
                    " </foreach>)  and status = 1" +
                    "</script> "
    )
    List<GptModelConfig> checkModelIsAffect(@Param("ids") List<Long> ids);


}
