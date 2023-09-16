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
 * @since 2023-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gpt_api_token_config")
public class GptApiTokenConfig implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * token名称
     */
    private String name;

    private String token;

    /**
     * 访问次数
     */
    private Long visitNumber;

    /**
     * 余额
     */
    private Long balance;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    private String createUser;

    private LocalDateTime createTime;

    private String updateUser;

    private LocalDateTime updateTime;

    /**
     * 1:有效 0：无效
     */
    private Integer status;


}
