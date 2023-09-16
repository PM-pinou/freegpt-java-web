package com.chat.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class UserAccessRuleReq {

    private Integer page = 1;


    private Integer pageSize = 20;


    private Long id;

    /**
     * 本次更新的用户id
     */
    private Long userId;

    /**
     * 模型标识
     */
    private String serviceType;

    /**
     * 开始生效时间
     */
    private LocalDateTime startEffectiveTime;

    /**
     * 有效结束时间
     */
    private LocalDateTime endEffectiveTime;
}
