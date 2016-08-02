package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.dto.UserDefinedFilterDto;
import com.westernalliancebancorp.positivepay.model.UserDetailDefinedFilter;
/**
 * Interface providing service methods to work with the UserDetailDefinedFilter Model
 * @author Anand Kumar
 */
public interface UserDetailDefinedFilterService {
	UserDetailDefinedFilter update(UserDetailDefinedFilter userDetailDefinedFilter);
	UserDetailDefinedFilter save(UserDetailDefinedFilter userDetailDefinedFilter);
	void delete(UserDetailDefinedFilter userDetailDefinedFilter);
	UserDetailDefinedFilter findById(Long id);
	List<UserDetailDefinedFilter> findAll();
	List<UserDefinedFilterDto> findAllForUser();
	UserDetailDefinedFilter save(UserDefinedFilterDto userDefinedFilterDto);
}
