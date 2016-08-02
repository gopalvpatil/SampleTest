package com.westernalliancebancorp.positivepay.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.PermissionDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.UserHistoryDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserHistory;
import com.westernalliancebancorp.positivepay.service.EmulatedCookieService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;
import com.westernalliancebancorp.positivepay.utility.GsonUtility;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/13/14
 * Time: 12:57 PM
 */
@Service
public class EmulatedCookieServiceImpl implements EmulatedCookieService {
    @Loggable
    private Logger logger;

    @Autowired
    UserHistoryDao userHistoryDao;

    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;

    @Autowired
    private UserDetailDao userDetailDao;

    @Value("${positivepay.cookie.ttl}")
    private long cookieTtl = 7200000; //2hrs

    @Value("${positivepay.cookie.max.ttl}")
    private long cookieMaxTtl = 28800000; //8hrs

    @Autowired
    UserService userService;

    @Autowired
    PermissionDao permissionDao;

    @Override
    public String createEmulationCookie(String userName) throws UnsupportedEncodingException {
        Affidavit affidavit = null;
        if (canCreateEmulationCookie(userName)) {
            List<Permission> permissionList = getUserPermission(userName);
            affidavit = new Affidavit(userName, (System.currentTimeMillis() + cookieTtl), (System.currentTimeMillis() + cookieMaxTtl), Affidavit.TYPE.EMULATED.toString(), permissionList);
            affidavit.setCreatedByUserName(SecurityUtility.getPrincipal());
        }
        String cookieValue = affidavitSecurityUtility.encrypt(GsonUtility.toString(affidavit), true);
        updateEmulatedHistory(affidavit);
        return cookieValue;
    }

    @Override
    public String exitEmulationCookie() throws UnsupportedEncodingException {
        Affidavit affidavit = AffidavitThreadLocal.get();
        String createdByUserName = affidavit.getCreatedByUserName();
        logger.info("createdByUserName = " + createdByUserName);
        List<Permission> permissionList = getUserPermission(createdByUserName);
        Affidavit newAffidavit = new Affidavit(createdByUserName, (System.currentTimeMillis() + cookieTtl), (System.currentTimeMillis() + cookieMaxTtl), Affidavit.TYPE.NORMAL.toString(), permissionList);
        String cookieValue = affidavitSecurityUtility.encrypt(GsonUtility.toString(newAffidavit), true);
        updateEmulatedHistory(newAffidavit);
        return cookieValue;
    }

    private void updateEmulatedHistory(Affidavit affidavit) {
        UserHistory userHistory = new UserHistory();
        if (affidavit.getType().equalsIgnoreCase(Affidavit.TYPE.EMULATED.toString())) {
        	UserDetail userDetail = userDetailDao.findByUserName(SecurityUtility.getPrincipal());
            userHistory.setUserDetail(userDetail);
            userHistory.setSystemComment(String.format("User %s started emulating user %s", SecurityUtility.getPrincipal(), affidavit.getUserName()));
            userHistory.setUserComment(String.format("I %s started emulating user %s", SecurityUtility.getPrincipal(), affidavit.getUserName()));
            userHistory.setUserActivity(userService.createOrRetrieveUserActivity(UserActivity.Activity.EMULATED_COOKIE_CREATED));
        } else {
        	UserDetail userDetail = userDetailDao.findByUserName(affidavit.getUserName());
            userHistory.setUserDetail(userDetail);
            userHistory.setSystemComment(String.format("User %s stopped emulating user %s and resumed normal mode", affidavit.getUserName(), SecurityUtility.getPrincipal()));
            userHistory.setUserComment(String.format("I %s stopped emulating user %s", affidavit.getUserName(), SecurityUtility.getPrincipal()));
            userHistory.setUserActivity(userService.createOrRetrieveUserActivity(UserActivity.Activity.EMULATED_COOKIE_DELETED));
        }
        userHistoryDao.save(userHistory);
    }

    private boolean canCreateEmulationCookie(String userName) {
        return Boolean.TRUE;
        /*UserDetail userDetail = userDetailDao.findByUserName(userName);
        List<UserAccountRole> userAccountRoleSet = userAccountRoleDao.findByUserId(userDetail.getId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof PositivePayUser) {
                PositivePayUser positivePayUser = (PositivePayUser) principal;
                Collection<GrantedAuthority> grantedAuthorityCollection = positivePayUser.getAuthorities();
                for (GrantedAuthority grantedAuthority : grantedAuthorityCollection) {
                    PositivePayGrantedAuthority positivePayGrantedAuthority = (PositivePayGrantedAuthority) grantedAuthority;
                    for (UserAccountRole userAccountRole : userAccountRoleSet) {
                        if (userAccountRole.getAccount().getId().equals(positivePayGrantedAuthority.getAccount())) {
                            if (positivePayGrantedAuthority.getAuthority().equals(Role.Roles.ROLE_ADMIN)) {
                                return Boolean.TRUE;
                            }
                        }
                    }
                }
            } else {
                throw new RuntimeException("Should be an instance of String or PositivePayUser object but found a different one. " + principal.getClass());
            }
            return Boolean.FALSE;
        }
        return Boolean.FALSE;*/
    }

    private List<Permission> getUserPermission(String username) {
        //List<Permission> permissionList = permissionDao.findResourcesByUser(username);
        List<Permission> returnList = new ArrayList<Permission>(0);
        /*for (Permission permission : permissionList) {
            UserPermission userPermission = new UserPermission();
            try {
                BeanUtils.copyProperties(userPermission, permission);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Exception while creating UserPermission Object", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Exception while creating UserPermission Object", e);
            }
            returnList.add(userPermission);
        }*/
        return returnList;
    }
}
