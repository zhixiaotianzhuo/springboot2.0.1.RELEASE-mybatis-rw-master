package com.hqs.springboot.cache;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

/**
 * Created by Manu on 2018/7/17.
 */
public class RedisCacheManager extends AbstractCacheManager {

    private final Integer sessionTimeout ;

    public RedisCacheManager(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    protected Cache createCache(String s) throws CacheException {
        return new RedisCache(sessionTimeout);
    }

}
