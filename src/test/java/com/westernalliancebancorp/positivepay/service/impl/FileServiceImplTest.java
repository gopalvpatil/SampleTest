package com.westernalliancebancorp.positivepay.service.impl;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.service.FileMetaDataService;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/12/14
 * Time: 4:27 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class FileServiceImplTest {
    @Autowired
    FileMetaDataService fileMetaDataService;
    @Autowired
    AccountDao accountDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    private BatchDao batchDao;
    @Autowired
    private ItemTypeDao itemTypeDao;

   /* @Test
    public void testProcessExceptionChecks() throws Exception {
        Map<String, List<Check>> map = new HashMap<String, List<Check>>();
/*        map.put(ExceptionalCheck.EXCEPTION_STATUS.ACCOUNT_NOT_RELATED_USER.name(), getChecks());
        map.put(ExceptionalCheck.EXCEPTION_STATUS.DUPLICATE_CHECK_IN_FILE_AMOUNT_VARIED_EXCEPTION.name(), getChecks());
        map.put(ExceptionalCheck.EXCEPTION_STATUS.DUPLICATE_CHECK_IN_FILE_EXCEPTION.name(), getChecks());
        map.put(ExceptionalCheck.EXCEPTION_STATUS.DUPLICATE_CHECK_IN_CHECK_DETAIL_EXCEPTION.name(), getChecks());
        fileMetaDataService.processExceptionChecks(map);
    }
*/
    private List<Check> getChecks() {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        List<Check> checkList = new ArrayList<Check>();
        for (int i = 0; i <= 2; i++) {
            Check check = new Check();
            check.setAccount(account);
            check.setIssuedAmount(BigDecimal.valueOf(120002));
            check.setCheckNumber(UUID.randomUUID().toString());
            check.setItemType(itemType);
            check.setIssueDate(getStaleDate());
            check.setPayee("Micheal More");
            check.setLineNumber(i+"");
            check.setRoutingNumber("1111-1111-111-" + i);
            AuditInfo auditInfo = new AuditInfo();
            auditInfo.setCreatedBy("gduggira");
            auditInfo.setDateCreated(new Date());
            auditInfo.setDateModified(new Date());
            auditInfo.setModifiedBy("gduggira");
            check.setAuditInfo(auditInfo);
            checkList.add(check);
        }
        return checkList;
    }

    private Date getStaleDate() {
        Integer days = new Integer(185);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        String date = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition position = new ParsePosition(0);
        return formatter.parse(date, position);
    }
    
    @Test
    public void testinsertAllChecks()
    {
        List<Check> checksToBeSaved = new ArrayList<Check>();
        List<Check> checks = getChecks();
        for (Check check : checks) {
            check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
            WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
            CheckStatus targetCheckStatus = retrieveOrCreateCheckStatus(check, workflowManager, "start");
            check.setCheckStatus(targetCheckStatus);
            checksToBeSaved.add(check);
        }
        batchDao.insertAllChecks(checksToBeSaved);
    }
    
    private CheckStatus retrieveOrCreateCheckStatus(Check check, WorkflowManager workflowManager, String targetStatusName) {
        CheckStatus checkStatus = null;
        checkStatus = checkStatusDao.findByNameAndVersion(targetStatusName, workflowManager.getSupportedVersion());
        if (checkStatus == null) {
            checkStatus = new CheckStatus();
            checkStatus.setName(targetStatusName);
            checkStatus.setVersion(workflowManager.getSupportedVersion());
            checkStatus.setDescription(workflowManager.getStatusDescription(targetStatusName));
            checkStatusDao.save(checkStatus);
        }
        return checkStatus;
    }
}
