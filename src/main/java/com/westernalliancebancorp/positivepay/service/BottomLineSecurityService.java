package com.westernalliancebancorp.positivepay.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * BottomLineSecurityService is
 *
 * @author Giridhar Duggirala
 */

public interface BottomLineSecurityService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String getUid(Map requestParameters);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handleLogout(String uid);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handleBottomLineKill(String uid);
}
