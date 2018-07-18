package com.hqs.springboot.config;

import com.hqs.springboot.constants.DatabaseType;

/**
 * 保存一个线程安全的DatabaseType容器
 *
 * 用于保存数据源类型
 *
 */
public class DatabaseContextHolder {
    private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<>();

    public static void setDatabaseType(DatabaseType type) {
        contextHolder.set(type);
    }

    public static DatabaseType getDatabaseType() {
        return contextHolder.get();
    }
}
