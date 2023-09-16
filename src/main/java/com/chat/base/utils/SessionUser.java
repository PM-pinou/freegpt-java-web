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
    private static Set<String> adminSet = new HashSet<>();

    static {
        adminSet.add("18589224217");// laoban
        adminSet.add("15889198403");// liuzilin
        adminSet.add("18230675983");// lixin
        adminSet.add("13212631576");// luoshixin
        adminSet.add("15515600808"); // admin1
        adminSet.add("15565139513"); // admin2
        adminSet.add("827034613@qq.com");//测试人员
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
