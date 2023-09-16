package com.chat.base.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ChannelConfigDelReq {

    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

}