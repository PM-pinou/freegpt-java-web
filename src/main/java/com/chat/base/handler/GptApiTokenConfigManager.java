package com.chat.base.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.entity.GptApiTokenConfig;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.req.GptApiTokenConfigAddReq;
import com.chat.base.bean.req.GptApiTokenConfigReq;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.GptApiTokenConfigVo;
import com.chat.base.service.impl.GptApiTokenConfigServiceImpl;
import com.chat.base.service.impl.TokenChannelConfigServiceImpl;
import com.chat.base.utils.AmountUtil;
import com.chat.base.utils.CacheUtil;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author liuzilin
 * @since 2023-08-03
 */
@Slf4j
@Component
public class GptApiTokenConfigManager {

    @Autowired
    private GptApiTokenConfigServiceImpl configService;

    @Autowired
    private TokenChannelConfigServiceImpl tokenChannelService;

    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @Autowired
    private UserManager userManager;


    public GptApiTokenConfig getGptApiTokenConfigByToken(String token){
        return configService.getBaseMapper().getGptApiTokenConfigByToken(token);
    }

    public CacheGptApiTokenVo queryGptApiTokenByUserId(Long userId){
        GptApiTokenConfig gptApiTokenConfig = configService.queryApiTokenByUserId(userId);
        if (Objects.nonNull(gptApiTokenConfig)){
            return DtoToCacheVO(gptApiTokenConfig);
        }
       return null;
    }

