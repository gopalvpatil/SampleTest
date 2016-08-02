package com.westernalliancebancorp.positivepay.web.security;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;
import com.westernalliancebancorp.positivepay.utility.GsonUtility;

/**
 * PositivePayCookiePreAuthenticationFilter is
 *
 * @author Giridhar Duggirala
 */

public class PositivePayCookiePreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Value("${positivepay.affidavit.cookie.name}")
    private String ppAffidavitName = "PP_AFFIDAVIT_COOKIE";

    @Value("${positivepay.affidavit.cookie.domain}")
    private String getPpAffidavitDomainName = "wal.com";
    
    @Value("${positivepay.cookie.is.secure}")
    private boolean isSecureCookie = Boolean.TRUE; 

    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ppAffidavitName)) {
                logger.debug("Cookie found with the name :" + cookie.getName() + " under the domain :" + cookie.getDomain());
                if (request.getParameter("logout") == null)
                    return (cookie.getValue() == null || cookie.getValue().isEmpty()) ? null : cookie.getValue();
                else
                    return null;
            }
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
    
    /**
     * This method is used to reset the cookie in affidavit if it is changed
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
    	
    	if(AffidavitThreadLocal.isTTLChanged() != null && AffidavitThreadLocal.isTTLChanged()){
	    	try {
		    	Affidavit affidavit = AffidavitThreadLocal.get();
		    	if(affidavit != null){
		    		//Creating a new affidavit object to store into cookie as the permissionList in orginla affidavit may throw LazyIntialization exception.
			    	Affidavit newAffidavit = new Affidavit(affidavit.getUserName(), affidavit.getTtl(),
			    			affidavit.getMaxTtl(), affidavit.getUid(), affidavit.getType(), null);
                    newAffidavit.setCreatedByUserName(affidavit.getCreatedByUserName());
			    	String cookieValue = affidavitSecurityUtility.encrypt(GsonUtility.toString(newAffidavit), true);
					Cookie cookie = new Cookie(ppAffidavitName, cookieValue);
			        cookie.setPath("/");
			        cookie.setDomain(getPpAffidavitDomainName);
			        cookie.setMaxAge(-1);
			        cookie.setSecure(isSecureCookie);
			        response.addCookie(cookie);
		    	}
		        AffidavitThreadLocal.markTTLChanged(false);//set back to false
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Error occurrent while setting Affidavit in cookie after ttl changed", e);
			}
    	}
        PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Action.name());
    	super.successfulAuthentication(request, response, authResult);
    }
}
