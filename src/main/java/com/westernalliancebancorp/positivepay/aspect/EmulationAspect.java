package com.westernalliancebancorp.positivepay.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.westernalliancebancorp.positivepay.exception.CouldNotSaveBecauseOfEmulationException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;

/**
 * Emulation Aspect works around the annotation @RollbackForEmulatedUser and checks the the user is emulated.
 * If the user is emulated, the save, upadte and delete operations should be rolled back.
 * @author Anand Kumar
 *
 */
@Component
@Aspect
public class EmulationAspect {
	
	@Loggable
    private Logger logger;
	
	@Autowired
	JpaTransactionManager txManager;
	
	@Around("@annotation(com.westernalliancebancorp.positivepay.annotation.RollbackForEmulatedUser)")
	public Object rollbackForEmulatedUser(ProceedingJoinPoint joinPoint) throws Throwable{
		Affidavit affidavit = AffidavitThreadLocal.get();
		if(affidavit!=null && affidavit.getType().equals(Affidavit.TYPE.EMULATED.toString())) {
			logger.info("User is in emulation mode, so the transaction will not be committed.");
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setName("rootTransaction");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			TransactionStatus status = txManager.getTransaction(def);
			status.setRollbackOnly();
			joinPoint.proceed();
			txManager.commit(status);
			throw new CouldNotSaveBecauseOfEmulationException("Emulated User: So transaction will be rolled back.");
		} else {
			//Let the method execute normally
			return joinPoint.proceed();
		}
	}
}