    public IPage<GptApiTokenConfigVo> queryGptApiTokens(GptApiTokenConfigReq req){
        // 适配根据用户账号查询的去看
        if(StringUtils.isNotBlank(req.getAccount())){
            UserInfo userInfo = userManager.queryUserInfoByAccount(req.getAccount());
            if(Objects.nonNull(userInfo)){
                req.setUserId(userInfo.getId());
            }
        }
        GptApiTokenConfig gptApiTokenConfig = new GptApiTokenConfig();
        gptApiTokenConfig.setId(req.getId());
        gptApiTokenConfig.setToken(req.getToken());
        gptApiTokenConfig.setStatus(req.getStatus());
        gptApiTokenConfig.setUserId(req.getUserId());
        QueryWrapper<GptApiTokenConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(gptApiTokenConfig);
        if(Objects.nonNull(req.getCreateTime()) && Objects.nonNull(req.getEndTime())){
            queryWrapper.between("end_time",req.getCreateTime(),req.getEndTime());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<GptApiTokenConfig> configIPage = configService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), queryWrapper);
        return DtoToVOs(configIPage);
    }

    public Boolean addGptApiTokenByUserInfo(UserInfo userInfo){
        if(Objects.isNull(userInfo)){
            log.info("addGptApiTokenByUserInfo fail userInfo is null userInf={}",userInfo);
            return false;
        }
        GptApiTokenConfig gptApiTokenConfig = new GptApiTokenConfig();
        gptApiTokenConfig.setUserId(userInfo.getId());
        gptApiTokenConfig.setName(userInfo.getUsername());
        gptApiTokenConfig.setCreateTime(LocalDateTime.now());
        gptApiTokenConfig.setCreateUser("system");
        gptApiTokenConfig.setToken("sk-"+UUID.randomUUID().toString().replaceAll("-", ""));
        Boolean addGptApiTokenResult = configService.addGptApiTokenConfig(gptApiTokenConfig);
        Boolean tokenAndChannelResult = addTokenAndChannelId(gptApiTokenConfig.getToken(), CommonConstant.DEFAULT_CHANNEL_ID);
        log.info("addGptApiTokenByUserInfo userInfo  addGptApiTokenResult = {},tokenAndChannelResult={} ",addGptApiTokenResult,tokenAndChannelResult);
        if (!addGptApiTokenResult || !tokenAndChannelResult){
            // 返回false 上层抛异常，结束事物
            return false;
        }
        return true;
    }


    @Transactional
    public Boolean updateGptApiToken(GptApiTokenConfigAddReq req){
        Boolean delChannelresult  = false;
        Boolean updateChannelresult  = false;
        Boolean updateTokenresult  = false;
        if(CollectionUtils.isNotEmpty(req.getChannelIds())){
            // 删除旧的绑定通道
            delChannelresult =  tokenChannelService.delToTokenChannelConfig(req.getToken());
            // 添加新的绑定通道
            for (Long channelId : req.getChannelIds()) {
                updateChannelresult = tokenChannelService.addToTokenChannelConfig(req.getToken(), channelId);
            }
        }
        GptApiTokenConfig gptApiTokenConfig = VoToDto(req);
        updateTokenresult = configService.updateById(gptApiTokenConfig);
        if(!delChannelresult  && !updateChannelresult && !updateTokenresult){
         throw new RuntimeException(" rollback transcation");
        }
        //对修改的用户进行更新缓存的操作
        CacheUserInfoVo userInfoVo = CacheUtil.getIfPresent(String.valueOf(req.getUserId()));
        // 缓存中不存在则不对缓存做处理
        if (userInfoVo!=null){
            if( req.getBalance()!=null && req.getBalance()>0){
                synchronized (userInfoVo.getClass()){
                    weightAlgorithmManager.initAlgorithm(userInfoVo);
                }
            }
        }
        return true;
    }

    private Boolean addTokenAndChannelId(String token,Long channelId){
       return tokenChannelService.addToTokenChannelConfig(token,channelId);
    }

    private GptApiTokenConfig VoToDto(GptApiTokenConfigAddReq req){
        GptApiTokenConfig entity = new GptApiTokenConfig();
        entity.setVisitNumber(req.getVisitNumber());
        entity.setUserId(req.getUserId());
        entity.setUpdateUser(SessionUser.getAccount());
        entity.setVisitNumber(req.getVisitNumber());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setToken(req.getToken());
        entity.setStatus(req.getStatus());
        entity.setName(req.getName());
        entity.setId(req.getId());
        entity.setEndTime(req.getEndTime());
        entity.setCreateUser(req.getCreateUser());
        entity.setCreateTime(req.getCreateTime());
        entity.setBalance(req.getBalance());
        return entity;
    }

    private IPage<GptApiTokenConfigVo> DtoToVOs(IPage<GptApiTokenConfig> configIPage){
        IPage<GptApiTokenConfigVo> iPage = new Page();
        List<GptApiTokenConfigVo> configVos = new ArrayList<>();
        for (GptApiTokenConfig record : configIPage.getRecords()) {
            GptApiTokenConfigVo gptApiTokenConfigVo = DtoToVO(record);
            configVos.add(gptApiTokenConfigVo);
        }
        iPage.setRecords(configVos);
        iPage.setCurrent(configIPage.getCurrent());
        iPage.setTotal(configIPage.getTotal());
        iPage.setSize(configIPage.getSize());
        iPage.setPages(configIPage.getPages());
        return iPage;
    }

    private GptApiTokenConfigVo DtoToVO(GptApiTokenConfig record){
            GptApiTokenConfigVo gptApiTokenConfigVo = new GptApiTokenConfigVo();
            List<String> channelIds = tokenChannelService.queryChannelIdsByToken(record.getToken());
            gptApiTokenConfigVo.setBalance(record.getBalance());
            gptApiTokenConfigVo.setChannelIds(channelIds);
            gptApiTokenConfigVo.setCreateTime(record.getCreateTime());
            gptApiTokenConfigVo.setCreateUser(record.getCreateUser());
            gptApiTokenConfigVo.setEndTime(record.getEndTime());
            gptApiTokenConfigVo.setId(record.getId());
            gptApiTokenConfigVo.setName(record.getName());
            gptApiTokenConfigVo.setStatus(record.getStatus());
            gptApiTokenConfigVo.setToken(record.getToken());
            gptApiTokenConfigVo.setUpdateTime(record.getUpdateTime());
            gptApiTokenConfigVo.setUpdateUser(record.getUpdateUser());
            gptApiTokenConfigVo.setUserId(record.getUserId());
            gptApiTokenConfigVo.setVisitNumber(record.getVisitNumber());
            gptApiTokenConfigVo.setBalanceStr(AmountUtil.getTokenAmount(record.getBalance()));
        return gptApiTokenConfigVo;
    }



    private CacheGptApiTokenVo DtoToCacheVO(GptApiTokenConfig record){
        CacheGptApiTokenVo gptApiTokenConfigVo = new CacheGptApiTokenVo();
        List<String> channelIds = tokenChannelService.queryChannelIdsByToken(record.getToken());
        gptApiTokenConfigVo.setBalance(record.getBalance());
        gptApiTokenConfigVo.setChannelIds(channelIds);
        gptApiTokenConfigVo.setId(record.getId());
        gptApiTokenConfigVo.setName(record.getName());
        gptApiTokenConfigVo.setStatus(record.getStatus());
        gptApiTokenConfigVo.setToken(record.getToken());
        gptApiTokenConfigVo.setUserId(record.getUserId());
        gptApiTokenConfigVo.setVisitNumber(record.getVisitNumber());
        return gptApiTokenConfigVo;
    }

}
