package com.chat.base.bean.vo;

import lombok.Data;

@Data
public class InterceptRecordVo {

    private Integer number;
    private String source;
    private Long userId;
    private String ip;
}
