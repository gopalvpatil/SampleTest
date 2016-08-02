package com.westernalliancebancorp.positivepay.utility;

import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;
import com.westernalliancebancorp.positivepay.web.security.BottomLineAuthenticationToken;
import com.westernalliancebancorp.positivepay.web.security.PositivePayUser;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * @author Giridhar Duggirala
 */

public class SecurityUtility {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(AuditListener.class);

    public static String getPrincipal() {
        String name = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                name = (String) principal;
            } else if (principal instanceof PositivePayUser) {
                PositivePayUser positivePayUser = (PositivePayUser) principal;
                return positivePayUser.getUsername();
            } else {
                throw new RuntimeException("Should be an instance of String or PositivePayUser object but found a different one. " + principal.getClass());
            }
        } else {
            logger.info("Cannot find the authentication info, trying to get the details from Thread Local");
            name = PositivePayThreadLocal.get();
            if (name == null || name.isEmpty()) {
                throw new RuntimeException("Exception!! no user info found either in security context or ThreadLocal, please check");
            }
        }
        return name;
    }

    public static void setPrincipal(String principal) {
        if (principal == null) {
            throw new NullPointerException("Principal value passed is null please check");
        }
        PositivePayThreadLocal.set(principal);
    }

    public static String setTransactionId() {
        String transactionId = TransactionIdThreadLocal.get();
        if (transactionId != null) {
            logger.info("Transaction id " + transactionId);
            TransactionIdThreadLocal.set(transactionId);
        } else {
            logger.info("Generated Transaction id " + transactionId);
            transactionId = RandomStringUtils.random(6, Boolean.TRUE, Boolean.TRUE);
            TransactionIdThreadLocal.set(transactionId);
        }
        return transactionId;
    }

    /*public static boolean isLoggedInUserAdmin() {
        String name = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                name = (String) principal;

            } else if (principal instanceof PositivePayUser) {
                PositivePayUser positivePayUser = (PositivePayUser) principal;
                Collection<GrantedAuthority> grantedAuthorites = positivePayUser.getAuthorities();
                for (GrantedAuthority grantedAuthority : grantedAuthorites) {
                    if (grantedAuthority.getAuthority().equals(Role.Roles.ROLE_ADMIN.name())) {
                        return Boolean.TRUE;
                    }
                }

            } else {
                throw new RuntimeException("Should be an instance of String or PositivePayUser object but found a different one. " + principal.getClass());
            }
        }
        return Boolean.FALSE;
    }*/

    public static boolean isLoggedInUserCorporateAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication instanceof BottomLineAuthenticationToken) {
                Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
                for (GrantedAuthority grantedAuthority : grantedAuthorities) {
                    if (grantedAuthority.getAuthority().equals(Role.Roles.ROLE_CORPORATE_ADMIN.name())) {
                        return Boolean.TRUE;
                    }
                }
            } else {
                return Boolean.FALSE;
                //TODO: Check with BottomLineAuthenation and LdapAuthentication to see if we are creating something different token than BottomLineAuthenticationToken
            }
        }
        return Boolean.FALSE;
    }

    public static boolean isLoggedInUserBankAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication instanceof BottomLineAuthenticationToken) {
                Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
                for (GrantedAuthority grantedAuthority : grantedAuthorities) {
                    if (grantedAuthority.getAuthority().equals(Role.Roles.ROLE_BANK_ADMIN.name())) {
                        return Boolean.TRUE;
                    }
                }
            } else {
                return Boolean.FALSE;
                //TODO: Check with BottomLineAuthenation and LdapAuthentication to see if we are creating something different token than BottomLineAuthenticationToken
            }
        }
        return Boolean.FALSE;
    }

    public static boolean isUserBankAdmin(String userName, UserDetailDao userDao) {
        UserDetail userDetail = userDao.findUserRoleByUserName(userName);
        Role baseRole = userDetail.getBaseRole();
        if (baseRole.getName().equals(Role.Roles.ROLE_BANK_ADMIN.name())) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
    
    public static boolean hasPermission(Permission.NAME permissionName) {
    	Affidavit affidavit = AffidavitThreadLocal.get();
        for (Permission permission : affidavit.getPermissionList()) {
            if ((permission.getName()).name().equalsIgnoreCase(permissionName.name())) {
               return true;
            }
        }
        return false;
    }
}
