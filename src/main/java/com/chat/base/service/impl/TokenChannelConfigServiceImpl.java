package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chat.base.bean.entity.TokenChannelConfig;
import com.chat.base.mapper.TokenChannelConfigMapper;
import com.chat.base.service.ITokenChannelConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Service
public class TokenChannelConfigServiceImpl extends ServiceImpl<TokenChannelConfigMapper, TokenChannelConfig> implements ITokenChannelConfigService {

    @Autowired
    private TokenChannelConfigMapper mapper;

    public List<String> queryChannelIdsByToken(String token){
        return mapper.queryChannelIdsByToken(token);
    }

    public Boolean addToTokenChannelConfig(String token,Long channelIds){
        TokenChannelConfig tokenChannelConfig = new TokenChannelConfig();
        tokenChannelConfig.setToken(token);
        tokenChannelConfig.setChannelConfigId(channelIds);
        return mapper.insert(tokenChannelConfig) == 1;
    }

    public Boolean delToTokenChannelConfig(String token){
        TokenChannelConfig tokenChannelConfig = new TokenChannelConfig();
        tokenChannelConfig.setToken(token);

        QueryWrapper<TokenChannelConfig> wrapper = new QueryWrapper<>();
        wrapper.setEntity(tokenChannelConfig);
        return mapper.delete(wrapper) >= 1;
    }

}
