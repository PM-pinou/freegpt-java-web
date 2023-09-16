package com.chat.base.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2023-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("token_channel_config")
public class TokenChannelConfig implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 用户的token
     */
    private String token;

    /**
     * channel用户表
     */
    private Long channelConfigId;


}
