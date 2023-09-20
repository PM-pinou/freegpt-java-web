package com.chat.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户登录
 */
@Data
public class UserInfoRegisterReq {
    /**
     * 账号
     */
    @NotNull(message = "账号不能为空")
    private String account;
    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;

    /**
     * 账号头像
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String username;

    private String appName;
    /**
     * 验证码
     */
    @NotNull(message = "验证码不能为空")
    private String verificationCode;
}
