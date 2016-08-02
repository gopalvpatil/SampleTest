package com.westernalliancebancorp.positivepay.aspect;

import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.log.Loggable;

/**
 * Aspect used for logging time taken by methods at controller, service and dao layers.
 * @author akumar1
 *
 */
@Component
@Aspect
public class AuditAspect {
	
	@Loggable
    private Logger logger;
	
	@Pointcut("within(com.westernalliancebancorp.positivepay.web.controller.*)")
    public void inWebLayer() {}
	
	@Pointcut("within(com.westernalliancebancorp.positivepay.service.impl..*)")
    public void inServiceLayer() {}
	
	@Pointcut("within(com.westernalliancebancorp.positivepay.dao..*)")
    public void inDataAccessLayer() {}
	
	@Around("inWebLayer() || inServiceLayer() || inDataAccessLayer()")
	public Object audit(ProceedingJoinPoint joinPoint) throws Throwable{
		Signature methodSignature = joinPoint.getSignature();
		logger.debug("{} called", methodSignature);
		long startTime = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long stopTime = System.currentTimeMillis();
	    logger.debug("{} finished in {} ms", methodSignature, (stopTime - startTime));
        logger.info(Log.event(Event.TIME_INTERVAL_TOTAL_TIME,String.format("<METHOD>%s</METHOD> finished in <TIME>%s</TIME> ms", methodSignature, (stopTime - startTime))));
	    return result;
	}
}
