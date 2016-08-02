package com.westernalliancebancorp.positivepay.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.westernalliancebancorp.positivepay.exception.CouldNotSaveBecauseOfEmulationException;
import com.westernalliancebancorp.positivepay.exception.HttpStatusCodedResponseException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;

/**
 * GlobalExceptionHandler is
 *
 * @author Giridhar Duggirala
 */

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "site.default.error.page";
    @Loggable
    private Logger logger;
    private static int STATUS_EMULATED_USER = 999;
    
    @ExceptionHandler(value = TransactionSystemException.class)
    public void transactionSystemExceptionHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, TransactionSystemException transactionSystemException) throws Throwable{
    	logger.error(Log.event(Event.UNKNOWN_ERROR, "Unknown error occurred so I have been routed to global exception handler",transactionSystemException),transactionSystemException);
        if(transactionSystemException.getApplicationException().getClass() == CouldNotSaveBecauseOfEmulationException.class) {
    		httpServletResponse.setStatus(STATUS_EMULATED_USER);
    	}
    	if (AnnotationUtils.findAnnotation(transactionSystemException.getClass(), ResponseStatus.class) != null) {
            throw transactionSystemException;
        }
    }
    
    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest httpServletRequest, Exception e) throws Exception{
        logger.error(Log.event(Event.UNKNOWN_ERROR, "Unknown error occurred so I have been routed to global exception handler",e),e);
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("exceptionMessage", e.getMessage());
        mav.addObject("transactionId", TransactionIdThreadLocal.get());
        mav.addObject("url",httpServletRequest.getRequestURL());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }
    
    /**
     * Exception handler that will return http response code. Mainly used for JSON request/response.
     * @param request
     * @param response
     * @param httpCodedException
     * @return
     */
    @ExceptionHandler(value=HttpStatusCodedResponseException.class)
    @ResponseBody
    public Object httpStatusCodedResponseException(HttpServletRequest request, HttpServletResponse response, 
    		HttpStatusCodedResponseException httpCodedException) {
        logger.error(Log.event(Event.UNKNOWN_ERROR, "Unknown error occurred so I have been routed to global exception handler",httpCodedException),httpCodedException);
    	response.setStatus(httpCodedException.getStatusCode().value());
    	if(httpCodedException.getResponseBody() != null)
    		return httpCodedException.getResponseBody();
    	else if(StringUtils.isNotBlank(httpCodedException.getMessage()))
    		return httpCodedException.getMessage();
    	else
    		return "Error occurred while processing request";
    }
}
