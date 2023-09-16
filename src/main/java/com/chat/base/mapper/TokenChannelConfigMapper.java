package com.chat.base.mapper;

import com.chat.base.bean.entity.TokenChannelConfig;
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
public interface TokenChannelConfigMapper extends BaseMapper<TokenChannelConfig> {

    @Select("select channel_config.id from  channel_config  join  token_channel_config on channel_config.id = token_channel_config.channel_config_id  where channel_config.`status` = 1 and token_channel_config.token =#{token}")
    List<String> queryChannelIdsByToken(@Param("token") String token);


}
