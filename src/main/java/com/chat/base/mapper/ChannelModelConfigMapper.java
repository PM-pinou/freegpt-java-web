package com.chat.base.mapper;

import com.chat.base.bean.entity.ChannelModelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
public interface ChannelModelConfigMapper extends BaseMapper<ChannelModelConfig> {


    @Select(
            "<script>" +
                    " SELECT model_config_id FROM `channel_model_config` "+
                    " WHERE  channel_config_id in (" +
                    " <foreach item='id' index='index' collection='channelIds' separator=','>" +
                    " #{id}" +
                    " </foreach>)" +
                    "</script> "
    )
    //todo 加缓存
    List<String> getModeslIdsByChannelIds(@Param("channelIds") List<String> channelIds);

}
