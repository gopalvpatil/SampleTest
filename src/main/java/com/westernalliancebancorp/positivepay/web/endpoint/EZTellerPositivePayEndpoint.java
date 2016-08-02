package com.westernalliancebancorp.positivepay.web.endpoint;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.harlandfs.ezteller.nativepositivepay.ApproveOnUsCheck;
import com.harlandfs.ezteller.nativepositivepay.ApproveOnUsCheckResponse;
import com.harlandfs.ezteller.nativepositivepay.ApproveOnUsCheckWithOverride;
import com.harlandfs.ezteller.nativepositivepay.ApproveOnUsCheckWithOverrideResponse;
import com.harlandfs.ezteller.nativepositivepay.IsSystemAvailable;
import com.harlandfs.ezteller.nativepositivepay.IsSystemAvailableResponse;
import com.harlandfs.ezteller.nativepositivepay.ReturnResult;
import com.harlandfs.ezteller.nativepositivepay.ReverseOnUsCheck;
import com.harlandfs.ezteller.nativepositivepay.ReverseOnUsCheckResponse;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CheckService;

/**
 * EZTeller Positive Pay Endpoint.
 * User:	Gopal Patil
 * Date:	Jan 28, 2014
 * Time:	6:34:43 PM
 */
@Endpoint("ezTellerPositivePayEndpoint")
public class EZTellerPositivePayEndpoint {	
	
	@Loggable
	private Logger logger;
	
	@Autowired
	private BankService bankService;
	
	@Autowired
	private CheckService checkService;
	
	@Autowired
	private AccountService accountService;
	
	//Messages
    @Value("${pp.available}")
    String pPAvailable;
	@Value("${pp.not.available}")
    String pPNotAvailable;
	
	@Value("${not.pp.account}")
    String notPPAccount;
	@Value("${pp.check.found}")
    String pPCheckFound;
	
    @Value("${amount.notmatched.exception}")
    String amountNotMatchedException;
    
    @Value("${supervisor.override.paid.exception}")
    String supervisorPaidException;
    @Value("${supervisor.override.paidnotissued.exception}")
    String supervisorPNIException;
    @Value("${supervisor.override.stop.exception}")
    String supervisorStopException;
    @Value("${supervisor.override.void.exception}")
    String supervisorVoidException;
    @Value("${supervisor.override.stale.exception}")
    String supervisorStaleException;    
    @Value("${supervisor.override.amount.exception}")
    String supervisorAmountException;
    
    @Value("${level4.supervisor.override.paidnotissued.exception}")
    String level4SupervisorPNIException;
    @Value("${level4.supervisor.override.payment.exception}")
    String level4SupervisorPaymentException;
    
    @Value("${account.paidnotissued.exception}")
    String accountPNIException;
    @Value("${paidnotissued.exception}")
    String pNIException;
   
   
	/**
	 * @param isSystemAvailable
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "IsSystemAvailable", namespace = "http://www.harlandfs.com/EZTeller/NativePositivePay")
	public IsSystemAvailableResponse isSystemAvailable(IsSystemAvailable isSystemAvailable) {
		IsSystemAvailableResponse isSystemAvailableResponse = new IsSystemAvailableResponse();
		ReturnResult returnResult = new ReturnResult();		
		try {
			if (bankService.isBankExist(isSystemAvailable.getNBankID())) {
		         returnResult.setResultCode(1);
		         returnResult.setMessage(pPAvailable);
			} else {
		         returnResult.setResultCode(0);
		         returnResult.setMessage(pPNotAvailable);
			}
		} catch (Exception e) {
			logger.error("Exception is thrown by:: EZTellerPositivePayEndpoint : IsSystemAvailable operation And Exception is: " + e.getMessage(), e);
			e.printStackTrace();
		}
		
		isSystemAvailableResponse.setIsSystemAvailableResult(returnResult);
		return isSystemAvailableResponse;
	}
	
	/**
	 * @param approveOnUsCheck
	 * @return
	 */
	@PayloadRoot(localPart = "ApproveOnUsCheck", namespace = "http://www.harlandfs.com/EZTeller/NativePositivePay")
	public ApproveOnUsCheckResponse approveOnUsCheck(ApproveOnUsCheck approveOnUsCheck) {		
		ApproveOnUsCheckResponse approveOnUsCheckResponse = new ApproveOnUsCheckResponse();
		ReturnResult returnResult = this.getApproveOnUsCheckResult(approveOnUsCheck.getStrAccountNumber(), 
				approveOnUsCheck.getStrCheckNumber(), approveOnUsCheck.getDCheckAmount());
		approveOnUsCheckResponse.setApproveOnUsCheckResult(returnResult);
		return approveOnUsCheckResponse;
	}
	
