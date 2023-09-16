package com.chat.base.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Map;

@Data
public class CacheUserInfoVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户账号
     */
    private String account;


    /**
     * 账号状态 0正常 1：禁止登录
     */
    private Integer status;


    /**
     * 用户等级
     */
    private String userLevel;

    /**
     * 用户对应的通道模型
     * 这个信息不能对外
     * 这个数据不能暴露到外部
     */
    private volatile Map<String,CacheGptModelConfigVo> gptModelConfigsMap;

    /**
     * 用户api token 信息
     */
    private volatile CacheGptApiTokenVo  gptApiTokenVo;

    /**
     * 登录的ip
     */
    private String ip;
}
