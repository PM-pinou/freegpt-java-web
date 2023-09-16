package com.chat.base.handler.access.rule;

import com.chat.base.bean.constants.ModelServiceTypeConstant;
import com.chat.base.bean.constants.UserLevelConstant;
import com.chat.base.bean.vo.UserLevelAccessVo;
import org.springframework.stereotype.Component;


/**
 * 尊贵的付费用户
 * 调用chat-gpt3模型的 随便玩
 */
@Component(UserLevelConstant.PERMANENT_USERS + ModelServiceTypeConstant.CHAT_GPT_MODEL3)
public class PermanentUserChatGptUserServiceTypeRule3 implements UserServiceTypeRule<UserLevelAccessVo> {

    @Override
    public boolean rule(String userLevel, UserLevelAccessVo accessVo) {
        return true;
    }
}
