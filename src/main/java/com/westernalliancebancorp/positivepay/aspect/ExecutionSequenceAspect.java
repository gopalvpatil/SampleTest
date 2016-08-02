package com.westernalliancebancorp.positivepay.aspect;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.workflow.CallbackContext;

@Component
@Aspect
public class ExecutionSequenceAspect {

	@Loggable
	private Logger logger;

	@Around("@annotation(com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence)")
	public Object buildExceutionSequence(ProceedingJoinPoint joinPoint)
			throws Throwable {
		try {
			boolean fromCallback = false;
			CallbackContext callbackContext =null;
			Map<String,Object> userData = null;
			Object target = joinPoint.getTarget();
		    String className = target.getClass().getName();
			Object[] args = joinPoint.getArgs();
			for(Object arg :args)
			{
				if(arg instanceof CallbackContext)
				{
					callbackContext = (CallbackContext)arg;
					userData = callbackContext.getUserData();
					fromCallback = true;
				}
			}
			if(!fromCallback)
			{
				for(Object arg :args)
				{
					if(arg instanceof Map)
					{
						userData = (Map<String,Object>)arg;
					}
				}
			}

			String executionSeq = (String)userData.get(WorkflowService.STANDARD_MAP_KEYS.EXECUTION_SEQUENCE.name());
			userData.put(WorkflowService.STANDARD_MAP_KEYS.EXECUTION_SEQUENCE.name(),executionSeq + className);
			Object result = joinPoint.proceed();
			logger.info("Execution Sequence :"+(String)userData.get(WorkflowService.STANDARD_MAP_KEYS.EXECUTION_SEQUENCE.name()));
			return result;
			
		} catch (Exception e) {
			return joinPoint.proceed();
			//Ignore exception
		}
		
	}
}
