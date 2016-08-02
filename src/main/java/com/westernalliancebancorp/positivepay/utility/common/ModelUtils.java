package com.westernalliancebancorp.positivepay.utility.common;

import java.util.*;

import org.springframework.beans.BeanUtils;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.ActionDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.LinkageTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.dao.UserActivityDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.FileType;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.LinkageType;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.Workflow;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * User: gduggirala
 * Date: 4/4/14
 * Time: 11:24 AM
 */


public class ModelUtils {
	
    public static FileMetaData retrieveOrCreateManualEntryFile(FileDao fileDao,FileTypeDao fileTypeDao) {

        FileMetaData fileMetaData = fileDao.findByFileName(FileMetaData.MANUAL_ENTRY_FILE_NAME);
        if (fileMetaData == null) {
            fileMetaData = new FileMetaData();
            fileMetaData.setFileName(FileMetaData.MANUAL_ENTRY_FILE_NAME);
            fileMetaData.setOriginalFileName(FileMetaData.MANUAL_ENTRY_ORIGINAL_FILE_NAME);
            fileMetaData.setStatus(FileMetaData.STATUS.PROCESSED);
            fileMetaData.setFileSize(0l);
            fileMetaData.setItemsReceived(0l);
            fileMetaData.setUploadDirectory(FileMetaData.MANUAL_ENTRY_UPLOAD_DIRECTORY);
            fileMetaData.setChecksum(FileMetaData.MANUAL_ENTRY_CHECKSUM);
            fileMetaData.setFileType(retrieveOrCreateFileType(FileType.FILE_TYPE.MANUAL_ENTRY,fileTypeDao));
            fileDao.save(fileMetaData);
        }
        return fileMetaData;
    }

    public static FileMetaData retrieveOrCreateExceptionalReferenceDataFile(FileDao fileDao,FileTypeDao fileTypeDao) {
        FileMetaData fileMetaData = fileDao.findByFileName(FileMetaData.EXCEPTIONAL_REFERENCE_DATA_FILE_NAME);
        if (fileMetaData == null) {
            fileMetaData = new FileMetaData();
            fileMetaData.setFileName(FileMetaData.EXCEPTIONAL_REFERENCE_DATA_FILE_NAME);
            fileMetaData.setOriginalFileName(FileMetaData.EXCEPTIONAL_REFERENCE_DATA_ORIGINAL_FILE_NAME);
            fileMetaData.setStatus(FileMetaData.STATUS.PROCESSED);
            fileMetaData.setFileSize(0l);
            fileMetaData.setItemsReceived(0l);
            fileMetaData.setUploadDirectory(FileMetaData.EXCEPTIONAL_REFERENCE_DATA_UPLOAD_DIRECTORY);
            fileMetaData.setChecksum(FileMetaData.EXCEPTIONAL_REFERENCE_DATA_CHECKSUM);
            fileMetaData.setFileType(retrieveOrCreateFileType(FileType.FILE_TYPE.EXCEPTIONAL_REFERENCE_DATA,fileTypeDao));
            fileDao.save(fileMetaData);
        }
        return fileMetaData;
    }

    public static List<Long> executeWithNoCriteria(UserDetailDao userDetailDao, BatchDao batchDao) {
        if (SecurityUtility.isUserBankAdmin(SecurityUtility.getPrincipal(), userDetailDao)) {
            List<Long> accountIdsList = batchDao.getAllAccountsIds();
            return accountIdsList;
        } else {
            Set<Account> accountSet = userDetailDao.getAccountByUserName(SecurityUtility.getPrincipal());
            if (accountSet != null && !accountSet.isEmpty()) {
                List<Long> accountIdsList = Lambda.extract(accountSet, Lambda.on(Account.class).getId());
                return accountIdsList;
            }
            return Collections.EMPTY_LIST;
        }
    }

