package com.westernalliancebancorp.positivepay.service;

import com.googlecode.ehcache.annotations.TriggersRemove;
import org.springframework.stereotype.Component;

/**
 * User: gduggirala
 * Date: 19/6/14
 * Time: 12:59 PM
 */
@Component
public interface ResetCaching {
    //void resetGetUserAccounts();
    void resetGetUserAccountsByCompanyId();
    void resetUserRoleCache();
}
