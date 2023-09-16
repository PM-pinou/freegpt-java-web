package com.chat.base.access;

import com.chat.base.RunnerTest;
import com.chat.base.bean.entity.UserAccessRule;
import com.chat.base.bean.util.UserAccessRuleUtil;
import com.chat.base.mapper.UserAccessRuleMapper;
import com.chat.base.service.impl.UserAccessRuleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class AccessTest extends RunnerTest {

    @Autowired
    private UserAccessRuleServiceImpl accessRuleService;

    @Resource
    private UserAccessRuleMapper ruleMapper;

    @Test
    public void addAccess(){
        int i = accessRuleService.addAccess(UserAccessRuleUtil.getAccessRuleGpt3(1234L));
        System.out.println(i);
    }


    @Test
    public void queryIds(){
        List<UserAccessRule> userAccessRules = ruleMapper.queryByIds(new HashSet<>(Arrays.asList(1656217949066317826L)));
        System.out.println(userAccessRules);
    }

}
