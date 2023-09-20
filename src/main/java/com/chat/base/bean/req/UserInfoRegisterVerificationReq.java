package com.chat.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户登录
 */
@Data
public class UserInfoRegisterVerificationReq {
    /**
     * 账号
     */
    @NotNull(message = "账号不能为空")
    private String account;

}
