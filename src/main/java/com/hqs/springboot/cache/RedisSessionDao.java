package com.hqs.springboot.cache;


import com.hqs.springboot.constants.Constants;
import com.hqs.springboot.utils.common.RedisClientUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 *  EnterpriseCacheSessionDAO 提供了缓存功能的会话维护，默认情况下使用 MapCache 实现，内部使用 ConcurrentHashMap 保存缓存的会话
 *  因为继承了 CacheSessionDAO ，所以首先会在缓存中查找，缓存中发现指定数据如果不存在才会再去到数据库中查找
 */
public class RedisSessionDao extends EnterpriseCacheSessionDAO {

    private final int sessionTimeout;

    public RedisSessionDao(int sessionTimeout) {
        super();
        this.sessionTimeout = sessionTimeout;
    }

    private static final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        logger.info("创建seesion,id=[{}]",session.getId().toString());
        try {
            RedisClientUtil.setObject(String.valueOf(session.getId()),session,sessionTimeout);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return sessionId;
    }


    @Override
    protected void doUpdate(Session session) {
        logger.info("更新seesion,id=[{}]",session.getId().toString());
        try {
            RedisClientUtil.setObject(String.valueOf(session.getId()),session, Constants.REDIS_EXPIRE_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(Session session) {
        logger.info("删除seesion,id=[{}]",session.getId().toString());
        try {
            RedisClientUtil.deleteObject(String.valueOf(session.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {

        logger.info("获取seesion,id=[{}]",sessionId.toString());
        Session session = null;
        try {
            session = (Session) RedisClientUtil.getObject(String.valueOf(sessionId),null);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return session;
    }

}
