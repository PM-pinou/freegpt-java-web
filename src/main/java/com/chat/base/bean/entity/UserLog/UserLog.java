package com.chat.base.bean.entity.UserLog;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;

/**
 * <p>
 * 
 * </p>
 *
 * @author lixin
 * @since 2023-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_log")
public class UserLog implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    private String ip;

    private String browserName;

    private String appName;

    /**
     * 业务id
     */
    private String biz;

    /**
     * 操作
     */
    private Integer op;

    @Column(name= "create_time")
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    private String createUser;


}
