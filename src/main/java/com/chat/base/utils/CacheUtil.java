package com.chat.base.utils;

import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class CacheUtil {

    private static Cache<String, CacheUserInfoVo> cache = CacheBuilder.newBuilder().expireAfterWrite(CommonConstant.CACHE_TIME_OUT, TimeUnit.SECONDS).build();

    private static Cache<String, String > cacheVerification = CacheBuilder.newBuilder().expireAfterWrite(CommonConstant.CACHE_VERIFICATION_TIME_OUT, TimeUnit.MINUTES).build();

    public static void put(String key, CacheUserInfoVo value) {
        cache.put(key, value);
    }

    public static void putVerification(String key, String value) {
        cacheVerification.put(key, value);
    }

    public static CacheUserInfoVo getIfPresent(String key){
        return cache.getIfPresent(key);
    }
    public static String getVerification(String key) {
        return cacheVerification.getIfPresent(key);
    }

    public static ConcurrentMap<String, String > getVerificationAll() {
        return cacheVerification.asMap();
    }


    /**
     * 获取所有的登录的用户信息
     * @return
     */
    public static ConcurrentMap<String, CacheUserInfoVo>  getAllCacheUserInfo(){
        return cache.asMap();
    }

    /**
     * 获取所有的缓存的用户信息
     * @return
     */
    public static Collection<CacheUserInfoVo> getUserInfo(){
        return cache.asMap().values();
    }


    public static void invalidate(String key){
        cache.invalidate(key);
    }
    /**
     * 删除登录缓存
     * @param key
     */
    public static void removeCache(String key){
        cache.asMap().remove(key);
    }
    /**
     * 删除验证吗
     * @param key
     */
    public static void removeVerificationCache(String key){
        cacheVerification.asMap().remove(key);
    }

}
