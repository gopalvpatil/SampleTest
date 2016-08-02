package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.dao.SsoAttributeDao;
import com.westernalliancebancorp.positivepay.dao.SsoDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.BottomLineSecurityService;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * BottomLineAuthenticationEntryPoint is Used by the <code>ExceptionTranslationFilter</code> to commence authentication for the users arriving from BottomLine SSO
 * <p/>
 * First we receive the request url with the request parameters (username, account) etc. as these may get changed (added more or removed some) these has to be specified in a map.
 * We read the parameters, generate a UID and save the map into DB against the generated UID.
 * The user's browser will be redirected to the application (Positive Pay) with a generated UID as a request parameter the name of the parameter is mentioned as uidRequestParameterName.
 * The URL for redirection is mentioned as <code>service</code> and this URL is monitored by the {@link com.westernalliancebancorp.positivepay.web.security.BottomLineAuthenticationFilter},
 * which will validate the BottomLine login was successful.
 *<b>positivepay.uid.request.parameter.name</b> - The name of the parameter that is set for UID. for ex: given the URL
 * https://host.domain.com/s/signin?key=1s5feFTTpolGHiD19457 to which the request has been redirected after creating the UID, "key" will be value for this property
 * <b>positivepay.bottomline.csv.params.to.read</b> - When the request has been arrived from BottomLine we need to read set of parameters that are required, that list of parameters to read are
 * provided as in csv format
 * <b>positivepay.bottomline.service.url</b> - The URL that is to be set for browsers redirection. example URL https://host.domain.com/s/signin
 *
 * @author Giridhar Duggirala
 */

@Service(value="bottomLineEntryPoint")
public class BottomLineAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
    @Loggable
    private Logger logger;
    @Value("${positivepay.uid.request.parameter.name}")
    private String uidRequestParameterName;
    @Value("${positivepay.bottomline.csv.params.to.read}")
    private String csvParamsToRead;
    @Value("${positivepay.bottomline.service.url}")
    private String service;
    @Value("${positivepay.bottomline.service.https.check.enabled}")
    private boolean isHttpsCheckEnabled=false;

    private String[] paramsToRead;
    @Autowired
    private SsoAttributeDao ssoAttributeDao;
    @Autowired
    private SsoDao ssoDao;
    @Autowired
    BottomLineSecurityService bottomLineSecurityService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final String uid = bottomLineSecurityService.getUid(request.getParameterMap());
        logger.debug(new StringBuilder().append("UID received is :").append(uid).toString());
        final String redirectUrl = createRedirectUrl(uid);
        logger.info(new StringBuilder().append("Redirect URL that is created is :").append(redirectUrl).toString());
        response.getWriter().println(redirectUrl);
        response.setStatus(HttpServletResponse.SC_OK);
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
        Assert.hasLength(this.csvParamsToRead, "csvParamsToRead must be specified");
        paramsToRead = csvParamsToRead.split(",");
    }
}
