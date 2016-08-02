package com.westernalliancebancorp.positivepay.web.security;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.PermissionDao;
import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.dao.SsoAttributeDao;
import com.westernalliancebancorp.positivepay.dao.SsoDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Sso;
import com.westernalliancebancorp.positivepay.model.SsoAttribute;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;
import com.westernalliancebancorp.positivepay.utility.GsonUtility;

/**
 * BottomLineAuthenticationProvider is used to Authenticate the user.
 * We don't have the user name or the user password, all that we receive is "key". This key should be saved in DB (Table name: SSO) depending upon the key
 * value (UID) we will fetch the UID and check if the key is created before valid timeout.
 * <p/>
 * Once the key is valid then read the "username" attribute from the list of attributes that are saved against this "UID" in the SsoAttributes table
 * Once the username attribute is found then we will load the user details and roles using {@link PositivePayUserDetailsServiceImpl}.
 *
 * @author Giridhar Duggirala
 */

@Service(value = "bottomLineAuthenticationProvider")
public class BottomLineAuthenticationProvider implements AuthenticationProvider, InitializingBean {
    @Loggable
    private Logger logger;
    @Autowired
    private SsoDao ssoDao;
    @Value("${positivepay.valid.timeout.in.millis}")
    private long validUidTimeoutInMillis = 30000l;
    @Value("${positivepay.bottomline.user.attribute.name}")
    private String usernameAttributeName = "username";
    @Value("${positivepay.bottomline.institutionid.attribute.name}")
    private String institutionIdName = "institutionid";
    @Value("${positivepay.bottomline.csv.params.to.read}")
    private String csvParamsToRead;

    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private SsoAttributeDao ssoAttributeDao;
    @Autowired
    UserService userService;

    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;
    @Value("${positivepay.cookie.ttl}")
    private long cookieTtl = 7200000; //2hrs
    @Autowired
    RoleDao roleDao;
    @Autowired
    PermissionDao permissionDao;

