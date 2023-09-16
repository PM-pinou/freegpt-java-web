package com.chat.base.bean.req;


import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

/**
 * 用户日志查询请求实体
 */
@Data
public class UserLogReq {

    private Integer page = 1;


    private Integer pageSize = 20;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 产品
     */
    private String appName;

    private String browserName;

    private String biz;

    /**
     * 动作
     */
    private Integer op;

    /**
     * 搜索开始时间
     */
    private LocalDateTime startTime;

    /**
     * 搜索结束时间
     */
    private LocalDateTime endTime;
}
