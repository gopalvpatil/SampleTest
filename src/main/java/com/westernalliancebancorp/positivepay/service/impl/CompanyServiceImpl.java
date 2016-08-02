package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;
import com.westernalliancebancorp.positivepay.annotation.RollbackForEmulatedUser;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.CompanyDao;
import com.westernalliancebancorp.positivepay.dao.ContactDao;
import com.westernalliancebancorp.positivepay.dao.DecisionWindowDao;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.CompanyDtoBuilder;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Contact;
import com.westernalliancebancorp.positivepay.model.DecisionWindow;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Loggable
    private Logger logger;

    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private BankDao bankDao;
    @Autowired
    private DecisionWindowDao decisionWindowDao;

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.service.CompanyService#update(com.westernalliancebancorp.positivepay.model.Company)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Company update(Company company) {
        return companyDao.update(company);
    }

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.service.CompanyService#save(com.westernalliancebancorp.positivepay.model.Company)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Company save(Company company) {
        return companyDao.save(company);
    }

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.service.CompanyService#delete(com.westernalliancebancorp.positivepay.model.Company)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Company company) {
        companyDao.delete(company);
    }

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.service.CompanyService#findById(java.lang.Long)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Company findById(Long id) {
        return companyDao.findById(id);
    }

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.service.CompanyService#findAll()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(cacheName = "findAllCompanies")
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.service.CompanyService#findByBankId(java.lang.Long)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(cacheName = "companyDtosByBankId")
    public List<CompanyDTO> findByBankId(Long bankId) throws Exception {
        //Get all accounts for the given bank
        List<Account> accounts = accountDao.findAllByBankId(bankId);
        Set<Company> companies = new HashSet<Company>();

        for(Account account : accounts) {
            Company companyToAdd = account.getCompany();
            companies.add(companyToAdd);
        }

        List<CompanyDTO> companyDTOList = new ArrayList<CompanyDTO>();
        for(Company fromCompany: companies) {
            //CompanyDTO toCompany = new CompanyDTO();
            //BeanUtils.copyProperties(toCompany, fromCompany);
        	if(fromCompany.isActive()){
        		CompanyDTO toCompany = CompanyDtoBuilder.getCompanyDtoFromEntity(fromCompany);
                companyDTOList.add(toCompany);
        	}
        }

        return companyDTOList;
    }

    @Override
    public List<Company> findAllByBankIds(List<Long> bankIds) {
        return companyDao.findAllByBankIds(bankIds);
    }

    @Override
    public Company getCompanyDetail(Long companyId) {
        return companyDao.getCompanyDetail(companyId);
    }

    @Override
    public Company getCompanyDetails(Long companyId) {
        return companyDao.getCompanyDetails(companyId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Contact saveCompanyContact(Contact contact) {
        return contactDao.save(contact);
    }
    @Override
    public Contact updateContactDetails(Contact contact) {
        contactDao.update(contact);
        return contact;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void makeCompanyAndItsAccountInactive(Long companyId) {
        Company company = companyDao.findById(companyId);
        company.setIsActive(false);
        for(Account account : company.getAccounts()) {//Fetch accounts here
            account.setActive(false);
            accountDao.update(account); //TODO: Optimize code and make it batch update perhaps by CascadeUpdate on company
        }
        companyDao.update(company);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @RollbackForEmulatedUser
    @TriggersRemove(cacheName = {"findAllCompanies"},when=When.AFTER_METHOD_INVOCATION, removeAll=true)
    public Long saveOrUpdateCompany(CompanyDTO companyDTO) {

        boolean makeAccountsInactive = false;
        Company company = null;
        if(companyDTO.getId() != null) {
            company = companyDao.findById(companyDTO.getId());
            //check if company was made inactive
            if(!companyDTO.getActive() && company.isActive())
                makeAccountsInactive = true;
        }
        else {
            company = new Company();
            Bank bank = bankDao.getReference(companyDTO.getBankId());
            company.setBank(bank);
        }
        CompanyDtoBuilder.updateCompanyFromCompanyDto(company, companyDTO);

        //Save Company
        if(company.getId() == null) {
        	//WALPP-360 fix. Setting default window decision to 1 for new company
        	DecisionWindow decisionWindow = decisionWindowDao.getReference(1L);
        	company.setDecisionWindow(decisionWindow);
            company = companyDao.save(company);
        } else {
            company = companyDao.update(company);
        }

        Contact contact = null;
        if(company.getContacts() != null && company.getContacts().size() > 0) {
            contact = company.getContacts().iterator().next();//Update first contact only
        } else {
            contact = new Contact();
            contact.setCompany(company);
            contact.setPrimaryContact(true);
        }
        CompanyDtoBuilder.updateContactFromCompanyDto(contact, companyDTO);
        //Save Contact
        if(contact.getId() == null) {
            contact = contactDao.save(contact);
        } else {
            contact = contactDao.update(contact);
        }

        if(makeAccountsInactive) {//make companies and its accounts inactive
            makeCompanyAndItsAccountInactive(company.getId());
        }

        return company.getId();
    }

    @Override
    public List<Company> findAllByUserName(String userName) {
    	List<Company> ret = null;
    	
    	if(SecurityUtility.isLoggedInUserBankAdmin()) {
    		ret = companyDao.findAll();
    	} else {
            ret = companyDao.findAllByUserName(userName);    		
    	}
        
        return ret;
    }
}