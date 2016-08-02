package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/21/13
 * Time: 9:00 PM
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class SampleJpaDaoTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private BankDao bankDao;

    @Autowired
    private AuthenticationEntryPoint bottomLineEntryPoint;

    private static final String comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc pulvinar est id ipsum suscipit feugiat";

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value = true)
    public void testCreateDB() {

    }

    @Before
    public void setup(){
        PositivePayThreadLocal.set("gduggira");
    }
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value = true)
    @Ignore
    public void testCreate() {
        //testDelete();
        //Create Users
        Set<UserDetail> userDetails = createUser(10);
        for (UserDetail userDetail : userDetails) {
            userDetailDao.save(userDetail);
        }
        //Create Roles
        Set<Role> roles = createRoles(10);
        for (Role role : roles) {
            roleDao.save(role);
        }
        //Create Banks
        Map<Integer, Bank> bankMap = createBanks(10);
        for (int i = 0; i < 10; i++) {
            bankDao.save(bankMap.get(new Integer(i)));
        }

        Map<Integer, Account> accountMap = createAccounts(10, bankMap.get(2).getId());
        //Create UserAccountRole using the previous entries.
        for (int i = 0; i < 9; i++) {
            UserDetail[] userDetailArray = userDetails.toArray(new UserDetail[0]);
            Role[] roleArray = roles.toArray(new Role[0]);
            Bank[] bankArray = bankMap.values().toArray(new Bank[0]);
            //UserAccountRole userAccountRole = new UserAccountRole();
            //userAccountRole.setAccount(accountMap.get(i));
            //userAccountRole.setUserDetail(userDetailArray[i]);
            //userAccountRole.setRole(roleArray[i]);
            //userBankRoleDao.save(userAccountRole);
        }
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value = true)
    @Ignore
    public void testSso() throws IOException, ServletException {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addParameter("name1", "value1");
        httpServletRequest.addParameter("name2", "value1");
        httpServletRequest.addParameter("name3", "value1");
        httpServletRequest.addParameter("name4", "value1");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        bottomLineEntryPoint.commence(httpServletRequest, mockHttpServletResponse, null);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value = true)
    @Ignore
    public void testDelete() {
        List<UserDetail> userDetails = userDetailDao.findAll();
        List<Bank> banks = bankDao.findAll();
        List<Role> roles = roleDao.findAll();
      //  List<UserAccountRole> userAccountRoles = userBankRoleDao.findAll();
       // for (UserAccountRole userAccountRole : userAccountRoles) {
        //    userBankRoleDao.delete(userAccountRole);
       // }
        for (UserDetail userDetail : userDetails) {
            userDetailDao.delete(userDetail);
        }
        for (Role role : roles) {
            roleDao.delete(role);
        }
        for (Bank bank : banks) {
            bankDao.delete(bank);
        }
    }

    private Set<Role> createRoles(int numberOfRoles) {
        Set<Role> roles = new HashSet(numberOfRoles);
        for (int i = 0; i < numberOfRoles; i++) {
            Role role = new Role();
            role.setName("ROLE_" + UUID.randomUUID());
            role.setDescription("DESCRIPTION_" + i);
            roles.add(role);
        }
        return roles;
    }

    private Map<Integer, Bank> createBanks(int numberOfBanks) {
        Map<Integer, Bank> banksMap = new HashMap<Integer, Bank>(numberOfBanks);
        Bank previousBank = null;
        for (int i = 0; i < numberOfBanks; i++) {
            Bank bank = new Bank();
            bank.setName("BANK_" + i);
            bank.setRoutingNumber("RoutingNumber_" + i);
            if (previousBank != null) {
                bank.setParent(previousBank);
            }
            banksMap.put(i, bank);
            previousBank = bank;
        }
        return banksMap;
    }

    private Map<Integer, Account> createAccounts(int numberOfAccounts, long bankId) {
        Map<Integer, Account> accountsMap = new HashMap<Integer, Account>(numberOfAccounts);
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < numberOfAccounts; i++) {
            Account account = new Account();
            Bank bank = new Bank();
            bank.setId(bankId);
            account.setBank(bank);
            long accountNumber = 0;
            do {
                accountNumber = random.nextLong();
            } while (accountNumber < 0);
            account.setNumber(accountNumber + "");
            accountsMap.put(i, account);
        }
        return accountsMap;
    }

    private Set<UserDetail> createUser(int numberOfUsers) {
        Set<UserDetail> userDetails = new HashSet(numberOfUsers);
        for (int i = 0; i < numberOfUsers; i++) {
            Calendar calendar = Calendar.getInstance();
            UserDetail userDetail = new UserDetail();
            userDetail.setActive(Boolean.TRUE);
            userDetail.setEmail("userEmail" + UUID.randomUUID().toString() + "@wal.com");
            userDetail.setFirstName("FirstName " + i);
            userDetail.setPassword("user");
            userDetail.setLastName("LastName " + i);
            calendar.add(Calendar.YEAR, -1);
            userDetail.setLocked(Boolean.FALSE);
            userDetail.setUserName("user" + UUID.randomUUID().toString());
            calendar.add(Calendar.YEAR, 1);
            calendar.add(Calendar.HOUR, -12);
            userDetails.add(userDetail);
        }
        return userDetails;
    }
}
