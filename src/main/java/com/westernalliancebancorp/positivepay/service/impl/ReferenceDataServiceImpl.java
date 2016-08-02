package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.PPUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.ReferenceDataService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

@Service
public class ReferenceDataServiceImpl implements ReferenceDataService {

    /**
     * The logger object
     */
    @Loggable
    private Logger logger;
    @Autowired
    private ReferenceDataDao referenceDataDao;
    @Autowired
    private BatchDao batchDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    FileUploadUtils fileUploadUtils;
    @Autowired
    ExceptionalReferenceDataDao exceptionalReferenceDataDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData update(ReferenceData check) {
        return referenceDataDao.update(check);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData save(ReferenceData check) {
        return referenceDataDao.save(check);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(ReferenceData check) {
        referenceDataDao.delete(check);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData findById(Long id) {
        return referenceDataDao.findById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ReferenceData> findAll() {
        return referenceDataDao.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ReferenceData> saveAll(List<ReferenceData> referenceDataList) {
        //Using JDBC Template batch Updates
        List<ReferenceData> referenceDataListToBeSaved = new ArrayList<ReferenceData>();
        for (ReferenceData referenceData : referenceDataList) {
            AuditInfo auditInfo = new AuditInfo();
            String name = SecurityUtility.getPrincipal();
            auditInfo.setCreatedBy(name);
            auditInfo.setDateCreated(new Date());
            auditInfo.setDateModified(new Date());
            auditInfo.setModifiedBy(name);
            referenceData.setAuditInfo(auditInfo);
            referenceDataListToBeSaved.add(referenceData);
        }
        return batchDao.insertAllReferenceData(referenceDataListToBeSaved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData correctTheReferenceData(Long referenceDataId, String checkNumber, String accountNumber) {
        ReferenceData referenceData = referenceDataDao.findById(referenceDataId);
        String strippedCheckNumber = PPUtils.stripLeadingZeros(checkNumber);
        if (checkNumber != null && !checkNumber.isEmpty()) {
            if (!referenceData.getCheckNumber().equals(strippedCheckNumber)) {
                referenceData.setCheckNumber(strippedCheckNumber);
            }
        }
        if (accountNumber != null && !accountNumber.isEmpty()) {
            Account existingAccount = accountDao.findById(referenceData.getAccount().getId());
            Account newAccount = accountDao.findByAccountNumberAndBankId(accountNumber, existingAccount.getBank().getId());
            if(newAccount == null){
                throw new RuntimeException("No account is found with account number "+accountNumber+" and bank id "+existingAccount.getBank().getId());
            }
            referenceData.setAccount(newAccount);
        }
        Account account = accountDao.findById(referenceData.getAccount().getId());
        referenceData.setDigest(fileUploadUtils.getDigest(account.getNumber(), referenceData.getCheckNumber()));
        referenceDataDao.update(referenceData);
        return referenceData;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData correctCheckNumber(Long referenceDataId, String checkNumber) {
        return correctTheReferenceData(referenceDataId, checkNumber, null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData correctAccountNumber(Long referenceDataId, String accountNumber) {
        return correctTheReferenceData(referenceDataId, null, accountNumber);
    }
}
