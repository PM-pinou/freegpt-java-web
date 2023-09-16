package com.chat.base.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mj_task_info")
public class MjTaskInfo implements Serializable {

    private static final long serialVersionUID=1L;

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
     * 任务状态 0 -SUBMITTED 已提交,1-IN_PROGRESS 执行中,2-SUCCESS 完成,-1 -失败,3-删除
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String prompt;

    private Long modelId;

    private String taskUrl;

    /**
     * 任务结束时间
     */
    private LocalDateTime finishTime;

    /**
     * 任务类型 0-普通生成 1-转换/放大
     */
    private Integer type;

    /**
     * 父亲id
     */
    private String parentId;

    private String parentPhoto;

    private Integer parentIndex;

    /**
     * state
     */
    private String state;

    /**
     * prompt
     */
    private String action;


}
