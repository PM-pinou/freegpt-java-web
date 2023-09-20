package com.chat.base.bean.constants;

import java.math.BigDecimal;

public  class CommonConstant {

    /**
     * 登录成功 cookie的key
     */
    public final static String TOKEN = "blueCat_token";

    /**
     * 登录失效三小时
     */
    public final static int CACHE_TIME_OUT = 123125920;
    /**
     * 验证码实现五分钟
     */
    public final static int CACHE_VERIFICATION_TIME_OUT = 5;

    /**
     * 通用的敏感词过滤通道ID
     */
    public final static Long COMMON_FILTER_CHANNEL_ID = 22L;

    public final static Long COMMON_REGEX_CHANNEL_ID = 5232962780960694399L;

    /**
     * 公众号的二维码
     */
    public final static String OFFICIAL_ACCOUNT = "http://43.153.112.145:1002/wechat.jpeg";
    /**
     * 聊天的来源
     */
    public final static String CHAT_SOURCE = "chat";


    public final static String API_CHAT_SOURCE = "api_chat";

    public final static String PROMPT_TOOL_SOURCE = "AITool";

    /**
     * 限流规则ip
     */
    public final static String LIMIT_IP = "IP";

    /**
     * 如果用户没有登录的话 最多访问次数
     */
    public final static Integer LIMIT_IP_COUNT = 10;

    /**
     * 默认十秒才能发一次话
     */
    public final static Integer CHAT_LIMIT_TIME = 3;

    /**
     * 登录的作用域
     */
    public final static int LOGIN_SCOPE = 1;


    /**
     * 不登录的作用域
     */
    public final static int NO_LOGIN_SCOPE = 2;


    /**
     * 所有的作用域
     */
    public final static int ALL_SCOPE = 3;

    /**
     * 系统默认的token
     */
    public final static String SYS_TOKEN = "sk-system-token";

    /**
     * 上下文的条数
     */
    public final static Integer CONTENT_NUMBER = 6;

    /**
     * gpt计算价格的倍数
     */
    public final static BigDecimal multipleAmount = new BigDecimal("10000000");
    /**
     * gpt计算价格的精确位数
     */
    public final static int multipleAmountDigit = 7;

    // 用户注册的默认渠道，需要保证此渠道存在且绑定相应的gpt配置
    public static final Long DEFAULT_CHANNEL_ID = 1L;

    /**
     * mj的失败状态
     */
    public static final Integer DRAW_FAIL = -1;

    public static final int CONSUME = 1;
    public static final int RETURN_MONEY = -1;


}
