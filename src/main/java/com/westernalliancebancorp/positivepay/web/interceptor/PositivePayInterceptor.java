package com.westernalliancebancorp.positivepay.web.interceptor;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.utility.common.PPUtils;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;

@Component
public class PositivePayInterceptor extends HandlerInterceptorAdapter {

	@Loggable
    private Logger logger;

	/* This method can be used to manipulate the response and the model and view.
	 * Currently we are using this for showing the emulation message on every screen if the user is emulated type.
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//Check if user is in emulated mode
		if(modelAndView != null && PPUtils.isEmulatedUser()) {//adding null check for modelAndView as for ajax call it may be null
			logger.info("User is in emulated mode.");
			modelAndView.addObject("emulation", true);
		}
	}

	/* This method is called before the handler execution, returns a boolean value, true : continue the handler execution chain; false, stop the execution chain and return it.
	 * Currently we are using this for logging the request URL that is being processed.
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestMapping = (String) request.getServletPath();
		logger.info("processing url : {}", requestMapping);
		HttpSession currentSession = ((HttpServletRequest)request).getSession(false);
		logger.info("currentSession="+currentSession);
		Affidavit affidavit = AffidavitThreadLocal.get();
		logger.info("affidavit="+affidavit);
		Principal principal = request.getUserPrincipal();
		logger.info("principal="+principal);
        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
        logger.info("ajaxHeader="+ajaxHeader);
    	if(principal == null && (!requestMapping.contains("/messages")))
        {
    		if("XMLHttpRequest".equals(ajaxHeader))
            {
    			logger.info("Session timed out : ajax  call:  send {} error code", 911);
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.sendError(911, "Session Timed Out");
                return false;
            } else {
            	/*logger.info("Session timed out: http call: forward to login page");
            	HttpServletResponse resp = (HttpServletResponse) response;
            	if(!requestMapping.contains("/login")){
            		resp.sendRedirect(request.getContextPath()+"/login?sessionExpired=true");
            	}*/
            }
        }
		return true;
	}	
}
