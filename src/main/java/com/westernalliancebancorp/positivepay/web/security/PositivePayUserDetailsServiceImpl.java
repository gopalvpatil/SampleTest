package com.westernalliancebancorp.positivepay.web.security;

import ch.lambdaj.Lambda;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.dao.FileMappingDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dto.UserPermissionDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

/**
 * <tt>PositivePayUserDetailsServiceImpl</tt> implementation which retrieves the user details
 * (username, password, enabled flag, and authorities) from a database using JPA queries.
 *
 * @author Giridhar Duggirala
 */
@Service("userDetailsService")
public class PositivePayUserDetailsServiceImpl implements UserDetailsService {
    @Loggable
    private Logger logger;
    
    @Autowired
    UserDetailDao userDetailDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    AccountDao accountDao;

    @Autowired
    FileMappingDao fileMappingDao;

    @Autowired
    UserService userService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetail userDetail = userService.getUserRoleByUserName(username);
        Collection<GrantedAuthority> grantedAuthorities= new ArrayList<GrantedAuthority>();
        /*
        Role role = null;
        
        String[] cAdminPermissionList = corporateAdminPermissionList.split(",");
        Set<Permission> permissions = userDetail.getPermissions();
        boolean isUserCorporateAdmin = Boolean.FALSE;
        for(Permission permission:permissions) {
            for(String validPermission:cAdminPermissionList) {
                if(validPermission.equals(permission.getName())) {
                    isUserCorporateAdmin = Boolean.TRUE;
                    break;
                }
            }
        }
        
        
        if (userDetail.getBaseRole().getName().equals(bankAdminRoleName)) {
            role = roleDao.findByName(bankAdminRoleName);
            logger.info("Bank Admin User");
        }else if(isUserCorporateAdmin) {
            role = roleDao.findByName(corporateAdminRoleName);
            logger.info("Corporate Admin User");
        }else{
            logger.info("Corporate User");
            role = roleDao.findByName(corporateUserRoleName);
        }
        */
        
        /*if (userDetail.getBaseRole() != null) {
            role = roleDao.findById(userDetail.getBaseRole().getId());
        } else {
            throw new RuntimeException("No role associated with the " + username + " please check !!");
        }*/
        /*Set<Account> accounts = userDetail.getAccounts();
        if(accounts == null || accounts.isEmpty()) {
            if(role.getName().equals(Role.Roles.ROLE_BANK_ADMIN)) {
                //TODO: We some how has to know to which bank this user is admin for
                //and get all the accounts under that bank
                throw new RuntimeException(String.format("Functionality yet to be implemented for the role \"%s\" trying to figure out how to associate a bank to the user", Role.Roles.ROLE_BANK_ADMIN.getDescription()));
            }else{
                if(role.getName().equals(Role.Roles.ROLE_CORPORATE_USER)) {
                    throw new RuntimeException(String.format("User \"%s\" has role \"%s\" and no accounts are associated, please check", username, Role.Roles.ROLE_CORPORATE_USER.getDescription()));
                }
            }
        }
        List<Long> accountIds = Lambda.extract(accounts, Lambda.on(Account.class).getId());
        List<Account> accountList = accountDao.findByAccountIds(accountIds);
        for(Account account:accountList) {
            PositivePayGrantedAuthority positivePayGrantedAuthority = new SimplePositivePayGrantedAuthorityImpl(role.getName(), account.getId());
            positivePayGrantedAuthorities.add(positivePayGrantedAuthority);
        }*/

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userDetail.getBaseRole().getName());
        grantedAuthorities.add(grantedAuthority);
        return new PositivePayUser(username, userDetail.getPassword(), grantedAuthorities);
    }
}
