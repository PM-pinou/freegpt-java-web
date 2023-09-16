package com.chat.base.bean.req;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-21
 */
@Data
public class MjTaskInfoByStatusReq {

    private Integer page = 1;

    private Integer pageSize = 10;

    private Long id;
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 使用 的token
     */
    private String useToken;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 任务状态 0 -SUBMITTED 已提交,1-IN_PROGRESS 执行中,2-SUCCESS 完成,-1 -失败
     */
    private List<Integer> status;

    private LocalDateTime createTime;

    private LocalDateTime endTime;


}
