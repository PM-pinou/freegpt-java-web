package com.chat.base.utils;

import java.util.HashSet;
import java.util.Set;

public class RiskControlUtil {

    /**
     * 黑名单
     */
    private static Set<String> blacklist = new HashSet<>();

    public static synchronized void put(String key){
        try {
            blacklist.add(key);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 该应用是否在黑名单中
     * @param key
     */
    public static boolean get(String key){
        try {
            return blacklist.contains(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
