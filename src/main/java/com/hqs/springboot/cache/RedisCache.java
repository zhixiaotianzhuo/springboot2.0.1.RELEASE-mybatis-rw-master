package com.hqs.springboot.cache;

import com.hqs.springboot.utils.common.RedisClientUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Manu on 2018/7/17.
 */
public class RedisCache<K, V> implements Cache<K, V> {

    private final Integer sessionTimeout;

    public RedisCache(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    public V get(K key) throws CacheException {
        Object object = RedisClientUtil.getObject(String.valueOf(key), null);
        System.out.println("result:" + object);
        return object != null ? (V) object : null;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        RedisClientUtil.setObject(String.valueOf(key), value, sessionTimeout);
        return value;
    }

    @Override
    public V remove(K key) throws CacheException {
        Object value = RedisClientUtil.getObject(String.valueOf(key), null);
        RedisClientUtil.deleteObject(String.valueOf(key));
        return value != null ? (V) value : null;
    }

    @Override
    public void clear() throws CacheException {
    }

    @Override
    public int size() {
        return 1024 * 1024 * 30;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

}
