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
@TableName("channel_model_config")
public class ChannelModelConfig implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 通道id
     */
    private Long channelConfigId;

    /**
     * 模型表id
     */
    private Long modelConfigId;


}
