package com.westernalliancebancorp.positivepay.web.controller;

import com.westernalliancebancorp.positivepay.annotation.PositivePaySecurity;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.service.BottomLineSecurityService;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BottomLineAuthenticationController is
 *
 * @author Giridhar Duggirala
 */

@Controller
public class BottomLineAuthenticationController implements InitializingBean {
    @Loggable
    private Logger logger;
    @Value("${positivepay.uid.request.parameter.name}")
    private String uidRequestParameterName;
    @Value("${positivepay.bottomline.service.url}")
    private String service;
    @Value("${positivepay.bottomline.service.https.check.enabled}")
    private boolean isHttpsCheckEnabled=false;

    @Value("${positive.bottomline.login.request.url.param}")
    private String requestUrl = "/logon/";

    @Autowired
    BottomLineSecurityService bottomLineSecurityService;

    @Deprecated
   // @RequestMapping("/s/logon")
    public void logon(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestMapping = (String) request.getServletPath();
        logger.info("processing url : {}", requestMapping);
        if(!request.getRequestURL().toString().contains("/logon")) {
            return;
        }
        final String uid = bottomLineSecurityService.getUid(request.getParameterMap());
        logger.debug(new StringBuilder().append("UID received is :").append(uid).toString());
        final String redirectUrl = createRedirectUrl(uid);
        logger.info(new StringBuilder().append("Redirect URL that is created is :").append(redirectUrl).toString());
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping("/s/logout")
    @PositivePaySecurity(resource = "ADJUST_AMOUNT",errorMessage = "doesn't have permission to adjust amount",group = Permission.TYPE.OTHER_PERMISSIONS)
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String uid = httpServletRequest.getParameter(uidRequestParameterName);
        bottomLineSecurityService.handleBottomLineKill(uid);
    }

    private String createRedirectUrl(String uid) {
        return String.format("%s?%s=%s", service, uidRequestParameterName, uid);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(this.service, "service must be specified");
        if(isHttpsCheckEnabled && !service.startsWith("https:")) {
            throw new RuntimeException("service must start with https:// please check.");
        }
    }
}
