package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.dao.UserActivityDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.UserHistoryDao;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.UserHistory;
import com.westernalliancebancorp.positivepay.service.BottomLineSecurityService;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;
import com.westernalliancebancorp.positivepay.utility.GsonUtility;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * PositivePayLogoutSuccessHandler is
 *
 * @author Giridhar Duggirala
 */

@Service(value = "logoutSuccessHandler")
public class PositivePayLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {
    @Value("${positivepay.affidavit.cookie.name}")
    private String ppAffidavitName = "PP_AFFIDAVIT_COOKIE";

    private String targetUrl = "/login?logout=true";

    @Autowired
    UserHistoryDao userHistoryDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private UserActivityDao userActivityDao;

    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;

    @Autowired
    BottomLineSecurityService bottomLineSecurityService;

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        Affidavit affidavit = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ppAffidavitName)) {
                logger.debug("Cookie found with the name :" + cookie.getName() + " under the domain :" + cookie.getDomain());
                affidavit = GsonUtility.getAffidavit(affidavitSecurityUtility.decrypt(cookie.getValue(), true));
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        if (affidavit != null) {
            updateHistory(affidavit);
            affidavit.setType(Affidavit.TYPE.NORMAL.name());
        }
        super.setDefaultTargetUrl(targetUrl);
        super.handle(request, response, authentication);
    }

    private void updateHistory(Affidavit affidavit) {
        if (affidavit.getUid() != null) {
            bottomLineSecurityService.handleLogout(affidavit.getUid());
        }
        SecurityUtility.setPrincipal(affidavit.getUserName());
        UserHistory userHistory = new UserHistory();
        UserDetail userDetail = null;
        if (affidavit.getType().equals(Affidavit.TYPE.NORMAL.toString())) {
            userHistory.setSystemComment("User " + affidavit.getUserName() + " Logged out");
            userHistory.setUserComment("I " + affidavit.getUserName() + " Logged out");
            userDetail = userDetailDao.findByUserName(affidavit.getUserName());
        }else if (affidavit.getType().equals(Affidavit.TYPE.EMULATED.toString())) {
            userHistory.setSystemComment(String.format("User %s deleted emulated cookie of %s", affidavit.getCreatedByUserName(), affidavit.getUserName()));
            userHistory.setUserComment(String.format("I %s deleted emulated cookie of %s", affidavit.getCreatedByUserName(), affidavit.getUserName()));
            userDetail = userDetailDao.findByUserName(affidavit.getCreatedByUserName());
        }else{
            userHistory.setSystemComment("User " + affidavit.getUserName() + " Logged out");
            userHistory.setUserComment("I " + affidavit.getUserName() + " Logged out");
            userDetail = userDetailDao.findByUserName(affidavit.getUserName());
        }
        userHistory.setUserDetail(userDetail);
        userHistory.setUserActivity(ModelUtils.createOrRetrieveUserActivity(UserActivity.Activity.LOG_OUT, userActivityDao));
        userHistoryDao.save(userHistory);
    }

}
