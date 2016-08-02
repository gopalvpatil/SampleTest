package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Permission;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 29/4/14
 * Time: 4:15 PM
 */
public interface SecurityService {
    boolean hasResourceAccess(String resourceName, Permission.TYPE TYPEName);
}
