package com.chat.base.handler;

import com.chat.base.service.impl.TokenChannelConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liuzilin
 * @since 2023-08-03
 */
@Slf4j
@Component
public class TokenChannelConfigManager {

    @Autowired
    private TokenChannelConfigServiceImpl configService;

    public List<String> queryChannelIdsByToken(String token){
       return configService.queryChannelIdsByToken(token);
    }

    public Boolean addToTokenChannelConfig(String token,Long channelId){
       return configService.addToTokenChannelConfig(token,channelId);
    }

}
