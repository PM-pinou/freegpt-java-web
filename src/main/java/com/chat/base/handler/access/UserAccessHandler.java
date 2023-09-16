package com.chat.base.handler.access;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.bean.entity.UserAccessRule;
import com.chat.base.bean.req.UserAccessRuleReq;
import com.chat.base.bean.req.UserAccessRuleUpdateReq;
import com.chat.base.bean.vo.UserLevelAccessVo;
import com.chat.base.service.impl.UserAccessRuleServiceImpl;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserAccessHandler {

    @Resource
    private UserAccessRuleServiceImpl ruleService;

    private UserAccessRule updateReqToEntity(UserAccessRuleUpdateReq updateReq){
        UserAccessRule entity = new UserAccessRule();
        entity.setId(updateReq.getId());
        entity.setUseNumber(updateReq.getUseNumber());
        entity.setStartEffectiveTime(updateReq.getStartEffectiveTime());
        entity.setEndEffectiveTime(updateReq.getEndEffectiveTime());
        return entity;
    }

    /**
     * 更新
     * @param updateReq
     * @return
     */
    public boolean updateUserAccess(UserAccessRuleUpdateReq updateReq){
        UserAccessRule entity = updateReqToEntity(updateReq);
        entity.setUpdateUser(SessionUser.getAccount());
        entity.setUpdateTime(LocalDateTime.now());
        return ruleService.updateBatchById(Arrays.asList(entity));
    }


    /**
     * 获取用户的模型访问配置
     * @param userId
     * @return
     */
    public Map<String, UserLevelAccessVo> getUserAccessRule(Long userId){
        List<UserLevelAccessVo> userLevelAccessVos = ruleService.getAllAccessRuleByUserId(userId);
        return userLevelAccessVos.stream()
                .collect(Collectors.toMap(UserLevelAccessVo::getServiceType, Function.identity()));
    }


    public IPage<UserAccessRule> queryUserAccess(UserAccessRuleReq req){
        try {
            UserAccessRule userLog = new UserAccessRule();
            userLog.setUserId(req.getUserId());
            userLog.setServiceType(req.getServiceType());
            QueryWrapper<UserAccessRule> queryWrapper = new QueryWrapper<>();
            queryWrapper.setEntity(userLog);
            if(Objects.nonNull(req.getStartEffectiveTime())){
                queryWrapper.ge("start_effective_time",req.getStartEffectiveTime());
            }
            if(Objects.nonNull(req.getEndEffectiveTime())){
                queryWrapper.le("end_effective_time",req.getEndEffectiveTime());
            }
            return ruleService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), queryWrapper);
        }catch (Exception e){
            log.error("addSystemLog error req={}", JSONObject.toJSONString(req));
        }
        return null;
    }

}
