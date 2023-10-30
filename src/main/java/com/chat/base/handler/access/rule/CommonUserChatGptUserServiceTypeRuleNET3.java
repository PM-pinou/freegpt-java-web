package com.chat.base.handler.access.rule;

import com.chat.base.bean.constants.ModelServiceTypeConstant;
import com.chat.base.bean.constants.UserLevelConstant;
import com.chat.base.bean.vo.UserLevelAccessVo;
import org.springframework.stereotype.Component;


/**
 * 普通用户
 * 调用chat-gpt3模型的 每天 10次
 */
@Component(UserLevelConstant.COMMON_USER + ModelServiceTypeConstant.CHAT_NET_GPT_MODEL3)
public class CommonUserChatGptUserServiceTypeRuleNET3 implements UserServiceTypeRule<UserLevelAccessVo> {

    @Override
    public boolean rule(String userLevel, UserLevelAccessVo accessVo) {

        int useNumber = accessVo.getUseNumber().get();
        if(useNumber>0){
            return true;
        }
        return false;
    }
}


