package com.westernalliancebancorp.positivepay.web.security;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.westernalliancebancorp.positivepay.dao.PermissionDao;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.UserActivityDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.UserHistoryDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserHistory;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;
import com.westernalliancebancorp.positivepay.utility.GsonUtility;

/**
 * PositivePayAuthenticationSuccessHandler is
 *
 * @author Giridhar Duggirala
 */
@Service(value = "authenticationSuccessHandler")
public class PositivePayAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Loggable
    private Logger logger;

    @Value("${positivepay.cookie.ttl}")
    private long cookieTtl = 7200000; //2hrs

    @Value("${positivepay.cookie.max.ttl}")
    private long cookieMaxTtl = 28800000; //8hrs

    @Value("${positivepay.cookie.is.secure}")
    private boolean isSecureCookie = Boolean.TRUE; //2hrs

    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;
    @Value("${positivepay.affidavit.cookie.name}")
    private String ppAffidavitName = "PP_AFFIDAVIT_COOKIE";

    @Value("${positivepay.affidavit.cookie.domain}")
    private String getPpAffidavitDomainName = "wal.com";

    @Value("${positivepay.uid.request.parameter.name}")
    private String uidRequestParameterName;

    @Autowired
    UserHistoryDao userHistoryDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private UserActivityDao userActivityDao;

    @Autowired
    PermissionDao permissionDao;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Adding cookie");
        String uid = request.getParameter(uidRequestParameterName);
        Affidavit affidavit = null;
        //if (authentication instanceof BottomLineAuthenticationToken && authentication.getPrincipal() instanceof PositivePayUser) {
        if ((authentication instanceof BottomLineAuthenticationToken || authentication instanceof UsernamePasswordAuthenticationToken)
                && authentication.getPrincipal() instanceof PositivePayUser) {
            if (uid == null || uid.isEmpty()) {
                List<Permission> permissionList = getUserPermission(((PositivePayUser) authentication.getPrincipal()).getUsername());
                affidavit = new Affidavit(((PositivePayUser) authentication.getPrincipal()).getUsername(), (System.currentTimeMillis() + cookieTtl), (System.currentTimeMillis() + cookieMaxTtl), Affidavit.TYPE.NORMAL.toString(), permissionList);
            } else {
                List<Permission> permissionList = getUserPermission(((PositivePayUser) authentication.getPrincipal()).getUsername());
                affidavit = new Affidavit(((PositivePayUser) authentication.getPrincipal()).getUsername(), (System.currentTimeMillis() + cookieTtl), (System.currentTimeMillis() + cookieMaxTtl), uid, Affidavit.TYPE.NORMAL.toString(), permissionList);
            }
        } else {
            throw new RuntimeException("Affidavit creation exception, must be an instance of PositivePayUser");
        }
        String cookieValue = affidavitSecurityUtility.encrypt(GsonUtility.toString(affidavit), true);
        Cookie cookie = new Cookie(ppAffidavitName, cookieValue);
        cookie.setPath("/");
        cookie.setDomain(getPpAffidavitDomainName);
        cookie.setMaxAge(-1);
        cookie.setSecure(isSecureCookie);
        response.addCookie(cookie);
        logger.debug(String.format("Cookie added with name '%s' and the value '%s'", ppAffidavitName, cookieValue));
        logger.info("Cookie added.");
        updateHistory(affidavit);
        PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Action.name());
        handle(request, response, authentication);
    }

    private List<Permission> getUserPermission(String username) {

        //Check BottomLineAthenticationProvider.getPreAuthenticatedAuthentication, we dont store permissions in cookie
        //We will only store in the Affidavit when we verify the cookie.


        //List<Permission> permissionList = permissionDao.findResourcesByUser(username);
        List<Permission> returnList = new ArrayList<Permission>(0);
        //Browsers are not allowing to send large size cookies to in the process of reducing cookie information
        //let get the permissions when the request arrives to the server.
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

    private void updateHistory(Affidavit affidavit) {
        UserHistory userHistory = new UserHistory();
        userHistory.setSystemComment("User " + affidavit.getUserName() + " Logged in");
        userHistory.setUserComment("I " + affidavit.getUserName() + " logged in");
        UserDetail userDetail = userDetailDao.findByUserName(affidavit.getUserName());
        userHistory.setUserDetail(userDetail);
        userHistory.setUserActivity(ModelUtils.createOrRetrieveUserActivity(UserActivity.Activity.LOG_IN, userActivityDao));
        userHistoryDao.save(userHistory);
    }
}
