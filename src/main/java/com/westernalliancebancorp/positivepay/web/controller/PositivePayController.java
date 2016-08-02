package com.westernalliancebancorp.positivepay.web.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;

/**
 * Positive Pay Web Controller.
 *
 * @author <a href="mailto:akumar1@intraedge.com">Anand Kumar</a>
 */
@Controller
@SessionAttributes
public class PositivePayController {

    //static Logger logger = LoggerFactory.getLogger(PositivePayController.class);
    @Loggable
    private Logger logger;

    @Value("${positivepay.affidavit.cookie.name}")
    private String ppAffidavitName = "PP_AFFIDAVIT_COOKIE";

    @Value("${positivepay.affidavit.cookie.domain}")
    private String getPpAffidavitDomainName = "wal.com";

    @Value("${positivepay.cookie.ttl}")
    private long cookieTtl = 7200000; //2hrs

    @Value("${positivepay.cookie.max.ttl}")
    private long cookieMaxTtl = 28800000; //8hrs

    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;

    @RequestMapping({"/s/logon"})
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/user/dashboard");
    }

    /**
     * This controller method simply takes the user to the login page.
     *
     * @param model   for incorporating attributes to be shown in the view
     * @param request object for finding the url pattern
     * @return the view name
     * @throws Exception
     */
    @RequestMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String logoutParameterValue = request.getParameter("logout");
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            logger.info("The user {} is already logged in, so redirecting to home page.", principal.getName());
            response.sendRedirect("user/dashboard");
        }
        if (principal == null || logoutParameterValue != null && logoutParameterValue.equals("true")) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(ppAffidavitName)) {
                        logger.debug("Cookie found with the name :" + cookie.getName() + " under the domain :" + cookie.getDomain());
                        cookie.setDomain(getPpAffidavitDomainName);
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }else{
                Cookie cookie = new Cookie(ppAffidavitName, "");
                cookie.setDomain(getPpAffidavitDomainName);
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        String requestMapping = (String) request.getServletPath();
        logger.info("processing url : {}", requestMapping);

        return "site.default.login.page";
    }

    /**
     * This maps the request to a specific error page based on the error code.
     *
     * @param errorId the errorcode e.g. 404, 500 etc
     * @param model   for incorporating attributes to be shown in the view
     * @param request object for finding out the url from which the request originally came from
     * @return the error page view name
     */
    @RequestMapping("error/{errorId}")
    public String errorPage(@PathVariable Integer errorId, Model model, HttpServletRequest request) {
        String origialUri = String.format("%s://%s:%d%s/", request.getScheme(), request.getServerName(), request.getServerPort(), request.getAttribute("javax.servlet.forward.request_uri"));
        logger.info("error code {} was returned for the request URI {}", errorId, origialUri);
        //model.addAttribute("username", getUserName(request));
        model.addAttribute("originalUri", origialUri);
        String returnURL = "site." + errorId + ".error.page";
        logger.info("Returning to Page ="+returnURL);
        return returnURL;
    }
}
