package com.westernalliancebancorp.positivepay.web.controller;

import com.westernalliancebancorp.positivepay.annotation.PositivePaySecurity;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.ExceptionalReferenceDataService;
import com.westernalliancebancorp.positivepay.service.RemoveVoidOrStopService;
import com.westernalliancebancorp.positivepay.service.model.GenericResponse;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: gduggirala
 * Date: 16/6/14
 * Time: 1:19 PM
 */
@Controller
public class CheckNonWorkflowActionsController {
    @Loggable
    private Logger logger;

    @Autowired
    CheckDao checkDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    CheckService checkService;

    @Autowired
    ExceptionalReferenceDataService exceptionalReferenceDataService;
    
    @Autowired
    RemoveVoidOrStopService removeVoidOrStopService;

    @RequestMapping(value = "/user/nonWorkflow/unmatch", method = RequestMethod.GET)
    public String matchOrUnmatchPayment(@RequestParam(value = "userComment", required = false) String userComment, @RequestParam(value = "checkId") Long checkId, @RequestParam(value = "referenceIdToMatch", required = false) Long referenceIdToMatch) throws CallbackException, WorkFlowServiceException {
        Check check = checkService.unmatchAndMatch(userComment, checkId, referenceIdToMatch);
        //TODO: UI Developer please use the appropriate response.
        return "";
    }

    @RequestMapping(value = "/user/nonWorkflow/delete", method = RequestMethod.POST)
    public @ResponseBody String deleteReferenceData(@RequestParam(value = "exceptionalReferencedataId", required = true) Long exceptionalReferencedataId,
                                      @RequestParam(value = "userComment", required = false) String userComment) throws CallbackException, WorkFlowServiceException {
        boolean returnData = exceptionalReferenceDataService.deleteExceptionalReferenceDataRecord(exceptionalReferencedataId, userComment);
        //TODO: UI Developer please use the appropriate response.
        return "";
    }

    @RequestMapping(value = "/user/nonWorkflow/changeCheckNumber", method = RequestMethod.POST)
    public @ResponseBody String changeDuplicateReferenceDataCheckNumber(@RequestParam(value = "exceptionalReferenceDataId") Long exceptionalReferenceDataId,
                                                        @RequestParam(value = "changedCheckNumber") String changedCheckNumber,
                                                        @RequestParam(value = "userComment", required = false) String userComment) throws ParseException, CallbackException, WorkFlowServiceException {
        /**
         * When the duplicate reference data check serial number is changed (Per say call this as DUPLICATE-RD1)
         * then check if there is any referenceData already existing with the same check number and account number (DUPLICATE-RD2) and ItemCode in referenceData table.
         * If DUPLICATE-RD2 is existing then
         *      And if DUPLICATE-RD2 is referred by a check CHK1 then unMatch DUPLICATE-RD2 with that check and move that check(CHK1) into start status.
         *      If DUPLICATE-RD2 is not referred to any check then move DUPLICATE-RD2 into exceptional reference data as Duplicate (As we now know that DUPLICATE-RD1 is corrected, so
         *      DUPLICATE-RD2 is misread)
         *      Put DUPLICATE-RD1 into ReferenceData table with the status NOT_PROCESSED.
         * If DUPLICATE-RD2 is not existing then
         *      Move DUPLICATE-RD1 into ReferenceData table.
         *
         *
         */
        checkService.changeDuplicateReferenceDataCheckNumber(exceptionalReferenceDataId, changedCheckNumber,userComment);
        return "";
    }

    @RequestMapping(value = "/user/nonWorkflow/changeZeroedCheckNumber", method = RequestMethod.POST)
    public @ResponseBody String changeDuplicateReferenceDataCheckNumber(@RequestParam(value = "traceNumber", required = true) String traceNumber,
                                                                        @RequestParam(value = "changedCheckNumber", required = true) String changedCheckNumber,
                                                                        @RequestParam(value = "amount", required = true) String amount,
                                                                        @RequestParam(value = "accountNumber", required = true) String accountNumber,
                                                                        @RequestParam(value = "userComment", required = false) String userComment) throws ParseException, CallbackException, WorkFlowServiceException, AccountNotFoundException {
        ExceptionalReferenceData exceptionalRefereneceData =exceptionalReferenceDataService.findBy(traceNumber, amount, accountNumber);
        exceptionalReferenceDataService.changeZeroedCheckNumber(exceptionalRefereneceData.getId(), changedCheckNumber, userComment);
        return "";
    }

