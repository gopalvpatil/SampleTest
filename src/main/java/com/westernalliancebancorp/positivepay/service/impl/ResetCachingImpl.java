package com.westernalliancebancorp.positivepay.service.impl;

import com.googlecode.ehcache.annotations.TriggersRemove;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.ResetCaching;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 * User: gduggirala
 * Date: 19/6/14
 * Time: 1:00 PM
 */
@Service
public class ResetCachingImpl implements ResetCaching {
    @Loggable
    Logger logger;

    /*@Override
    @TriggersRemove(cacheName = "getUserAccounts")
    public void resetGetUserAccounts() {
       logger.info("Reset the getUserAccounts");
    }*/

    @Override
    @TriggersRemove(cacheName = "getUserAccountsByCompanyId")
    public void resetGetUserAccountsByCompanyId() {
        logger.info("Reset the getUserAccountsByCompanyId");
    }

    @Override
    @TriggersRemove(cacheName = "userRoles")
    public void resetUserRoleCache() {
        logger.info("Reset the userRoles");
    }
}
