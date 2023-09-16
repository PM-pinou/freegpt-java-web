package com.chat.base.bean.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public enum OpEnum {
    LOGIN(1,"登录"),
    REGISTER(2,"注册"),
    GPT3(3,"GPT回答失败"),
    COUPUN(4,"兑换卷消费"),
    COUPUNRETURN(5,"兑换卷删除"),
    COUPUN_CREATE(6,"创建兑换卷");
    private Integer op;
    private String desc;

}
