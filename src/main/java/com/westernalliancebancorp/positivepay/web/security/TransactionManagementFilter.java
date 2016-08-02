package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TransactionManagementFilter is
 *
 * @author Giridhar Duggirala
 */
@Component("transactionManagementFilter")
public class TransactionManagementFilter extends GenericFilterBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Value("${positive.bottomline.transaction.id.name}")
    private String transactionIdName = "TRANSACTION_ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = null;
        HttpServletRequest httpServletRequest = null;
        if (response instanceof HttpServletResponse) {
            httpServletResponse = (HttpServletResponse) response;

        }
        if (request instanceof HttpServletRequest) {
            httpServletRequest = (HttpServletRequest) request;
        }
        String transactionId = SecurityUtility.setTransactionId();
        logger.info(String.format("Received transactionId %s for resource %s", transactionId, httpServletRequest.getRequestURL()));
        httpServletResponse.setHeader(transactionIdName, transactionId);
        logger.info(String.format("Completed setting the transactionId %s for resource %s", transactionId, httpServletRequest.getRequestURL()));
        chain.doFilter(request, response);
    }
}
