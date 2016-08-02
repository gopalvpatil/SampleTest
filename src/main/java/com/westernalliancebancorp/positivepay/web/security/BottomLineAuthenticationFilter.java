package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.dao.SsoAttributeDao;
import com.westernalliancebancorp.positivepay.dao.SsoDao;
import com.westernalliancebancorp.positivepay.model.Sso;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;
import com.westernalliancebancorp.positivepay.utility.GsonUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * BottomLineAuthenticationFilter is used for processing the UID that it has received during the authentication process.
 * The UID is generated when bottomline first connects to the application. The information that is received during that process is stored against the generated UID. A URL is created with the UID that is generated
 * and that URL is monitored by this filter.
 * When the URL bing monitored is received, we will attempt to authenticate the user by checking
 * 1. This UID against the DB record
 * 2. Make sure that the UID is in the acceptable time limit.
 * <b>positivepay.uid.request.parameter.name</b> - Parameter name of the UID generally the value would be "key"
 * <b>positivepay.check.request.method</b> - As per the BottomLine SSO specification we should check if the request is a POST request or not. for development sake
 * we can set that check to true for false, in production this should always be true.
 *
 * @author Giridhar Duggirala
 */

//@Service(value = "bottomLineAuthenticationFilter")
public class BottomLineAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${positivepay.uid.request.parameter.name}")
    private String uidRequestParameterName = "key";

    @Value("${positivepay.check.request.method}")
    private boolean checkRequestMethod = Boolean.FALSE;

    @Value("${positivepay.affidavit.cookie.name}")
    private String ppAffidavitName = "PP_AFFIDAVIT_COOKIE";

    @Value("${positivepay.affidavit.cookie.domain}")
    private String getPpAffidavitDomainName = "wal.com";

    @Value("${positivepay.cookie.ttl}")
    private long cookieTtl = 7200000; //2hrs

    @Value("${positivepay.cookie.max.ttl}")
    private long cookieMaxTtl = 28800000; //8hrs

    @Value("${positivepay.cookie.is.secure}")
    private boolean isSecureCookie = false;

    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * @param defaultFilterProcessesUrl the default value for <tt>filterProcessesUrl</tt> this will be monitored and when the request arrives, we attempt authentication.
     */
    protected BottomLineAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    public BottomLineAuthenticationFilter() {
        super("/s/signin");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (checkRequestMethod && !request.getMethod().equals("POST")) {
            throw new RuntimeException("Only POST Requests are considered.");
        }
        String key = request.getParameter(uidRequestParameterName);
        final BottomLineAuthenticationToken bottomLineAuthenticationToken = new BottomLineAuthenticationToken("", key);
        return this.getAuthenticationManager().authenticate(bottomLineAuthenticationToken);
    }
}