    public static ItemType getCheckDetailItemType(ReferenceData.ITEM_TYPE item_type, ItemTypeDao itemTypeDao) {
        if(item_type.name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.name())) {
            return  itemTypeDao.findByCode(ItemType.CODE.P.name());
        } else if(item_type.name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.name()) || item_type.name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP_PRESENTED.name())) {
            return itemTypeDao.findByCode(ItemType.CODE.S.name());
        } else {
            throw new RuntimeException("Unknown ItemType "+item_type);
        }
    }

    public static List<ItemType> getItemTypesFromPermission(ItemTypeDao itemTypeDao, List<Permission> permissionList) {
        List<ItemType> itemTypeList = new ArrayList<ItemType>();
        for (Permission permission : permissionList) {
            if (permission.getName().equals(Permission.NAME.ISSUED)) {
                ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
                itemTypeList.add(itemType);
            } else if (permission.getName().equals(Permission.NAME.VOID)) {
                ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.V.name());
                itemTypeList.add(itemType);
            } else if (permission.getName().equals(Permission.NAME.PAID) || permission.getName().equals(Permission.NAME.CREATE_PAID)) {
                ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.P.name());
                itemTypeList.add(itemType);
            } else if (permission.getName().equals(Permission.NAME.STOP)) {
                ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
                itemTypeList.add(itemType);
            } /*else if (permission.getName().equals(Permission.NAME.CREATE_DEPOSIT)) {
                ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.D.name());
                itemTypeList.add(itemType);
            }*/
        }
        return itemTypeList;
    }

    public static CheckStatus retrieveOrCreateCheckStatus(WorkflowManager workflowManager, String targetStatusName, CheckStatusDao checkStatusDao) {
        com.westernalliancebancorp.positivepay.model.CheckStatus checkStatus = null;
        checkStatus = checkStatusDao.findByNameAndVersion(targetStatusName, workflowManager.getSupportedVersion());
        if (checkStatus == null) {
            checkStatus = new com.westernalliancebancorp.positivepay.model.CheckStatus();
            checkStatus.setName(targetStatusName);
            checkStatus.setVersion(workflowManager.getSupportedVersion());
            checkStatus.setDescription(workflowManager.getStatusDescription(targetStatusName));
            checkStatus.setExceptionalStatus(workflowManager.isExceptionalStatus(targetStatusName));
            checkStatusDao.save(checkStatus);
        }
        return checkStatus;
    }

    @Deprecated
    //Please use ReferenceDataService.correctCheckNumber,correctAccountNumber
    public static void getCorrectedReferenceData(ReferenceData referenceData, String checkNumber, ReferenceDataDao refereceDataDao) {
        if (!referenceData.getCheckNumber().equals(checkNumber)) {
            referenceData.setCheckNumber(checkNumber);
            refereceDataDao.save(referenceData);
        }
    }

    @Deprecated
    //Please use ReferenceDataService.correctCheckNumber,correctAccountNumber
    public static void getCorrectedReferenceData(ReferenceData referenceData, Account account, ReferenceDataDao refereceDataDao) {
        if (!referenceData.getAccount().equals(account)) {
            referenceData.setAccount(account);
            refereceDataDao.update(referenceData);
        }
    }

   /* public static LinkageType retrieveOrCreateLinkageType(String linkageTypeName, LinkageTypeDao linkageTypeDao) {
        LinkageType linkageType = null;
        linkageType = linkageTypeDao.findByName(linkageTypeName);
        if (linkageType == null) {
            linkageType = new LinkageType();
            linkageType.setName(linkageTypeName);
            for (LinkageType.NAME name : LinkageType.NAME.values()) {
                if (linkageTypeName.equals(name.toString())) {
                    linkageType.setDescription(name.getDescription());
                }
            }

            linkageTypeDao.save(linkageType);
        }
        return linkageType;
    }*/

    public static ReferenceData getCorrectedReferenceData(ReferenceData referenceData, Check check, ReferenceDataDao referenceDataDao, CheckDao checkDao) {
        List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAndAccountId(check.getCheckNumber(), check.getAccount().getId());
        if (referenceDataList == null || referenceDataList.isEmpty()) {
            //"ReferenceDate record with the check id " + check.getId() + " and the account number " + check.getAccount().getId() + " is not existing so add this reference data");
            referenceData.setCheckNumber(check.getCheckNumber());
            referenceData.setAccount(check.getAccount());
            referenceDataDao.update(referenceData);
            return referenceData;
        } else if (referenceDataList.size() == 1) {
            ReferenceData referenceData1 = referenceDataList.get(0);
            //A reference data record is already existing.. let's see if this is associated with any other check..
            if (ReferenceData.STATUS.NOT_PROCESSED.name().equals(referenceData1.getStatus().name())) {
                //There is referenceData record but that record is not associated with any check
                return referenceData1;
            }
            Check otherCheck = checkDao.findByReferenceDataId(referenceData.getId());
            if (otherCheck == null) {
                //ReferenceData status is PROCESSED however I see that it is not associated with any check.. so this reference data can be used
                return referenceData1;
            }
        } else if (referenceDataList.size() > 1) {
            //More than one is a wrong scenario.. you cannot have more than one reference data with the same check number and the account number.
        }
        return referenceData;
    }

    public static ReferenceData getCorrectedReferenceData(ReferenceData referenceData, Check check, ReferenceDataDao referenceDataDao, CheckDao checkDao, ReferenceData.ITEM_TYPE referenceDataItemType) {
        List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAccountIdAndItemType(check.getCheckNumber(), check.getAccount().getId(), referenceDataItemType);
        if (referenceDataList == null || referenceDataList.isEmpty()) {
            //"ReferenceDate record with the check id " + check.getId() + " and the account number " + check.getAccount().getId() + " is not existing so add this reference data");
            referenceData.setCheckNumber(check.getCheckNumber());
            referenceData.setAccount(check.getAccount());
            referenceDataDao.update(referenceData);
            return referenceData;
        } else if (referenceDataList.size() == 1) {
            ReferenceData referenceData1 = referenceDataList.get(0);
            //A reference data record is already existing.. let's see if this is associated with any other check..
            if (ReferenceData.STATUS.NOT_PROCESSED.name().equals(referenceData1.getStatus().name())) {
                //There is referenceData record but that record is not associated with any check
                return referenceData1;
            }
            Check otherCheck = checkDao.findByReferenceDataId(referenceData.getId());
            if (otherCheck == null) {
                //ReferenceData status is PROCESSED however I see that it is not associated with any check.. so this reference data can be used
                return referenceData1;
            }
        }
        return referenceData;
    }

    public static ReferenceData getReferenceDataFromHistoryByCheckIdandCurrentStatus(Check check, CheckHistoryDao checkHistoryDao) {

        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckIdandStatusId(check.getId(), check.getCheckStatus().getId());
        ReferenceData latestReferenceData = checkHistory.get(0).getReferenceData();
        return latestReferenceData;
    }

    public static ReferenceData getReferenceDataFromHistoryByCheckIdandStatus(Check check, CheckHistoryDao checkHistoryDao) {

        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        ReferenceData latestReferenceData = checkHistory.get(0).getReferenceData();
        return latestReferenceData;
    }

    public static ReferenceData getReferenceDataFromHistory(Check check, String name, CheckStatusDao checkStatusDao, CheckHistoryDao checkHistoryDao, WorkflowDao workflowDao) {
        List<CheckHistory> checkHistoriesStatus = new ArrayList<CheckHistory>();
        Workflow workflow = workflowDao.findById(check.getWorkflow().getId());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion(name, workflow.getVersion());
        List<CheckHistory> checkHistories = checkHistoryDao.findByCheckId(check.getId());
        for (CheckHistory checkHistory : checkHistories) {
            if (checkHistory.getTargetCheckStatus().getId().equals(checkStatus.getId())) {
                checkHistoriesStatus.add(checkHistory);
            }
        }
        Collections.sort(checkHistoriesStatus);
        ReferenceData latestReferenceData = checkHistoriesStatus.get(checkHistoriesStatus.size() - 1).getReferenceData();
        return latestReferenceData;
    }

    public static Action createOrRetrieveAction(Action.ACTION_NAME actionName, int version, Action.ACTION_TYPE action_type, ActionDao actionDao) {
        Action action = null;
        action = actionDao.findByNameAndVersion(actionName.getName(), version, action_type);
        if (action == null) {
            action = new Action();
            action.setActionType(action_type);
            action.setDescription(actionName.getDescription());
            action.setName(actionName.getName());
            action.setVersion(version);
            action.isAdminAction(actionName.isAnAdminAction());
            actionDao.save(action);
        }
        return action;
    }
    
    public static ExceptionType retrieveOrCreateExceptionType(ExceptionType.EXCEPTION_TYPE exceptionName, ExceptionTypeDao exceptionTypeDao) {
    	ExceptionType exceptionType = exceptionTypeDao.findByName(exceptionName);
        if (exceptionType == null) {
        	exceptionType = new ExceptionType();
        	exceptionType.setDescription(exceptionName.toString());
        	exceptionType.setExceptionType(exceptionName);
        	exceptionTypeDao.save(exceptionType);
        }
        return exceptionType;
    }
    
    public static FileType retrieveOrCreateFileType(FileType.FILE_TYPE fileName, FileTypeDao fileTypeDao) {
    	FileType fileType = fileTypeDao.findByName(fileName);
        if (fileType == null) {
        	fileType = new FileType();
        	fileType.setDescription(fileName.toString());
        	fileType.setName(fileName);
        	fileTypeDao.save(fileType);
        }
        return fileType;
    }

    public static void markCheckDeleted(Check check, Map<String, Object> userData, CheckStatusDao checkStatusDao, ActionDao actionDao, CheckHistoryDao checkHistoryDao, WorkflowManagerFactory workflowManagerFactory) {
        CheckStatus deleteCheckStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId()), "delete", checkStatusDao);
        handleCheckHistory(check, userData, deleteCheckStatus, Action.ACTION_NAME.DELETE, "Delete", checkStatusDao, actionDao, checkHistoryDao);
        check.setCheckStatus(deleteCheckStatus);
        check.setReferenceData(null);
        check.setDigest(UUID.randomUUID().toString());
    }

    public static CheckHistory handleCheckHistory(Check check, Map<String, Object> userData, CheckStatus targetCheckStatus, Action.ACTION_NAME actionName, String actionDescription, CheckStatusDao checkStatusDao, ActionDao actionDao, CheckHistoryDao checkHistoryDao) {
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        Action action = ModelUtils.createOrRetrieveAction(actionName,
                check.getWorkflow().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(check);
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setAction(action);
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setId(null);
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistory.setAuditInfo(new AuditInfo());
        if (userData.get(WorkflowService.STANDARD_MAP_KEYS.SYSTEM_COMMENT.name()) == null || ((String) userData.get(WorkflowService.STANDARD_MAP_KEYS.SYSTEM_COMMENT.name())).isEmpty()) {
            checkHistory.setSystemComment(String.format("Payment status has been moved from \"%s\" to \"%s\" by taking the action \"%s\"", checkStatus.getDescription(), targetCheckStatus.getDescription(), actionDescription));
        } else {
            checkHistory.setSystemComment((String) userData.get(WorkflowService.STANDARD_MAP_KEYS.SYSTEM_COMMENT.name()));
        }
        if (userData.get(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name()) == null || ((String) userData.get(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name())).isEmpty()) {
            checkHistory.setUserComment("None");
        } else {
            checkHistory.setUserComment((String) userData.get(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name()));
        }
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }

    public static CheckHistory handleChangeCheckNumberNonWorkflowCheckHistory(Check check, CheckStatus targetCheckStatus, Action.ACTION_NAME actionName, String oldCheckNumber, String newCheckNumber, String actionDescription, CheckStatusDao checkStatusDao, ActionDao actionDao, CheckHistoryDao checkHistoryDao) {
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        Action action = ModelUtils.createOrRetrieveAction(actionName,
                check.getWorkflow().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setAction(action);
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setId(null);
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(String.format("While payment status is being moved from " +
                "\"%s\" to \"%s\" by taking the action \"%s\", check number has been changed from \"%s\" to \"%s\" ",
                checkStatus.getDescription(), targetCheckStatus.getDescription(), actionDescription, oldCheckNumber, newCheckNumber));
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }

    public static CheckHistory handleChangeAccountNumberNonWorkflowCheckHistory(Check check, CheckStatus targetCheckStatus, Action.ACTION_NAME actionName, String oldAccountNumber, String newAccountNumber, String actionDescription, CheckStatusDao checkStatusDao, ActionDao actionDao, CheckHistoryDao checkHistoryDao) {
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        Action action = ModelUtils.createOrRetrieveAction(actionName,
                check.getWorkflow().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setAction(action);
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setId(null);
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistory.setSystemComment(String.format("While payment status is being moved from " +
                "\"%s\" to \"%s\" by taking the action \"%s\", Account number has been changed from \"%s\" to \"%s\" ",
                checkStatus.getDescription(), targetCheckStatus.getDescription(), actionDescription, oldAccountNumber, newAccountNumber));
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }

    public static LinkageType retrieveOrCreateLinkageType(LinkageType.NAME linkageName, LinkageTypeDao linkageTypeDao) {
        LinkageType linkageType = linkageTypeDao.findByName(linkageName);
        if (linkageType == null) {
            linkageType = new LinkageType();
            linkageType.setDescription(linkageName.getDescription());
            linkageType.setName(linkageName);
            linkageTypeDao.save(linkageType);
        }
        return linkageType;
    }

    public static Role createOrRetrieveRole(String roleName,String roleDescription,  RoleDao roleDao) {
        Role role = roleDao.findByName(roleName);
        if(role == null) {
            role = new Role();
            role.setName(roleName);
            role.setDescription(roleDescription);
            roleDao.save(role);
            return role;
        }
        return role;
    }

    public static UserActivity createOrRetrieveUserActivity(UserActivity.Activity activity,UserActivityDao userActivityDao) {
        UserActivity userActivity= userActivityDao.findByName(activity.name());
        if(userActivity==null) {
            userActivity = new UserActivity();
            userActivity.setName(activity.name());
            userActivity.setDescription(activity.getDescription());
            userActivityDao.save(userActivity);
            return userActivity;
        }
        return userActivity;
    }
    
    public static ExceptionStatus createOrRetrieveExceptionStatus(ExceptionStatus.STATUS status, ExceptionStatusDao exceptionStatusDao) {
    	ExceptionStatus exceptionStatus = exceptionStatusDao.findByName(status.name());
    	if(exceptionStatus == null) {
    		exceptionStatus = new ExceptionStatus();
    		exceptionStatus.setName(status.name());
    		exceptionStatus.setDescription(status.getDescription());
    		exceptionStatusDao.save(exceptionStatus);
    		return exceptionStatus;
    	}
    	return exceptionStatus;
    }
}
