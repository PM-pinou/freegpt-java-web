package com.chat.base.bean.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-24
 */
@Data
public class MjTaskInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 任务id
     */
    private String taskId;


    /**
     * 用户id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
     * 任务状态 0 -SUBMITTED 已提交,1-IN_PROGRESS 执行中,2-SUCCESS 完成,-1 -失败
     */
    private Integer status;

    /**
     * 生成图url
     */
    private String taskUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

    /**
     * 任务类型 0-普通生成 1-转换/放大
     */
    private Integer type;

    /**
     * 父亲id
     */
    private String parentId;

    /**
     * 垫图uri
     */
    private String parentPhoto;

    /**
     * prompt
     */
    private String prompt;
    /**
     * state
     */
    private String state;

    /**
     * prompt
     */
    private String action;

    private Integer parentIndex;

}
