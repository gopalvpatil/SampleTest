package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.on;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;
import com.westernalliancebancorp.positivepay.annotation.RollbackForEmulatedUser;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CompanyDao;
import com.westernalliancebancorp.positivepay.dao.PermissionDao;
import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.dao.UserActivityDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.UserHistoryDao;
import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.FindUserDto;
import com.westernalliancebancorp.positivepay.dto.ManageUserDto;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.dto.UserPermissionDto;
import com.westernalliancebancorp.positivepay.exception.PositivePayException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserHistory;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.RoleService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.web.validator.UserValidator;

/**
 * @author Gopal Patil
 */

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Loggable
    private Logger logger;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    BankDao bankDao;
    @Autowired
    UserValidator userValidator;
    @Autowired
    CompanyDao companyDao;
    @Autowired
    UserActivityDao userActivityDao;
    @Autowired
    BatchDao batchDao;
    @Autowired
    UserHistoryDao userHistoryDao;
    @Autowired
    CompanyService companyService;
    @Autowired
    AccountService accountService;
    @Autowired
    RoleService roleService;
    @Autowired
    PermissionDao permissionDao;
    @Autowired
    RoleDao roleDao;
    @Value("${positivepay.user.default.base64.encoded.password}")
    private String base64EncodedPassword;

    @Override
    public UserDetail findByName(String userName) {
        return userDetailDao.findByUserName(userName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @RollbackForEmulatedUser
    public UserDetail createUser(UserDto userDto, BindingResult bindingResult) {
        userValidator.validate(userDto, bindingResult);
        UserDetail userDetail = new UserDetail();
        logger.debug("Trying to convert UserDto to user");
        BeanUtils.copyProperties(userDto, userDetail);
        logger.debug("Conversion completed");
        if (!SecurityUtility.isLoggedInUserBankAdmin()) {
            userDetail.setActive(Boolean.FALSE);
        }
        userDetailDao.save(userDetail);
        logger.info(String.format("User %s %s with the user name %s is saved successfully and the user id is %d", userDetail.getFirstName(), userDetail.getLastName(), userDetail.getUserName(), userDetail.getId()));
        assignUserRoles(userDto);
        return userDetail;
    }
    
    /**
     * Add user to system
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @RollbackForEmulatedUser
    public long addUser(UserDto userDto) {
    	
    	try{//Check if username exists
    		if(userDetailDao.findByUserName(userDto.getUserName()) != null)
        		throw new RuntimeException("Cannot add user. UserName already exists.");
    	}catch(EmptyResultDataAccessException ex){
    		//ignore no result error
    	}
    	Role userRole = null;
    	
		
		UserDetail userDetail = new UserDetail();
		userDetail.setUserName(userDto.getUserName());
		userDetail.setCorporateUserName(userDto.getUserName());
		userDetail.setFirstName(userDto.getFirstName());
		userDetail.setLastName(userDto.getLastName());
        if(userDetail.getPassword() == null || userDetail.getPassword().isEmpty()){
            userDetail.setPassword(base64EncodedPassword);
        }
		userDetail.setEmail(userDto.getEmail());
		String institutionId = "0";//Default to Zero
		if(userDto.getBankId() != null){
			Bank bank = bankDao.findById(userDto.getBankId());
			if(bank.getAssignedBankNumber() != null)
				institutionId = String.valueOf(bank.getAssignedBankNumber());
		}
		userDetail.setInstitutionId(institutionId);
		userDetail.setActive(userDto.isActive());
		userDetail.setLocked(userDto.isLocked());
		if(userDto.getRoleId() != null){//Assign role
			userRole = roleDao.findById(userDto.getRoleId());
			userDetail.setBaseRole(userRole);
		}
		
		//Save User
		userDetail = userDetailDao.save(userDetail);
		
		if(userRole != null){
			//Insert User Permissions
			Set<Permission> userPermission = userRole.getPermissions();
			List<Long> userPermissionIds = Lambda.extract(userPermission, Lambda.on(Permission.class).getId());
			batchDao.insertUserPermissions(userDetail.getId(), new HashSet<Long>(userPermissionIds));
		}
		
		//Assign user to company
		if(userDto.getCompanyId() != null){
			Company company = companyDao.findById(userDto.getCompanyId());
			if(company.getAccounts().size() == 0){
				throw new RuntimeException("Cannot add User to Company as no account(s) is present in company.");
			}
			Map<Long, Set<Long>> companyUsersMap = new HashMap<Long, Set<Long>>();
			Set<Long> userIds = new HashSet<Long>();
			userIds.add(userDetail.getId());
			companyUsersMap.put(userDto.getCompanyId(), userIds);
			batchDao.assignUsersToCompany(companyUsersMap);
		}
		
    	return userDetail.getId();
    }

    /**
     * Pass null as a parameter incase we would like to know the logged in user admin or not.
     * If any background process is using it then user name is mandatory else RuntimeException will be thrown.
     * @param userName
     * @return
     */
    @Deprecated
    private boolean isAdminUser(String userName) {
        if (userName != null) {
            UserDetail userDetail = userDetailDao.findByUserName(userName);
            Role baseRole = userDetail.getBaseRole();
            return baseRole.getName().equals(Role.Roles.ROLE_CORPORATE_ADMIN.name());
        } else {
            return SecurityUtility.isLoggedInUserCorporateAdmin();
        }
    }

    /*private boolean isBankAdminUser(String userName) {
        if (userName != null) {
            UserDetail userDetail = userDetailDao.findByUserName(userName);
            Role baseRole = userDetail.getBaseRole();
            return baseRole.getName().equals(Role.Roles.ROLE_BANK_ADMIN.name());
        } else {
            return SecurityUtility.isLoggedInUserBankAdmin();
        }
    }*/

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @RollbackForEmulatedUser
    public void assignUserRoles(UserDto userDto) {
        logger.info("Started saving the user roles.");
        Long bankId = userDto.getBankId();
        String accountNumber = userDto.getAccountNumber();
        logger.debug(String.format("Trying to find the account with number %s and the bank Id %d", accountNumber, bankId));
        Account account = accountDao.findByAccountNumberAndBankId(accountNumber, bankId);
        logger.debug("Account found with number " + accountNumber + " and the bank Id " + bankId);
        UserDetail userDetail = null;
        if (userDto.getUserId() == null) {
            if (userDto.getUserName() == null) {
                throw new RuntimeException("Id ot Username is mandatory to create the user");
            } else {
                userDetail = userDetailDao.findByUserName(userDto.getUserName());
            }
        } else {
            userDetail = userDetailDao.findById(userDto.getUserId());
        }
        logger.info(String.format("User found with user id %d", userDetail.getId()));
    }

    /**
     *  @deprecated Use getUserBanksByCompany
     */
    @Override 
    @Deprecated
    public List<Bank> getUserBanks() {
        String userName = SecurityUtility.getPrincipal();
        Set<Bank> banks = new HashSet<Bank>();
        //Get banks associated with accounts.
        if (SecurityUtility.isLoggedInUserBankAdmin()) {
            return bankDao.findAll();
        } else {
            UserDetail userDetail = userDetailDao.findByUserName(userName);
            Set<Account> accounts = userDetail.getAccounts();
            for (Account account : accounts) {
                Bank bank = bankDao.findById(account.getBank().getId());
                banks.add(bank);
                break;
            }
        }
        return new ArrayList<Bank>(banks);
    }

    /**
     * Shows all the accounts to which the user belongs to
     * Only refer to UserAccountRole table to get the accounts that he belongs to.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Account> getUserAccounts() {
        String userName = SecurityUtility.getPrincipal();
        if (SecurityUtility.isLoggedInUserBankAdmin()) {
            return accountDao.findAll();
        } else {
        	Set<Account> accounts = new HashSet<Account>();
            UserDetail userDetail = userDetailDao.findByUserName(userName);
            Set<Account> userAccounts = userDetail.getAccounts();
            if (userAccounts != null && !userAccounts.isEmpty()) {
                List<Long> accountIds = Lambda.extract(userAccounts, Lambda.on(Account.class).getId());
                return accountDao.findByAccountIds(accountIds);
            }
            return new ArrayList<Account>(accounts);
        }
    }

    /**
     * Show all the accounts for the users for a given company id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(cacheName = "getUserAccountsByCompanyId")
    public List<Account> getUserAccountsByCompanyId(String companyId) {
        //Get all User accounts
        List<Account> allUserAccounts = getUserAccounts();
        List<Account> accountsInCompany = accountDao.findAllByCompanyId(companyId);
        //Now filter only those accounts which belong to the company id and the user
        Set<Account> accounts = new HashSet<Account>();
        for (Account userAccount : allUserAccounts) {
            for (Account accountInCompany : accountsInCompany) {
                if (userAccount.isActive() && (userAccount.getId() == accountInCompany.getId())) {
                    accounts.add(userAccount);
                }
            }
        }
        return new ArrayList<Account>(accounts);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<CompanyDTO> getCompanies() {
    	List<Company> companyList = new ArrayList<Company>();
    	if(SecurityUtility.isLoggedInUserBankAdmin()) {
    		companyList = companyDao.findAllActiveCompanies();
    	} else {
    		companyList.add(getLoggedInUserCompany());
    	}
    	List<CompanyDTO> companyDtoList = new ArrayList<CompanyDTO>();
		for(Company fromCompany: companyList) {
			CompanyDTO toCompany = new CompanyDTO();
			BeanUtils.copyProperties(fromCompany, toCompany);
			companyDtoList.add(toCompany);
		}
        return companyDtoList;
    }

    @Override
    /**
     * Do not blacklist it as we need to insert emulated cookie history when the emulated user logg inn and logs out
     */
    public UserActivity createOrRetrieveUserActivity(UserActivity.Activity activity) {
        UserActivity userActivity = null;
        userActivity = userActivityDao.findByName(activity.name());
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setName(activity.name());
            userActivity.setDescription(activity.getDescription());
            userActivityDao.save(userActivity);
        }
        return userActivity;
    }

    /**
     * Show all the users for a given company id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<UserDto> getUsersByCompanyId(Long companyId) {
        //Get all accounts for the given company id
        return batchDao.findUsersBy(companyId);
    }
    
    /**
	 * This method check whether mapping exists in User_account if yes then
	 * update else insert
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    @RollbackForEmulatedUser
	public void saveOrUpdate(UserDetail userDetail) {
		// If mapping exists then update else create...
		userDetailDao.update(userDetail);

	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Set<Account> getAccountByUserDetailId(long userId) {
		Set<Account> accountList = accountDao.getAccountByUserDetailId(userId);
		return accountList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<UserDetail> showArchivedUsers() {
		List<UserDetail> users = userDetailDao.showArchivedUsers();
		return users;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @RollbackForEmulatedUser
	public UserDetail update(UserDetail userDetail) {
		return userDetailDao.update(userDetail);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Set<UserDetail> getUserDetailByAccountId(String number) {
		Set<UserDetail> accountList = userDetailDao
				.findUserDetailByAccountNumber(number);
		return accountList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Set<UserDetail> findByCompanyId(long companyId) {
		Set<UserDetail> list = userDetailDao
				.findUserDetailByCompanyId(companyId);
		return list;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Set<UserDetail> getAllTheUsersInTheBank(long bankId) {
		Set<UserDetail> accountList = userDetailDao
				.findUserDetailBySpecificBank(bankId);
		return accountList;
	}

	@Override
	public List<UserHistory> getUserDetailHistoryBy(Long userId, Integer startIndex, Integer maxResult) {
		return userHistoryDao.getUserDetailHistoryBy(userId, startIndex, maxResult);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(cacheName = "userCompany")
	/**
	 * This method should never be called for a ROLE_BANK_ADMIN user as he will be associated with multiple companies and there are no accounts associated with him
	 */
	public Company getUserCompany(String userName)
	{
		UserDetail userDetail = userDetailDao.findUserDetailAndAccountsByUserName(userName);
        Set<Account> accounts = userDetail.getAccounts();
        if(accounts == null || accounts.isEmpty()) {
        	if(SecurityUtility.isUserBankAdmin(userName, userDetailDao)) {
        		throw new RuntimeException("Invalid API usage - This method should not be called for bank admin.");
        	} else{
        		throw new RuntimeException(String.format("No company found as %s has no accounts associated.", userName));
        	}
        }
        Company company = null;
        for(Account account:accounts) {
            company = companyService.findById(account.getCompany().getId());
            return company;
        }
        return company;
	}

    @Override
    public Company getLoggedInUserCompany() {
        String userName = SecurityUtility.getPrincipal();
        return getUserCompany(userName);
    }

    @Override

    public Group<Permission> getUserPermission(String userName) {
        UserDetail userDetail = userDetailDao.findByUserName(userName);
        Set<Permission> permissionSet = userDetailDao.getPermissionsByUserDetailId(userDetail.getId());
        Group<Permission> permissionGroup = group(permissionSet, by(on(Permission.class).getType()));
        return permissionGroup;
    }

    @Override
    public Group<Permission> getLoggedInUserPermission() {
        String userName = SecurityUtility.getPrincipal();
        return getUserPermission(userName);
    }

    @Override
    @Transactional(readOnly=true)
    @Cacheable(cacheName = "userRoles")
    public UserDetail getUserRoleByUserName(String userName) {
        return userDetailDao.findUserRoleByUserName(userName);
    }

	@Override
	@Transactional(readOnly=true)
    //@Cacheable(cacheName = "userRolesAndPermissions") caching this is resulting in undesirable results after saving
	public UserPermissionDto getUserRoleAndPermissions(String userName) {

		// Fetch User permission first
		UserDetail userDetail = userDetailDao
				.findUserPermissionsAndRoleByUserName(userName);

		UserPermissionDto dto = new UserPermissionDto();
		dto.setUserName(userName);
		if (userDetail.getBaseRole() != null)
			dto.setBaseRoleId(userDetail.getBaseRole().getId());
		if (userDetail.getPermissions() != null) {
			Map<Long, Boolean> permissions = new HashMap<Long, Boolean>();
			for (Permission permission : userDetail.getPermissions()) {
				permissions.put(permission.getId(), true);
			}
			dto.setPermissions(permissions);
		}

		// Fetch available roles which can be assigned to user
		List<Role> allRoles = roleDao.findAll();
		for (Role role : allRoles) {
			UserPermissionDto.Role obj = new UserPermissionDto.Role();
			obj.setId(role.getId());
			obj.setLabel(role.getLabel());
			for (Permission permission : role.getPermissions()) {//TODO: here the Permissions fetch will happen. Improve the query to fetch at once
				obj.getPermissionIds().add(permission.getId());
			}
			dto.getRoles().add(obj);
		}
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    @RollbackForEmulatedUser
    @TriggersRemove(cacheName = {"userPermissions","userRoles"},when=When.AFTER_METHOD_INVOCATION, removeAll=true)
	public void saveUserRoleAndPermissions(String userName, UserPermissionDto dto) {
		
		UserDetail userDetail = userDetailDao.findUserPermissionsAndRoleByUserName(userName);

		UserDetail loggedInuserDetail = userDetailDao.findByUserName(SecurityUtility.getPrincipal());
        List<Long> accessiblePermissionIds = Lambda.extract(loggedInuserDetail.getPermissions(), Lambda.on(Permission.class).getId());
        
        Set<Long> selectedPermissions = new HashSet<Long>();
		if(dto.getPermissions()!=null) {
			for(Long id : dto.getPermissions().keySet()) {
				if(dto.getPermissions().get(id))//Select only permission having true value
					selectedPermissions.add(id);
			}
		}
		
		if(!accessiblePermissionIds.containsAll(selectedPermissions)) {
			throw new PositivePayException(String.format("User[%s] is not allowed to manage permissions which he dont have access", loggedInuserDetail.getUserName()));
		}
	
		//First delete all allowed permission by the user. 
		batchDao.deleteUserPermissions(userDetail.getId(), new HashSet<Long>(accessiblePermissionIds));
		//Now insert the selected Permissions
		batchDao.insertUserPermissions(userDetail.getId(), selectedPermissions);

		//Save User Permissions - Ends
		
		if(SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)) {
			// Save User Role - starts
			Long originalRoleId = userDetail.getBaseRole() != null ? userDetail.getBaseRole().getId() : null;
			Long userRoleId = dto.getBaseRoleId();
			if (StringUtils.isNotBlank(dto.getNewRoleName())) {
				userRoleId = roleService.saveRole(dto.getNewRoleName(), dto.getNewRoleName(), new ArrayList<Long>(selectedPermissions));
			}
			if ((userRoleId == null && originalRoleId != null) // role is removed
					|| (userRoleId != null && !userRoleId.equals(originalRoleId))) { // role is changed
				batchDao.updateUserBaseRole(userDetail.getId(), userRoleId);
			}
		// Save User Role - Ends
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ManageUserDto> findUserBySearchCriteria(FindUserDto criteria) {
		return batchDao.findUserBySearchCriteria(criteria);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
    @RollbackForEmulatedUser
	public void saveManageUsersData(List<ManageUserDto> manageUsers) {
		
		//Update user status and InsitutionId
		batchDao.updateUserStatusAndInstitutionId(manageUsers);
		
		// Create a map of company and users to assign
		Map<Long, Set<Long>> companyUsersMap = new HashMap<Long, Set<Long>>();
		for (ManageUserDto manageUser : manageUsers) {
			if (manageUser.getCompanyChanged() != null
					&& manageUser.getCompanyChanged().equals(true)) {
				Long companyId = manageUser.getCompanyId();
				Long userId = manageUser.getUserId();
				Set<Long> companyUsers = companyUsersMap.get(companyId);
				if (companyUsers == null) {
					companyUsers = new HashSet<Long>();
					companyUsersMap.put(companyId, companyUsers);
				}
				companyUsers.add(userId);
			}
		}
		if(companyUsersMap.size() > 0)
			batchDao.assignUsersToCompany(companyUsersMap);
	}

    @Cacheable(cacheName = "userPermissions")
    public List<Permission> findResourcesByUser(String userName) {
        return permissionDao.findResourcesByUser(userName);
    }

    @Override
    @Cacheable(cacheName = "displayableCheckStatuses")
    public List<CheckStatusDto> getDisplayableCheckStatus() {
        return batchDao.getDisplayableCheckStatuses();
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Boolean saveUserHistory(UserActivity.Activity activity, UserHistory userHistory, String userName) throws Exception{
        UserDetail userDetail = userDetailDao.findByUserName(userName);
        userHistory.setUserDetail(userDetail);
        userHistory.setUserActivity(ModelUtils.createOrRetrieveUserActivity(UserActivity.Activity.SYSTEM_MESSAGE_READ, userActivityDao));
        UserHistory uh =  userHistoryDao.save(userHistory);
        if(uh!=null)
        	return true;
		return false;
	}
    
    
    @Override
    @Transactional(readOnly=true)
    public List<Bank> getUserBanksByCompany() {
    	if(SecurityUtility.isLoggedInUserBankAdmin()) {
    			return bankDao.findAll(); 
    	}else{
    		List<Company> companies = companyDao.findAllByUserName(SecurityUtility.getPrincipal());
    		List<Bank> tempBanks = Lambda.extract(companies, Lambda.on(Company.class).getBank());
    		List<Bank> banks = new ArrayList<Bank>();
    		for(Bank bank : tempBanks) {
    			if(!banks.contains(bank))
    				banks.add(bank);
    		}
    		return banks;
    	}
    }
    
    @Override
    @Transactional(readOnly=true)
    public List<Company> getUserCompanies() {
    	List<Company> companies = null;
    	if(SecurityUtility.isLoggedInUserBankAdmin()) {
    		companies = companyDao.findAll();
    	}else{
    		companies = companyDao.findAllByUserName(SecurityUtility.getPrincipal());
    	}
    	return companies;
    }
}
