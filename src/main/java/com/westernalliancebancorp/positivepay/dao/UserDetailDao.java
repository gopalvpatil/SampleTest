package com.westernalliancebancorp.positivepay.dao;

import java.util.List;
import java.util.Set;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.UserDetail;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:05 PM
 */
public interface UserDetailDao extends GenericDao<UserDetail, Long> {
    UserDetail  findByUserName(String userName);
    List<UserDetail> findBy(String corporateUserName, String institutionId);
    Set<Account> getAccountByUserDetailId(long id);
    UserDetail findUserDetailAndAccountsByUserName(String userName);
    Set<Permission> getPermissionsByUserDetailId(Long userId);
	List<UserDetail> showArchivedUsers();
    Set<UserDetail> findUserDetailByAccountNumber(String number);
    Set<UserDetail> findUserDetailByCompanyId(Long companyId);
    Set<UserDetail> findUserDetailBySpecificBank(Long bankId);
    UserDetail findUserPermissionsAndRoleByUserName(String userName);
    Set<Account> getAccountByUserName(String userName);
    UserDetail findUserRoleByUserName(String userName);
    boolean isUserFullRecon(String userName);
}
