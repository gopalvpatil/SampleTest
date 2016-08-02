package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.SearchParameterDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDefinedFilterDao;
import com.westernalliancebancorp.positivepay.dto.SearchParameterDto;
import com.westernalliancebancorp.positivepay.dto.UserDefinedFilterDto;
import com.westernalliancebancorp.positivepay.dto.UserDetailFilterSearchValueDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.SearchParameter;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserDetailDefinedFilter;
import com.westernalliancebancorp.positivepay.model.UserDetailFilterSearchValue;
import com.westernalliancebancorp.positivepay.service.UserDetailDefinedFilterService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

/**
 * providing implementation for service methods to work with the UserDetailDefinedFilter Model.
 * @author Anand Kumar
 */
@Service
public class UserDetailDefinedFilterServiceImpl implements UserDetailDefinedFilterService {

	/** The logger object */
	@Loggable
	private Logger logger;
	
	/** The UserDetailDefinedFilterDao dependency */
	@Autowired
	private UserDetailDefinedFilterDao userDetailDefinedFilterDao;
	@Autowired
	private SearchParameterDao searchParameterDao;
	@Autowired 
	BatchDao batchDao;
	
	@Autowired
	private UserDetailDao userDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public UserDetailDefinedFilter update(UserDetailDefinedFilter userDetailDefinedFilter) {
		userDetailDefinedFilter.setUserDetail(getUserDetail());
		return userDetailDefinedFilterDao.update(userDetailDefinedFilter);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public UserDetailDefinedFilter save(UserDetailDefinedFilter userDetailDefinedFilter) {
		userDetailDefinedFilter.setUserDetail(getUserDetail());
		return userDetailDefinedFilterDao.save(userDetailDefinedFilter);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(UserDetailDefinedFilter userDetailDefinedFilter) {
		userDetailDefinedFilterDao.delete(userDetailDefinedFilter);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public UserDetailDefinedFilter findById(Long id) {
		return userDetailDefinedFilterDao.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<UserDetailDefinedFilter> findAll() {
		return userDetailDefinedFilterDao.findAll();
	}
	
	/**
	 * This will get all the filters for the user
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<UserDefinedFilterDto> findAllForUser() {
		if(SecurityUtility.isLoggedInUserBankAdmin()) {
			List<UserDetailDefinedFilter> userDetailDefinedFilters = userDetailDefinedFilterDao.findAllForUser(getUserDetail());
			return buildUserDefinedFilterDtoList(userDetailDefinedFilters);
		} else {
			throw new RuntimeException("No Payment/Items Filter for user other than Bank Admins.");
		}
	}
	
	private List<UserDefinedFilterDto> buildUserDefinedFilterDtoList(
			List<UserDetailDefinedFilter> userDetailDefinedFilters) {
		List<UserDefinedFilterDto> userDefinedFilterDtoList = new ArrayList<UserDefinedFilterDto>();
		Map<Long, String> searchParamIdNameMap = searchParameterDao.getIdNameMap();
		//loop through all the filters and make a list of userDefinedFilterDto
		for(UserDetailDefinedFilter userDetailDefinedFilter : userDetailDefinedFilters) {
			UserDefinedFilterDto userDefinedFilterDto = new UserDefinedFilterDto();
			userDefinedFilterDto.setId(userDetailDefinedFilter.getId());
			userDefinedFilterDto.setFilterName(userDetailDefinedFilter.getName());
			userDefinedFilterDto.setFilterDescription(userDetailDefinedFilter.getDescription());
			userDefinedFilterDto.setUserDetail(userDetailDefinedFilter.getUserDetail());
			Map<String, SearchParameterDto> searchParametersMap = new TreeMap<String, SearchParameterDto>();
			//Get the userDetailFilterSearchValueDtoList
			List<UserDetailFilterSearchValueDto> userDetailFilterSearchValueDtoList = batchDao.getUserDetailFilterSearchValuesByUserDetailDefinedFilterId(userDetailDefinedFilter.getId());
			for(UserDetailFilterSearchValueDto userDetailFilterSearchValueDto : userDetailFilterSearchValueDtoList) {
				String searchParameterName = searchParamIdNameMap.get(userDetailFilterSearchValueDto.getSearchParameterId());
				
				//GP
				SearchParameterDto searchParameterDto = new SearchParameterDto();
				searchParameterDto.setParameterCsv(userDetailFilterSearchValueDto.getParameterValue());
				searchParameterDto.setRelationalOperator(userDetailFilterSearchValueDto.getRelationalOperator());			
				
				searchParametersMap.put(searchParameterName.trim(), searchParameterDto);
				userDefinedFilterDto.setSearchParametersMap(searchParametersMap);
			}
			userDefinedFilterDtoList.add(userDefinedFilterDto);
		}
		return userDefinedFilterDtoList;
	}

	/**
	 * Given a UserDefinedFilterDto, this method will not only save that filter but will also save the data associated 
	 * with that filter in appropriate table.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public UserDetailDefinedFilter save(UserDefinedFilterDto userDefinedFilterDto) {
		UserDetailDefinedFilter userDetailDefinedFilter = new UserDetailDefinedFilter(); 
		//Find user details
		UserDetail userDetail = userDao.findByUserName(SecurityUtility.getPrincipal());
		userDetailDefinedFilter.setUserDetail(userDetail);
		userDetailDefinedFilter.setName(userDefinedFilterDto.getFilterName());
		userDetailDefinedFilter.setDescription(userDefinedFilterDto.getFilterDescription());
		userDetailDefinedFilter = userDetailDefinedFilterDao.save(userDetailDefinedFilter);
		//loop through the map and find search Parameter object associated
		//Then form UserDetailFilterSearchValue object and save them to database one by one
		int paramSequence = 0;
		List<UserDetailFilterSearchValue> UserDetailFilterSearchValueList = new ArrayList<UserDetailFilterSearchValue>();
		Map<String,SearchParameterDto> searchParametersMap = userDefinedFilterDto.getSearchParametersMap();
		
		
		for (Map.Entry<String, SearchParameterDto> entry : searchParametersMap.entrySet()) {
			paramSequence++;
			SearchParameter searchParameter = searchParameterDao.findByName(entry.getKey());
			UserDetailFilterSearchValue searchValue = new UserDetailFilterSearchValue();
			searchValue.setSearchParameter(searchParameter);
			searchValue.setUserDetailDefinedFilter(userDetailDefinedFilter);
			
			//GP
			SearchParameterDto searchParameterDto = entry.getValue();
			searchValue.setParameterValue(searchParameterDto.getParameterCsv());
			searchValue.setRelationalOperator(searchParameterDto.getRelationalOperator());
			
			searchValue.setParamSequence(paramSequence);

			UserDetailFilterSearchValueList.add(searchValue);
		}
		//save the search values
		batchDao.insertUserDetailFilterSearchValueList(UserDetailFilterSearchValueList);
		//return the saved filter
		return userDetailDefinedFilter;
	}
	
	private UserDetail getUserDetail() {
		String userName = SecurityUtility.getPrincipal();
		return userDao.findByUserName(userName);
	}

}