    @RequestMapping(value = "/user/nonWorkflow/changeAccountNumber", method = RequestMethod.POST)
    public @ResponseBody String changeDuplicateReferenceDataAccountNumber(@RequestParam(value = "exceptionalReferenceDataId") Long exceptionalReferenceDataId,
                                                        @RequestParam(value = "changedAccountNumber") String changedAccountNumber,
                                                        @RequestParam(value = "userComment", required = false) String userComment) throws ParseException, CallbackException, WorkFlowServiceException {
        /**
         * When the duplicate reference data check serial number is changed (Per say call this as DUPLICATE-RD1)
         * then check if there is any referenceData already existing with the same check number and account number (DUPLICATE-RD2) and ItemCode in referenceData table.
         * If DUPLICATE-RD2 is existing then
         *      And if DUPLICATE-RD2 is referred by a check CHK1 then unMatch DUPLICATE-RD2 with that check and move that check(CHK1) into start status.
         *      If DUPLICATE-RD2 is not referred to any check then move DUPLICATE-RD2 into exceptional reference data as Duplicate (As we now know that DUPLICATE-RD1 is corrected, so
         *      DUPLICATE-RD2 is misread)
         *      Put DUPLICATE-RD1 into ReferenceData table with the status NOT_PROCESSED.
         * If DUPLICATE-RD2 is not existing then
         *      Move DUPLICATE-RD1 into ReferenceData table.
         *
         *
         */
        checkService.changeDuplicateReferenceDataAccountNumber(exceptionalReferenceDataId, changedAccountNumber,userComment);
        return "";
    }
    
    @RequestMapping(value = "/user/nonWorkflow/pay", method = RequestMethod.POST)
    public @ResponseBody String payDuplicateReferenceData(@RequestParam(value = "exceptionalReferenceDataId") Long exceptionalReferenceDataId,
                                                          @RequestParam(value = "userComment", required = false) String userComment) throws ParseException, CallbackException, WorkFlowServiceException {

        checkService.payDuplicateReferenceData(exceptionalReferenceDataId,userComment);
        return "";
    }

    @RequestMapping(value = "/user/nonWorkflow/actions", method = RequestMethod.GET)
    public Map<String, String> getAvailableDuplicatePaymentNonWorkflowActions(@RequestParam(value = "actionsFor") String actionsFor){
        Map<String, String> availableActions = new HashMap<String, String>();
        availableActions.put("/user/nonWorkflow/changeCheckNumber","Change check number");
        availableActions.put("/user/nonWorkflow/changeAccountNumber","Change account number");
        availableActions.put("/user/nonWorkflow/delete","Delete Duplicate");
        return availableActions;
    }

    @PositivePaySecurity(resource = "CHANGE_PAYEE", errorMessage = "doesn't have permission to change Payee", group = Permission.TYPE.ITEMS)
    @RequestMapping(value = "/check/payee", method = RequestMethod.POST)
    public @ResponseBody boolean changePayee(@RequestParam(value = "checkId") Long checkId, @RequestParam(value = "payeeName") String payeeName, @RequestParam(value = "comment", required = false) String comment) {
        Check check = checkService.changePayee(checkId, payeeName, comment);
        if (check.getPayee().equals(payeeName)) {
            return true;
        }
        return false;
    }
    
    @PositivePaySecurity(resource = "ADD_COMMENT", errorMessage = "doesn't have permission to add Comment", group = Permission.TYPE.ITEMS)
    @RequestMapping(value = "/add/comment", method = RequestMethod.POST)
    public @ResponseBody boolean addComment(@RequestParam(value = "checkId") Long checkId,@RequestParam(value = "comment") String comment) {
        CheckHistory checkHistory = checkService.addComment(checkId, comment);
        if (checkHistory.getUserComment().equals(comment)) {
            return true;
        }
        return false;
    }
    
    //@PositivePaySecurity(resource = "REMOVE_STOP_VOID", errorMessage = "doesn't have permission to remove stop or void", group = Permission.TYPE.ITEMS)
    @RequestMapping(value = "/remove/stoporvoid", method = RequestMethod.POST)
    public @ResponseBody String removeStopOrVoid(@RequestParam(value = "checkId") Long checkId) throws Exception {
    	removeVoidOrStopService.removeVoidOrStop(checkId);
        return "";
    }
    

    @PositivePaySecurity(resource = "CHANGE_ITEM_DATE", errorMessage = "doesn't have permission to change date", group = Permission.TYPE.ITEMS)
    @RequestMapping(value = "/check/date", method = RequestMethod.POST)
    public @ResponseBody boolean changeDate(@RequestParam(value = "checkId") Long checkId, @RequestParam(value = "date") String date, @RequestParam(value = "comment", required = false) String comment) throws ParseException {
        Check check = checkService.changeDate(checkId, date,comment );
        return true;
    }
    
    @ExceptionHandler(Exception.class)
	public @ResponseBody
	GenericResponse handleException(HttpServletRequest request,
			HttpServletResponse response, Exception ex) {
    	String message = "{\"error\":\""+ex.getMessage()+"\",\"transactionId\":\""+TransactionIdThreadLocal.get()+"\"}";
		GenericResponse genericResponse = new GenericResponse(message);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return genericResponse;
	}
}
