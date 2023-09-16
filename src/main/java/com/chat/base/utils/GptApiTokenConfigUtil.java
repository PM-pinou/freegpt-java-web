package com.chat.base.utils;

import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 用户token的缓存
 */
public class GptApiTokenConfigUtil {

    public static Cache<String, CacheGptApiTokenVo> cache ;

    static {
        // 初始化 共用的 缓存
        cache  = CacheBuilder.newBuilder().initialCapacity(10).expireAfterAccess(720, TimeUnit.MINUTES).build();
    }

}
