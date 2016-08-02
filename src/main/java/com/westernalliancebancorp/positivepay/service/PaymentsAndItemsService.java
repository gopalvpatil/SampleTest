package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.dto.ActionDto;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.DataCriteriaDto;
import com.westernalliancebancorp.positivepay.dto.ExceptionalReferenceDataDto;
import com.westernalliancebancorp.positivepay.dto.FileTypeDto;
import com.westernalliancebancorp.positivepay.dto.PaymentDetailDto;
import com.westernalliancebancorp.positivepay.dto.UserDefinedFilterDto;
/**
 * Interface providing service methods to work with the PaymentDetailDto Model
 * @author Anand Kumar
 */
public interface PaymentsAndItemsService {
	List<PaymentDetailDto> findAllPaymentsForUserCompany();
	List<PaymentDetailDto> findAllPayments(UserDefinedFilterDto userDefinedFilterDto);
	List<PaymentDetailDto> findAllPaymentsByDataCriteria(DataCriteriaDto dataCriteriaDto);
	List<ExceptionalReferenceDataDto> getDuplicateCheckDetails(String checkNumber, String accountNumber,
			String itemType);
	List<FileTypeDto> getAllCreatedMethods();
	List<ActionDto> getAllAvailableActions();
	List<PaymentDetailDto> findAllItems(UserDefinedFilterDto userDefinedFilterDto);
	PaymentDetailDto getItemDetails(String checkNumber,String accountNumber,String itemType);
}
