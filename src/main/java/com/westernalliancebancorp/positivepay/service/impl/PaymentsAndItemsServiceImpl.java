package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.ActionDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.SearchParameterDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDefinedFilterDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailFilterSearchValueDao;
import com.westernalliancebancorp.positivepay.dto.ActionDto;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.DataCriteriaDto;
import com.westernalliancebancorp.positivepay.dto.ExceptionalReferenceDataDto;
import com.westernalliancebancorp.positivepay.dto.FileTypeDto;
import com.westernalliancebancorp.positivepay.dto.PaymentDetailDto;
import com.westernalliancebancorp.positivepay.dto.UserDefinedFilterDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.FileType;
import com.westernalliancebancorp.positivepay.service.PaymentsAndItemsService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

/**
 * providing implementation for service methods to work with the Payment Item DTO Model.
 * @author Anand Kumar
 */
@Service
public class PaymentsAndItemsServiceImpl implements PaymentsAndItemsService {

	/** The logger object */
	@Loggable
	private Logger logger;
	
	@Autowired
	private CheckDao checkDao;
	@Autowired
	private UserDetailDao userDao;
    @Autowired
    private ItemTypeDao itemTypeDao;
    @Autowired
    private UserDetailDefinedFilterDao userDetailDefinedFilterDao;
    @Autowired
    private SearchParameterDao searchParameterDao;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private UserDetailFilterSearchValueDao userDetailFilterSearchValueDao;
    @Autowired
    UserService userService;
    @Autowired
    private BatchDao batchDao;
    @Autowired
    private FileTypeDao fileTypeDao;
    @Autowired
    private ActionDao actionDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDetailDto> findAllPaymentsForUserCompany() {
		if(SecurityUtility.isLoggedInUserBankAdmin()) {
    		throw new RuntimeException("Invalid API usage - This method should not be called for bank admin.");
    	}
		Company userCompany = userService.getLoggedInUserCompany();
		return batchDao.findAllPaymentsForCompany(userCompany);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDetailDto> findAllPayments(UserDefinedFilterDto userDefinedFilterDto) {
		if(!SecurityUtility.isLoggedInUserBankAdmin()) {
    		throw new RuntimeException("Invalid API usage - This method can only be called bank admin.");
    	}
		return batchDao.findAllPayments(userDefinedFilterDto.getSearchParametersMap());
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDetailDto> findAllItems(UserDefinedFilterDto userDefinedFilterDto) {
		if(!SecurityUtility.isLoggedInUserBankAdmin()) {
    		throw new RuntimeException("Invalid API usage - This method can only be called bank admin.");
    	}
		return batchDao.findAllItems(userDefinedFilterDto.getSearchParametersMap());
	}
	
	/**
	 * This method is used to search the payments from the customer dashboard
	 * @param dataCriteriaDto
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDetailDto> findAllPaymentsByDataCriteria(DataCriteriaDto dataCriteriaDto) {
		if(SecurityUtility.isLoggedInUserBankAdmin()) {
    		throw new RuntimeException("Invalid API usage - This method should not be called for bank admin.");
    	}
		Company userCompany = userService.getLoggedInUserCompany();
		return batchDao.findAllPaymentsByDataCriteriaAndUserCompany(dataCriteriaDto, userCompany);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ExceptionalReferenceDataDto> getDuplicateCheckDetails(String checkNumber, String accountNumber,
			String itemType) {
		
		return batchDao.getExceptionalReferenceData(checkNumber, accountNumber, itemType);
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FileTypeDto> getAllCreatedMethods() {
		List<FileTypeDto> createMethodList = new ArrayList<FileTypeDto>();
		List<FileType> fileTypeList = fileTypeDao.findAll();
		for(FileType fileType : fileTypeList) {
			FileTypeDto fileTypeDto = new FileTypeDto();
			fileTypeDto.setDescription(fileType.getDescription());
			fileTypeDto.setName(fileType.getName().name());
			createMethodList.add(fileTypeDto);
		}
		return createMethodList;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ActionDto> getAllAvailableActions() {
		List<ActionDto> availableActions = new ArrayList<ActionDto>();
		List<Action> actionList = actionDao.findAllAdminActions();
		for(Action action : actionList) {
			ActionDto actionDto = new ActionDto();
			actionDto.setName(action.getName());
			actionDto.setDescription(action.getDescription());
			availableActions.add(actionDto);
		}
		return availableActions;
	}
	
	@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentDetailDto getItemDetails(String checkNumber,String accountNumber,String itemType) {
    	return batchDao.findItemDetails(checkNumber,accountNumber,itemType);
    }
}
