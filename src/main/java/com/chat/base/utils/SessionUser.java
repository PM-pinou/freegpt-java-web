package com.chat.base.utils;

import com.chat.base.bean.vo.CacheUserInfoVo;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SessionUser {

    private static ThreadLocal<CacheUserInfoVo> sessionUser = new ThreadLocal<>();

    public static void setSessionUserInfo(CacheUserInfoVo adminUserVO) {
        sessionUser.set(adminUserVO);
    }

    public static CacheUserInfoVo get(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo==null){
            return null;
        }
        return CacheUtil.getIfPresent(String.valueOf(cacheUserInfoVo.getId()));
    }

    public static Long getUserId(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return null;
        }
        return cacheUserInfoVo.getId();
    }

    /**
     * 用户登录的信息
     * @return
     */
    public static Optional<CacheUserInfoVo> getUserInfoVO(){
        CacheUserInfoVo cacheUserInfoVo = get();
        if(cacheUserInfoVo == null){
            return Optional.empty();
        }
        return Optional.of(cacheUserInfoVo);
    }

    public static Optional<String> getUserName(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return Optional.empty();
        }
        return Optional.of(cacheUserInfoVo.getUsername());
    }

    public static String getAccount(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return null;
        }
        return cacheUserInfoVo.getAccount();
    }

    public static void remove(){
        sessionUser.remove();
    }


    /**
     * 管理账号集合，目前直接写死到项目中
     */
    public static Set<String> adminSet = new HashSet<>();


    //新增管理员
    static {
        adminSet.add("xxxx");
    }

    /**
     * 判断是否是管理账号
     * @param account
     * @return
     */
    public static boolean isAdmin(String account){
        if(StringUtils.isEmpty(account)){
            return false;
        }
        return adminSet.contains(account);
    }

    public static boolean isAdmin(){
        String account = getAccount();
        return isAdmin(account);
    }

}
