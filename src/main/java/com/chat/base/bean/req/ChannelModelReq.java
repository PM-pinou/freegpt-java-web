package com.chat.base.bean.req;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-03
 */
@Data
public class ChannelModelReq implements Serializable {


    /**
     * 通道id
     */
    @NonNull
    private Long channelConfigId;

    /**
     * 模型表id
     */
    @NonNull
    private List<Long> modelConfigId;


}
