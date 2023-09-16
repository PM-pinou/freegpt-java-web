package com.chat.base.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class GptModelConfigDelReq {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

}