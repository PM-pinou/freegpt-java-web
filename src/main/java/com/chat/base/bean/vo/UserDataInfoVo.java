package com.chat.base.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Map;

@Data
public class UserDataInfoVo {


    /**
     * 名称
     */
    private String name;

    /**
     * 数据
     */
    private Integer data;


}
