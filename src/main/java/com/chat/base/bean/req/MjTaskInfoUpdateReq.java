package com.chat.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-21
 */
@Data
public class MjTaskInfoUpdateReq {
    private Long id;

    /**
     * 任务状态 0 -SUBMITTED 已提交,1-IN_PROGRESS 执行中,2-SUCCESS 完成,-1 -失败,3-删除
     */
    private Integer status;
}
