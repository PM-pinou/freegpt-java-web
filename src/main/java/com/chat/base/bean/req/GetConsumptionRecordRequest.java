package com.chat.base.bean.req;

import lombok.Data;

@Data
public class GetConsumptionRecordRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String userToken;
}
