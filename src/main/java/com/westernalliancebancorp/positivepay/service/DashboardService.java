package com.westernalliancebancorp.positivepay.service;

import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.dto.AccountPaymentInfoDto;
import com.westernalliancebancorp.positivepay.dto.PaymentByDateDto;

/**
 * User: gduggirala
 * Date: 3/6/14
 * Time: 6:59 PM
 */
public interface DashboardService {
    List<AccountPaymentInfoDto> getLoggedInnUserAllAccountsPaymentsData(List<String> accountNumbers, List<String> checkStatus);
    List<PaymentByDateDto> getAllPaymentsByDate(List<String> accountNumbers, Date fromDate, Date toDate);
    List<String> getUsersActiveAccountNumbers(String userName);
}