    @Value("${positivepay.cookie.max.ttl}")
    private long cookieMaxTtl = 28800000; //8hrs
    private boolean throwExceptionWhenTokenRejected = false;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        if ((authentication instanceof BottomLineAuthenticationToken)) {
            return getBottomLineAuthentication(authentication);
        }

        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            return getPreAuthenticatedAuthentication(authentication);
        }
        return null;
    }

    private Authentication getPreAuthenticatedAuthentication(Authentication authentication) {
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
        Affidavit affidavit = GsonUtility.getAffidavit(affidavitSecurityUtility.decrypt(((String) authentication.getPrincipal()), true));
        if (verifyAffidavit(affidavit)) {
        	AffidavitThreadLocal.set(affidavit);
            List<Permission> permissionList = userService.findResourcesByUser(affidavit.getUserName());
            affidavit.setPermissionList(permissionList);
            UserDetails userDetails = userDetailsService.loadUserByUsername(affidavit.getUserName());
            authentication.setAuthenticated(Boolean.TRUE);
            BottomLineAuthenticationToken bottomLineAuthenticationToken = new BottomLineAuthenticationToken(userDetails.getUsername(), authentication.getCredentials(), userDetails.getAuthorities());
            return bottomLineAuthenticationToken;
        } else {
            return null;
        }
    }

    private boolean verifyAffidavit(Affidavit affidavit) {
        if (affidavit.getUid() != null && !affidavit.getUid().isEmpty()) {
            Sso sso = ssoDao.findByUid(affidavit.getUid());
            String status = sso.getStatus().name();
            logger.info("UID: " + sso.getUid() + " with the status : " + sso.getStatus());
            if (status.equals(Sso.Status.LOGGED_OUT.name()) || status.equals(Sso.Status.BOTTOM_LINE_KILL.name()) || status.equals(Sso.Status.VALIDATED_FAILED.name()) || status.equals(Sso.Status.CREATED.name())) {
                logger.info("Ooop's cookie is now stale as BottomLine spit on it");
                if (throwExceptionWhenTokenRejected) {
                    throw new BadCredentialsException("No pre-authenticated credentials found in request.");
                }
                return Boolean.FALSE;
            }
        } else {
            logger.debug("This user is authenticated by either DB or by LDAP, so there is not UID.");
        }
        long ttl = affidavit.getTtl();
        long maxTtl = affidavit.getMaxTtl();
        long systemTime = System.currentTimeMillis();
        //First check if maxTtl is still greater than system time and use ideal time has not exceeded ttl
        if (systemTime < maxTtl &&  systemTime < ttl) {
            //As maxTtl is more than system time and user idle time  has not exceeded ttl, renew the cookie with new ttl
            logger.debug("Cookie is hot but needs to reheat by increasing ttl");
            //Just increase ttl and dont worry about ttl being more than maxttl. A check systemtime against maxttl will take care of that.
            affidavit.setTtl(systemTime + cookieTtl);
            //renewCookie(affidavit);
            AffidavitThreadLocal.markTTLChanged(true);
        } else {
            logger.debug("Cookie stale... create a new one");
            if (throwExceptionWhenTokenRejected) {
                throw new BadCredentialsException("Invalid cookie please renew, time exceeded");
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void renewCookie(Affidavit affidavit) {
        long ttl = affidavit.getTtl();
        long maxTtl = affidavit.getMaxTtl();
        long systemTime = System.currentTimeMillis();
        long renewalTime = 0;
        if (systemTime < ttl) {
            logger.info("Why the hell cookie is here? it still fresh and hot");
            return;
        }
        int iterations = 0;
        do {
            iterations++;
            renewalTime = ttl + (cookieTtl * iterations);
        } while (renewalTime < systemTime);
        if (renewalTime > maxTtl) {
            logger.info("Cookie may be over burnt reshaping it");
            renewalTime = maxTtl;
        }
        affidavit.setTtl(renewalTime);
    }

    private Authentication getBottomLineAuthentication(Authentication authentication) {
        long currentTimeInMillis = System.currentTimeMillis();
        if (authentication.getCredentials() == null || ((String) authentication.getCredentials()).isEmpty()) {
            throw new BadCredentialsException("Key is not received as part of the request.");
        }
        Sso sso = ssoDao.findByUid((String) authentication.getCredentials());
        long timeInMillis = sso.getCreateTimeInMillis();
        if ((!sso.getStatus().name().equals(Sso.Status.BOTTOM_LINE_KILL) || !sso.getStatus().name().equals(Sso.Status.VALIDATED_FAILED)) &&
                (currentTimeInMillis > (timeInMillis + validUidTimeoutInMillis))) {
            sso.setStatus(Sso.Status.VALIDATED_FAILED);
            ssoDao.update(sso);
            throw new BadCredentialsException("Key expired, please make a new request.");
        }
        authentication.setAuthenticated(Boolean.TRUE);
        sso.setStatus(Sso.Status.VALIDATED_SUCCESS);
        ssoDao.update(sso);
        List<SsoAttribute> ssoAttributeList = ssoAttributeDao.findBySsoId(sso.getId());
        String corporateUserName = "";
        String institutionId = "";
        for (SsoAttribute ssoAttribute : ssoAttributeList) {
            if (ssoAttribute.getName().equals(usernameAttributeName)) {
                corporateUserName = ssoAttribute.getValue();
            }
            if (ssoAttribute.getName().equals(institutionIdName)) {
                institutionId = ssoAttribute.getValue();
            }
        }
        List<UserDetail> userDetailList = userDetailDao.findBy(corporateUserName, institutionId);
        if(userDetailList == null || userDetailList.isEmpty()) {
            throw new SecurityException(String.format("No user name found with the supplied corporateUserName %s and institutionId %s", corporateUserName, institutionId));
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDetailList.get(0).getUserName());
        BottomLineAuthenticationToken bottomLineAuthenticationToken = new BottomLineAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        return bottomLineAuthenticationToken;
    }

    @Override
    public boolean supports(final Class<? extends Object> authentication) {
        return ((BottomLineAuthenticationToken.class.isAssignableFrom(authentication)) || (PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication)));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
