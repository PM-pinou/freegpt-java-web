package com.chat.base.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.ConsumptionConstant;
import com.chat.base.bean.constants.ModelPriceEnum;
import com.chat.base.bean.constants.OpEnum;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.req.UserInfoLoginReq;
import com.chat.base.bean.req.UserInfoRegisterReq;
import com.chat.base.bean.req.UserQueryReq;
import com.chat.base.bean.req.UserUpdateReq;
import com.chat.base.bean.util.UserInfoUtil;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.ChatMessageResultVo;
import com.chat.base.bean.vo.ModelBillingParamVo;
import com.chat.base.handler.billing.ModelBillingFactory;
import com.chat.base.handler.billing.ModelBillingService;
import com.chat.base.service.impl.UserInfoServiceImpl;
import com.chat.base.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class UserManager {

    @Autowired
    private UserInfoServiceImpl userInfoService;

    @Autowired
    private UserLogManager userLogManager;

    @Autowired
    private GptApiTokenConfigManager gptApiTokenConfigManager;

    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @PostConstruct
    public void initGuavaBloomFilter(){
        List<String> allAccount = userInfoService.getBaseMapper().getAllAccount();
        for (String account : allAccount) {
            boolean result = GuavaBloomFilterUtil.putValue(account);
            if(!result){
                log.error("initGuavaBloomFilter error account={},result={}",account,result);
            }
        }
    }


    /**
     *  聊天记录更新额度
     * @param cacheUserInfoVo
     * @param streamChatCompletion
     * @return
     */
    public boolean costUserBalanceByChat(CacheUserInfoVo cacheUserInfoVo,ChatMessageResultVo streamChatCompletion,String tradeId){

        if(cacheUserInfoVo==null || streamChatCompletion==null)return false;

        ModelPriceEnum modelPrice = ModelPriceEnum.getModelPrice(streamChatCompletion.getModel());
        int promptTokenNumber = streamChatCompletion.getPromptTokenNumber();
        int relyTokenNumber = streamChatCompletion.getRelyTokenNumber();
        Long cost = modelPrice.getInPrice()*promptTokenNumber + modelPrice.getOutPrice()*relyTokenNumber;

        if( promptTokenNumber >0 && relyTokenNumber>0 ){
            return updateUserBalance(cacheUserInfoVo,streamChatCompletion.getModel(),cost,
                    streamChatCompletion.getSystemToken(), ConsumptionConstant.GPT_TYPE, CommonConstant.CONSUME,tradeId);// 更新用户缓存中的余额
        }else{
            log.error("访问错误 error streamChatCompletion={}",streamChatCompletion);
        }
        return false;
    }




    /**
     * 减少用户余额缓存
     * @param cost 本次消费的token数
     * @param systemToken 渠道的token
     */
    public boolean updateUserBalance(CacheUserInfoVo cacheUserInfoVo,String model,Long cost,String systemToken,int type,int updateType, String tradeId){
        if(Objects.nonNull(cacheUserInfoVo)){
            // 如果用户走到这里 不管余额够不够都需要扣费了
            ModelBillingService modelBillingService = ModelBillingFactory.getModelBillingService(model);
            if(modelBillingService==null){
                log.error("updateUserBalance error model={},cost={}",model,cost);
                return false;
            }
            boolean billing = false;
            ModelBillingParamVo.ModelBillingParamVoBuilder builder = ModelBillingParamVo.builder()
                    .model(model)
                    .type(type)
                    .token(systemToken);

            switch (updateType) {
                case CommonConstant.CONSUME:
                    billing = modelBillingService.billing(cacheUserInfoVo.getGptApiTokenVo(), builder
                                    .op("消费" + model)
                                    .build(), cost,tradeId);
                    break;
                case CommonConstant.RETURN_MONEY:
                    billing = modelBillingService.upPilling(cacheUserInfoVo.getGptApiTokenVo(), builder
                                    .op(model+"退费")
                                    .build(), cost);
            }
            log.info("updateUserBalance billing={},token={} ",billing,cacheUserInfoVo.getGptApiTokenVo().getToken());
            return billing;
        }else{
            // 用户没有登录 无法扣费
            return false;
        }
    }

    /**
     * 缓存扣费
     */
    public Boolean decreaseCacheBalance(CacheGptApiTokenVo vo, Long couponCost){
        if(vo==null || couponCost < 0 )return false;
        Long balance = vo.getBalance();
        if( balance>0 && balance-couponCost >=0 ){
            // todo 如果多实例部署的话 这里需要改成 分布式锁
            synchronized(vo.getClass()){
                balance = vo.getBalance();
                long after = balance - couponCost;
                if(balance<=0){
                    return false;
                }
                if(after >=0 ){
                    vo.setBalance(after);
                }else {
                    // 如果用户余额不够本次消费的，直接将用户本次的余额置为0
                    vo.setBalance(0L);
                }
                return true; // 消费成功
            }
        }
        return false;
    }

    /**
     * 增加扣费
     */
    public Boolean increaseCachaBalance(CacheGptApiTokenVo vo, Long returnBalance){
        if( vo==null || returnBalance <= 0 )return false;
        // todo 如果多实例部署的话 这里需要改成 分布式锁
        synchronized(vo.getClass()){
            Long balance = vo.getBalance();
            long after = balance + returnBalance;
            vo.setBalance(after);
            return true; // 消费成功
        }
    }




    private UserInfo updateReqToEntity(UserUpdateReq updateReq){
        UserInfo entity = new UserInfo();
        entity.setId(updateReq.getUserId());
        entity.setPassword(updateReq.getPassword());
        entity.setUserLevel(updateReq.getUserLevel());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setStatus(updateReq.getStatus());
        return entity;
    }

    /**
     * 更新用户信息
     * @param updateReq
     * @return
     */
    @Transactional
    public UserInfo updateUserInfo(UserUpdateReq updateReq,String ip){
        UserInfo entity = updateReqToEntity(updateReq);
        boolean result = userInfoService.updateById(entity);
        if(result){
            return userInfoService.getBaseMapper().selectById(entity.getId());
        }
        return null;
    }


    /**
     * 用户登录
     * @param loginReq
     * @return
     */
    public CacheUserInfoVo login(UserInfoLoginReq loginReq,String ip){

        boolean exist = GuavaBloomFilterUtil.exist(loginReq.getAccount());
        if(!exist){
            // 账号不存在
            log.info("login GuavaBloomFilter account={} ",loginReq.getAccount());
            return null;
        }

        Optional<UserInfo> entity = userInfoService.queryUserByAccountAndPassword(loginReq.getAccount(), loginReq.getPassword());
        if(entity.isPresent()){
            UserInfo user = entity.get();
            CacheUserInfoVo cacheUserInfoVo = CacheUtil.getIfPresent(String.valueOf(user.getId()));
            if(cacheUserInfoVo!=null){
                return cacheUserInfoVo;
            }
            return getCacheUserInfoVoByUserByUserBean(user,ip);
        }
        return null;
    }


    /**
     * 构建缓存对象时 需要使用全局锁
     * 线程安全的
     * @param user
     * @param ip
     * @return
     */
    public CacheUserInfoVo getCacheUserInfoVoByUserByUserBean(UserInfo user,String ip){
        CacheUserInfoVo userInfoVo = CacheUtil.getIfPresent(String.valueOf(user.getId()));
        if(userInfoVo!=null){
            return userInfoVo;
        }
        synchronized (UserManager.class){
            userInfoVo = CacheUtil.getIfPresent(String.valueOf(user.getId()));
            if(userInfoVo==null){
                userInfoVo = buildCacheUserInfoVo(user,ip);
                CacheUtil.put(String.valueOf(user.getId()), userInfoVo);
            }
            return userInfoVo;
        }
    }


    /**
     * 构建缓存实体
     * @param user
     */
    private CacheUserInfoVo buildCacheUserInfoVo(UserInfo user,String ip){
        CacheUserInfoVo cacheUserInfoVo = UserInfoUtil.entityToVo(user);
        cacheUserInfoVo.setIp(ip);
        weightAlgorithmManager.initAlgorithm(cacheUserInfoVo);
        return cacheUserInfoVo;
    }

    public CacheUserInfoVo test(String model){
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();

        Optional<GptModelConfig> round = weightAlgorithmManager.round(cacheUserInfoVo, model);
        log.info("GptModelConfig ={}",round.get());
        return cacheUserInfoVo;

    }
    /**
     * 用户注册
     * @param registerReq
     * @return
     */
    @Transactional
    public ResultVO register(UserInfoRegisterReq registerReq,String ip){
        String account = registerReq.getAccount();
        boolean existAccount = GuavaBloomFilterUtil.exist(account);
        if(existAccount){
            return ResultVO.fail("该账号已经被注册过了！");
        }

        UserInfo userInfo = UserInfoUtil.reqToEntity(registerReq);
        int result = userInfoService.addUser(userInfo);
        if(result>0){
            userLogManager.addUserLog(registerReq.getAppName(),String.valueOf(userInfo.getId()), OpEnum.REGISTER.getOp(),ip, HttpUtil.browserName());
            Map<String,String> reqMap = new HashedMap();
            reqMap.put(CommonConstant.TOKEN,String.valueOf(userInfo.getId()));
            Boolean addGptApiToken = gptApiTokenConfigManager.addGptApiTokenByUserInfo(userInfo);
            if (!addGptApiToken){
                throw new RuntimeException("Transaction rollback");
            }
            GuavaBloomFilterUtil.putValue(account);
            login(UserInfoLoginReq.builder().account(registerReq.getAccount()).password(registerReq.getPassword()).build(),ip);
            return ResultVO.success(reqMap);
        }
        return ResultVO.fail("注册失败！服务器繁忙！");
    }

    public UserInfo queryUserInfoByAccount(String account){
        return userInfoService.queryUserInfoByAccount(account);
    }


    public UserInfo queryUserInfoById(Long id){
        return userInfoService.queryUserInfoById(id);
    }

    public IPage<UserInfo> queryUserInfo(UserQueryReq req){
        try {
            UserInfo userLog = new UserInfo();
            userLog.setAccount(req.getAccount());
            userLog.setPhone(req.getPhone());
            userLog.setStatus(req.getStatus());
            userLog.setId(req.getId());
            userLog.setUserLevel(req.getUserLevel());
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.setEntity(userLog);
            if(Objects.nonNull(req.getStartTime()) && Objects.nonNull(req.getEndTime())){
                queryWrapper.between("create_time",req.getStartTime(),req.getEndTime());
            }
            queryWrapper.orderByDesc("create_time");
            queryWrapper.select("id","username","avatar","account","phone","create_time","update_time","status","user_level");
            return userInfoService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), queryWrapper);
        }catch (Exception e){
            log.error("addSystemLog error req={}",JSONObject.toJSONString(req));
        }
        return null;
    }


}
