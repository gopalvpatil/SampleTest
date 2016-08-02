package com.westernalliancebancorp.positivepay.utility.cache;


import com.westernalliancebancorp.positivepay.log.Loggable;
import net.sf.ehcache.*;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * User: gduggirala
 * Date: 3/6/14
 * Time: 9:32 AM
 */
@Component("positivePayCacheEventListenerFactory")
public class PositivePayCacheEventListenerFactory extends CacheEventListenerFactory implements InitializingBean{
    @Loggable
    private Logger logger;

    @Autowired
    CacheManager cacheManager;

    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return PositivePayCacheEventListener.positivePayCacheEventListener;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(cacheManager!=null){
            String[] caches=cacheManager.getCacheNames();
            if(caches == null || caches.length <=0){
                logger.error("No caches found!!! just returning");
                return;
            }else {
                for(String cacheName:caches){
                    Cache cache =  cacheManager.getCache(cacheName);
                    cache.getCacheEventNotificationService().registerListener(PositivePayCacheEventListener.positivePayCacheEventListener);
                    logger.info("Registered listener for cache "+cache.getName());
                }
            }
        }
    }
}
