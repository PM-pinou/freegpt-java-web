package com.chat.base.bean.req;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户查询请求实体
 */
@Data
public class PromptRecordUserReq {

    private Integer page = 1;


    private Integer pageSize = 20;

    /**
     * 会话id
     */
    private String conversationId;
    /**
     * 来源
     */
    private String source;
    /**
     * token
     */
    private String token;


    /**
     * 模型标识
     */
    private String serviceType;

    /**
     * 搜索开始时间
     */
    private LocalDateTime startTime;

    /**
     * 搜索结束时间
     */
    private LocalDateTime endTime;
}