	/**
	 * @param approveOnUsCheckWithOverride
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "ApproveOnUsCheckWithOverride", namespace = "http://www.harlandfs.com/EZTeller/NativePositivePay")
	public ApproveOnUsCheckWithOverrideResponse approveOnUsCheckWithOverride(ApproveOnUsCheckWithOverride approveOnUsCheckWithOverride) {		
		ApproveOnUsCheckWithOverrideResponse approveOnUsCheckWithOverrideResponse = new ApproveOnUsCheckWithOverrideResponse();		
		ReturnResult returnResult = this.getApproveOnUsCheckResult(approveOnUsCheckWithOverride.getStrAccountNumber(), 
				approveOnUsCheckWithOverride.getStrCheckNumber(), approveOnUsCheckWithOverride.getDCheckAmount());		
		
		approveOnUsCheckWithOverrideResponse.setApproveOnUsCheckWithOverrideResult(returnResult);
		return approveOnUsCheckWithOverrideResponse;
	}

	/**
	 * @param reverseOnUsCheck
	 * @return
	 */
	@PayloadRoot(localPart = "ReverseOnUsCheck", namespace = "http://www.harlandfs.com/EZTeller/NativePositivePay")
	public ReverseOnUsCheckResponse reverseOnUsCheck(ReverseOnUsCheck reverseOnUsCheck) {		
		ReverseOnUsCheckResponse reverseOnUsCheckResponse = new ReverseOnUsCheckResponse();
		ReturnResult returnResult = new ReturnResult();	
		returnResult.setResultCode(0);
		returnResult.setMessage("Error. This operation currently not available");		
		reverseOnUsCheckResponse.setReverseOnUsCheckResult(returnResult);	
		return reverseOnUsCheckResponse;
	}
	
	private ReturnResult getApproveOnUsCheckResult(String accountNumber, String checkNumber, BigDecimal dCheckAmount) {
		ReturnResult returnResult = new ReturnResult();		
		try {
			Account account = accountService.findByAccountNumber(accountNumber);			
			if (account != null) {
				Check check = checkService.findCheckBy(accountNumber, checkNumber, dCheckAmount);
				if (check != null) {	
					if (check.getCheckStatus() != null) {	
						returnResult.setResultCode(4);
						if (check.getCheckStatus().getName().equals(CheckStatus.PAID_STATUS_NAME)) {						
							//If check is paid then verify for amount mismatch when the issued amount differs from what is currently being paid
							if (check.getReferenceData() != null && check.getReferenceData().getAmount() != null) {						
								if (check.getReferenceData().getAmount().compareTo(check.getIssuedAmount()) != 0) {
									returnResult.setMessage(supervisorAmountException);
								} else {
									returnResult.setMessage(supervisorPaidException);
								}							
							} else {
								returnResult.setMessage(supervisorPaidException);
							}
						} else if (check.getCheckStatus().getName().equals(CheckStatus.PAID_NOT_ISSUED)) {
							returnResult.setMessage(supervisorPNIException);
						} else if (check.getCheckStatus().getName().equals(CheckStatus.STOP_STATUS_NAME)) {
							returnResult.setMessage(supervisorStopException);
						} else if (check.getCheckStatus().getName().equals(CheckStatus.VOID_STATUS_NAME)) {
	                        returnResult.setMessage(supervisorVoidException);
						} else if (check.getCheckStatus().getName().equals(CheckStatus.STALE_STATUS_NAME)) {
	                        returnResult.setMessage(supervisorStaleException);
						} else {
					         returnResult.setResultCode(1);
					         returnResult.setMessage(pPCheckFound);
						}
					} else {
			            returnResult.setResultCode(0);
			            returnResult.setMessage("Error.");
					}
				} else {					
					//TODO
					logger.info("Error. Check is not found");
		            returnResult.setResultCode(0);
		            returnResult.setMessage("Error. Check is not found");
				}
			} else {
                returnResult.setResultCode(2);
                returnResult.setMessage(notPPAccount);
			}
		} catch (Exception e) {
            returnResult.setResultCode(0);
            returnResult.setMessage("Error.");
            logger.error("Exception is thrown by:: EZTellerPositivePayEndpoint : Exception is: " + e.getMessage(), e);
			e.printStackTrace();
		}		
		return returnResult;
	}
	
}
