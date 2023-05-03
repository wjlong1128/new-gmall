package com.wjl.gmall.acitvity.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
public class CacheHelper {
    public static ConcurrentHashMap<String, Object> cacheMap = new ConcurrentHashMap<>();

    public static void put(String key, Object object) {
        cacheMap.put(key, object);
    }

    public static void clear() {
        cacheMap.clear();
    }

    public static Object remove(String key) {
        return cacheMap.remove(key);
    }

    public static Object get(String key){
        return cacheMap.get(key);
    }

}
