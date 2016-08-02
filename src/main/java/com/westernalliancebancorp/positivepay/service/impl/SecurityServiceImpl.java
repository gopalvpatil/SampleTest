package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.service.SecurityService;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 29/4/14
 * Time: 4:16 PM
 */
public class SecurityServiceImpl implements SecurityService {
    @Override
    public boolean hasResourceAccess(String resourceName, Permission.TYPE TYPEName) {
        return false;
    }
}
