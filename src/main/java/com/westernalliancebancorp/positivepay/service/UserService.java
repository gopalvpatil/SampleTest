package com.westernalliancebancorp.positivepay.service;

import java.util.List;
import java.util.Set;

import org.springframework.validation.BindingResult;

import ch.lambdaj.group.Group;

import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.FindUserDto;
import com.westernalliancebancorp.positivepay.dto.ManageUserDto;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.dto.UserPermissionDto;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserHistory;

/**
 * @author Gopal Patil
 *
 */
public interface UserService {
    UserDetail createUser(UserDto userDto, BindingResult bindingResult);
    long addUser(UserDto userDto);
    void assignUserRoles(UserDto userDto);
    UserDetail findByName(String userName);
    List<Bank> getUserBanks();
    List<Account> getUserAccounts();
    List<Account> getUserAccountsByCompanyId(String companyId);
    List<CompanyDTO> getCompanies();
    UserActivity createOrRetrieveUserActivity(UserActivity.Activity activity);
    List<UserDto> getUsersByCompanyId(Long companyId);
    void saveOrUpdate(UserDetail userDetail);
    List<UserHistory> getUserDetailHistoryBy(Long userId, Integer startIndex, Integer maxResult);
    Company getUserCompany(String userName);
    Company getLoggedInUserCompany();
    Group<Permission> getUserPermission(String userName);
    Group<Permission> getLoggedInUserPermission();
	Set<Account> getAccountByUserDetailId(long userId);
    List<UserDetail> showArchivedUsers();
    UserDetail update(UserDetail userDetail);
    Set<UserDetail> getUserDetailByAccountId(String number);
    Set<UserDetail> getAllTheUsersInTheBank(long bankId);
    Set<UserDetail> findByCompanyId(long companyId);
    UserPermissionDto getUserRoleAndPermissions(String userName);
    void saveUserRoleAndPermissions(String userName, UserPermissionDto dto);
    List<ManageUserDto> findUserBySearchCriteria(FindUserDto criteria);
    void saveManageUsersData(List<ManageUserDto> manageUsers);
    List<Permission> findResourcesByUser(String userName);

    List<CheckStatusDto> getDisplayableCheckStatus();

    UserDetail getUserRoleByUserName(String userName);
    
    Boolean saveUserHistory(UserActivity.Activity activity, UserHistory userHistory,String userName) throws Exception;
    List<Bank> getUserBanksByCompany();
    List<Company> getUserCompanies();
}
