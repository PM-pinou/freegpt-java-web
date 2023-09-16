package com.chat.base.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.StandardCharsets;

/**
 * 布隆过滤器
 */
public class GuavaBloomFilterUtil {


    static BloomFilter<CharSequence> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.US_ASCII), 100000, 0.0444D);

    public static boolean putValue(String value){
        return bloomFilter.put(value);
    }

    /**
     * 存在返回true
     * @param value
     * @return
     */
    public static boolean exist(String value){
        return bloomFilter.mightContain(value);
    }

}
