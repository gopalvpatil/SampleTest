package com.westernalliancebancorp.positivepay.web.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HybridAuthenticationEntryPoint works like a router. Depending upon the origin of the request (Request is arriving from bottom line or from user browser itself)
 * it will route the request to appropriate Authentication Provider to handle authentication.
 *
 * <b>positive.bottomline.login.request.url.param</b> is the property that has to be set to set it up in various environments.
 * @author Giridhar Duggirala
 */

@Component(value = "hybridAuthenticationEntryPoint")
public class HybridAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
    @Autowired
    AuthenticationEntryPoint bottomLineEntryPoint;
    @Autowired
    AuthenticationEntryPoint loginUrlAuthenticationEntryPoint;
    @Value("${positive.bottomline.login.request.url.param}")
    private String requestUrl = "/logon";
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(!request.getRequestURL().toString().contains(requestUrl)) {
            loginUrlAuthenticationEntryPoint.commence(request, response,authException);
        }else {
            bottomLineEntryPoint.commence(request, response, authException);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(requestUrl, "Request URL cannot be null, as this is used to decide which Authentication provider to use.");
    }
}
