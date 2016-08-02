package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;
import com.westernalliancebancorp.positivepay.utility.GsonUtility;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PositivePayPreAuthenticatedAuthenticationProvider is
 *
 * @author Giridhar Duggirala
 */
@Deprecated
@Service("positivePayPreAuthenticationProvider")
public class PositivePayPreAuthenticatedAuthenticationProvider implements AuthenticationProvider, InitializingBean, Ordered {
    @Loggable
    private Logger logger;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;
    private boolean throwExceptionWhenTokenRejected = false;
    @Autowired
    RoleDao roleDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("PreAuthenticated authentication request: " + authentication);
        }
        if (authentication.getPrincipal() == null) {
            logger.debug("No pre-authenticated principal found in request.");

            if (throwExceptionWhenTokenRejected) {
                throw new BadCredentialsException("No pre-authenticated principal found in request.");
            }
            return null;
        }

        if (authentication.getCredentials() == null) {
            logger.debug("No pre-authenticated credentials found in request.");

            if (throwExceptionWhenTokenRejected) {
                throw new BadCredentialsException("No pre-authenticated credentials found in request.");
            }
            return null;
        }
        Affidavit affidavit = GsonUtility.getAffidavit(affidavitSecurityUtility.decrypt(((String)authentication.getPrincipal()), true));
        UserDetails userDetails = userDetailsService.loadUserByUsername(affidavit.getUserName());
        authentication.setAuthenticated(Boolean.TRUE);
        BottomLineAuthenticationToken bottomLineAuthenticationToken = new BottomLineAuthenticationToken(userDetails.getUsername(), authentication.getCredentials(), userDetails.getAuthorities());
        return bottomLineAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
