package com.hqs.springboot.utils.common;

import com.hqs.springboot.constants.Constants;
import com.xiaomi.miui.ad.infra.cache.CacheClient;
import com.xiaomi.miui.ad.infra.cache.CacheClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Tuple;

import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by Manu on 2018/7/23.
 */
public class RedisClientUtil {

    private final static Logger logger = LoggerFactory.getLogger(RedisClientUtil.class);

    private static CacheClient cacheClient = CacheClientFactory.getCacheClient(Constants.CACHE_CLIENT_STRING);


    public static long hset(final String key, final String field, final String value, final int expireSeconds) {
        return getCacheClient().hset(key, field, value, expireSeconds);
    }

    public static Map<String, String> hgetall(final String key, final Map<String, String> defaultValue) {
        return getCacheClient().hgetall(key, defaultValue);
    }

    public static Boolean setNX(String key, String value, int expireTime) {
        long code = cacheClient.setNX(key, value, 0, expireTime);
        return code == 1;
    }

    public static String set(String key, String value, int expireTime) {
        return getCacheClient().setString(key,value,expireTime);
    }

    public static String get(String key, String defaultValue){
        return getCacheClient().getString(key,defaultValue);
    }

    public static long delete(String key){
        return getCacheClient().delete(key);
    }

    public static long zadd(String key, final Map<String, Double> scoreMembers, long defaultValue, int expireSeconds) {
        return getCacheClient().zadd(key, scoreMembers, defaultValue, expireSeconds);
    }

    public static long zrem(String key, long defaultValue, final String... member) {
        return getCacheClient().zrem(key, defaultValue, member);
    }

    public static long zcard(String key, long defaultValue) {
        return getCacheClient().zcard(key, defaultValue);
    }

    public static Set<Tuple> zrangeWithScores(String key, final long start, final long end, Set<Tuple> defaultValue) {
        return getCacheClient().zrangeWithScores(key, start, end, defaultValue);
    }

    public static String setObject(String key, Object value, int expireTime) {
        return getCacheClient().set(key,serialize(value),expireTime);
    }

    public static Object getObject(String key, Object defaultValue){
        byte[] bytes = getCacheClient().get(key,serialize(defaultValue));
        return bytes != null ? deserialize(bytes) : null;
    }

    public static long deleteObject(String key){
        return getCacheClient().delete(key);
    }

    // 单例 * 模式 ？
    public static CacheClient getCacheClient(){
        if(cacheClient == null){
            cacheClient = CacheClientFactory.getCacheClient(Constants.CACHE_CLIENT_STRING);
            if(cacheClient == null){
                logger.error("cacheClientFactory.getCacheClient fail");
            }
        }
        return cacheClient;
    }

    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            logger.error("deserialize session error");
            e.printStackTrace();
        } finally {
            try {
                ois.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private static byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("serialize session error");
            logger.error(e.getMessage());
            throw new RuntimeException("serialize session error", e);
        } finally {
            try {
                oos.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

