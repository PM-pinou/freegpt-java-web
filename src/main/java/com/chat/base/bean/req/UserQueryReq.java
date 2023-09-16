package com.chat.base.bean.req;


import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 用户查询请求实体
 */
@Data
public class UserQueryReq {

    private Integer page = 1;


    private Integer pageSize = 20;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 电话信息
     */
    private String phone;

    /**
     * 用户等级
     */
    private String userLevel;


    /**
     * 账号
     */
    private String account;

    /**
     * 搜索开始时间
     */
    private LocalDateTime startTime;

    /**
     * 搜索结束时间
     */
    private LocalDateTime endTime;
}
