/**
 *
 */
package com.westernalliancebancorp.positivepay.dao.impl.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dto.AccountDto;
import com.westernalliancebancorp.positivepay.dto.AccountInfoDto;
import com.westernalliancebancorp.positivepay.dto.AccountPaymentInfoDto;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.DataCriteriaDto;
import com.westernalliancebancorp.positivepay.dto.DecisionWindowDto;
import com.westernalliancebancorp.positivepay.dto.ExceptionCheckDto;
import com.westernalliancebancorp.positivepay.dto.ExceptionalReferenceDataDto;
import com.westernalliancebancorp.positivepay.dto.FindUserDto;
import com.westernalliancebancorp.positivepay.dto.ItemErrorRecordsDto;
import com.westernalliancebancorp.positivepay.dto.JobDto;
import com.westernalliancebancorp.positivepay.dto.ManageUserDto;
import com.westernalliancebancorp.positivepay.dto.PaymentByDateDto;
import com.westernalliancebancorp.positivepay.dto.PaymentDetailDto;
import com.westernalliancebancorp.positivepay.dto.RecentFileDto;
import com.westernalliancebancorp.positivepay.dto.SearchParameterDto;
import com.westernalliancebancorp.positivepay.dto.UserDetailFilterSearchValueDto;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionType.EXCEPTION_TYPE;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.UserDetailFilterSearchValue;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.PPUtils;
import com.westernalliancebancorp.positivepay.utility.common.RelationalOperator;

/**
 * Class to have single method to insert all checks
 *
 * @author Anand Kumar
 */
@Repository
public class BatchJdbcDao extends JdbcTemplate implements BatchDao {

    @Loggable
    private Logger logger;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Value("${payments.database.view.name}")
	private String paymentsView;
    @Value("${items.database.view.name}")
	private String itemsView;
    @Value("${payments.items.max.records}")
    private String maxRecords;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Autowired
    public BatchJdbcDao(@Qualifier("dataSource") BasicDataSource dataSource) {
        super.setDataSource(dataSource);
    }

    public List<Long> findAllStaleChecks(List<Long> accountIds, String staleDate, List<Long> checkStatusIds) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "select" +
                " checkDetail.id as checkId" +
                " from" +
                " check_detail checkDetail " +
                " where" +
                " checkDetail.account_id in (" +
                ":accountIds" +
                ") and" +
                " checkDetail.check_status_id in (:checkStatusIds) and checkDetail.issue_date <= :staleDate";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIds", accountIds);
        namedParameters.put("checkStatusIds", checkStatusIds);
        namedParameters.put("staleDate", staleDate);
        List<Long> checkIds = namedParameterJdbcTemplate.queryForList(sql, namedParameters, Long.class);
        return checkIds;
    }

    public List<ReferenceData> insertAllReferenceData(List<ReferenceData> referenceDataList) {
        String sql = "INSERT INTO REFERENCE_DATA " +
                "(ACCOUNT_ID, AMOUNT, CHECK_NUMBER, ITEM_TYPE, PAID_DATE, STOP_DATE, ASSIGNED_BANK_NUMBER, " +
                "CREATED_BY, DATE_CREATED, DATE_MODIFIED, MODIFIED_BY, TRACE_NUMBER, FILE_META_DATA_ID, STATUS, FILE_IMPORT_LINE_NUMBER, " +
                "DUPLICATE_IDENTIFIER, STOP_PRESENTED_DATE, STOP_PRESENTED_REASON) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (ReferenceData referenceData : referenceDataList) {
            parameters.add(new Object[]{
                    referenceData.getAccount().getId(),
                    referenceData.getAmount(),
                    referenceData.getCheckNumber(),
                    referenceData.getItemType().name(),
                    referenceData.getPaidDate(),
                    referenceData.getStopDate(),
                    referenceData.getAssignedBankNumber(),
                    referenceData.getAuditInfo().getCreatedBy(),
                    referenceData.getAuditInfo().getDateCreated(),
                    referenceData.getAuditInfo().getDateModified(),
                    referenceData.getAuditInfo().getModifiedBy(),
                    referenceData.getTraceNumber(),
                    referenceData.getFileMetaData().getId(),
                    referenceData.getStatus().name(),
                    referenceData.getLineNumber(),
                    referenceData.getDigest() !=null ? referenceData.getDigest() : referenceData.getAccount().getNumber()+""+PPUtils.stripLeadingZeros(referenceData.getCheckNumber()),
                    referenceData.getStopPresentedDate(),
                    referenceData.getStopPresentedReason()
            });
        }
        super.batchUpdate(sql, parameters);
        return referenceDataList;
    }

    public List<ExceptionalReferenceData> insertAllExceptionalReferenceData(List<ExceptionalReferenceData> exceptionalReferenceDataList) {
        String sql = "INSERT INTO REFERENCE_DATA_EXCEPTION " +
                "(ACCOUNT_NUMBER, AMOUNT, CHECK_NUMBER, ITEM_TYPE, PAID_DATE, STOP_DATE, ASSIGNED_BANK_NUMBER, " +
                "CREATED_BY, DATE_CREATED, DATE_MODIFIED, MODIFIED_BY, TRACE_NUMBER, EXCEPTION_STATUS, FILE_IMPORT_LINE_NUMBER, " +
                "STOP_PRESENTED_DATE, STOP_PRESENTED_REASON, EXCEPTION_TYPE_ID, FILE_META_DATA_ID, REFERENCE_DATA_ID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (ExceptionalReferenceData exceptionalReferenceData : exceptionalReferenceDataList) {
            parameters.add(new Object[]{
            		exceptionalReferenceData.getAccountNumber(),
            		exceptionalReferenceData.getAmount(),
            		exceptionalReferenceData.getCheckNumber(),
            		exceptionalReferenceData.getItemType().name(),
            		exceptionalReferenceData.getPaidDate(),
            		exceptionalReferenceData.getStopDate(),
            		exceptionalReferenceData.getAssignedBankNumber(),
            		exceptionalReferenceData.getAuditInfo().getCreatedBy(),
            		exceptionalReferenceData.getAuditInfo().getDateCreated(),
            		exceptionalReferenceData.getAuditInfo().getDateModified(),
            		exceptionalReferenceData.getAuditInfo().getModifiedBy(),
            		exceptionalReferenceData.getTraceNumber(),
            		exceptionalReferenceData.getExceptionStatus().name(),
            		exceptionalReferenceData.getLineNumber(),
            		exceptionalReferenceData.getStopPresentedDate(),
            		exceptionalReferenceData.getStopPresentedReason(),
                    exceptionalReferenceData.getExceptionType().getId(),
                    exceptionalReferenceData.getFileMetaData().getId(),
                    exceptionalReferenceData.getReferenceData()==null?null:exceptionalReferenceData.getReferenceData().getId()
            });
        }
        super.batchUpdate(sql, parameters);
        return exceptionalReferenceDataList;
    }
    
    public List<ExceptionalCheck> insertAllExceptionalChecks(List<ExceptionalCheck> exceptionalChecks) {
        String sql = "INSERT INTO CHECK_DETAIL_EXCEPTION " +
                "(ACCOUNT_NUMBER, ROUTING_NUMBER, ISSUED_AMOUNT, CHECK_NUMBER, ISSUE_CODE, ISSUE_DATE, PAYEE, " +
                "CREATED_BY, DATE_CREATED, DATE_MODIFIED, MODIFIED_BY, EXCEPTION_TYPE_ID, EXCEPTION_STATUS_ID, FILE_IMPORT_LINE_NUMBER, FILE_META_DATA_ID,CHECK_STATUS) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (ExceptionalCheck exceptionalCheck : exceptionalChecks) {
            parameters.add(new Object[]{
                    exceptionalCheck.getAccountNumber(),
                    exceptionalCheck.getRoutingNumber(),
                    exceptionalCheck.getIssuedAmount(),
                    exceptionalCheck.getCheckNumber(),
                    exceptionalCheck.getIssueCode(),
                    exceptionalCheck.getIssueDate(),
                    exceptionalCheck.getPayee(),
                    exceptionalCheck.getAuditInfo().getCreatedBy(),
                    exceptionalCheck.getAuditInfo().getDateCreated(),
                    exceptionalCheck.getAuditInfo().getDateModified(),
                    exceptionalCheck.getAuditInfo().getModifiedBy(),
                    //Manual entry items may not have these values so we should check for null before trying to get their id's
                    exceptionalCheck.getExceptionType()==null?null:exceptionalCheck.getExceptionType().getId(),
                    exceptionalCheck.getExceptionStatus()==null?null:exceptionalCheck.getExceptionStatus().getId(),
                    exceptionalCheck.getLineNumber(),
                    exceptionalCheck.getFileMetaData()==null?null:exceptionalCheck.getFileMetaData().getId(),
                    exceptionalCheck.getCheckStatus()==null?null:exceptionalCheck.getCheckStatus().name()
            });
        }
        super.batchUpdate(sql, parameters);
        return exceptionalChecks;
    }

    public List<Check> insertAllChecks(List<Check> checks) {
        String sql = "INSERT INTO CHECK_DETAIL " +
                "(ACCOUNT_ID, ROUTING_NUMBER, ISSUED_AMOUNT, CHECK_NUMBER, ISSUE_DATE, PAYEE, " +
                "CREATED_BY, DATE_CREATED, DATE_MODIFIED, MODIFIED_BY, WORKFLOW_ID, FILE_META_DATA_ID, " +
                "CHECK_STATUS_ID, DUPLICATE_IDENTIFIER, FILE_IMPORT_LINE_NUMBER, ACTION_ID, ITEM_TYPE_ID,VOID_DATE,VOID_AMOUNT,MATCH_STATUS) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (Check check : checks) {
            parameters.add(new Object[]{
                    check.getAccount().getId(),
                    check.getRoutingNumber(),
                    check.getIssuedAmount(),
                    check.getCheckNumber(),
                    check.getIssueDate(),
                    check.getPayee(),
                    check.getAuditInfo().getCreatedBy(),
                    check.getAuditInfo().getDateCreated(),
                    check.getAuditInfo().getDateModified(),
                    check.getAuditInfo().getModifiedBy(),
                    check.getWorkflow().getId(),
                    check.getFileMetaData() != null ? check.getFileMetaData().getId() : null,
                    check.getCheckStatus().getId(),
                    check.getDigest() !=null ? check.getDigest() : check.getAccount().getNumber()+""+PPUtils.stripLeadingZeros(check.getCheckNumber()),
                    check.getLineNumber(),
                    null,
                    check.getItemType().getId(),
                    check.getVoidDate(),
                    check.getVoidAmount(),
                    check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus()
            });
        }
        super.batchUpdate(sql, parameters);
        return checks;
    }

    public List<Check> insertAllChecksNoFileMetadata(List<Check> checks) {
        String sql = "INSERT INTO CHECK_DETAIL " +
                "(ACCOUNT_ID, ROUTING_NUMBER, ISSUED_AMOUNT, CHECK_NUMBER, ISSUE_DATE, PAYEE, " +
                "CREATED_BY, DATE_CREATED, DATE_MODIFIED, MODIFIED_BY, WORKFLOW_ID, FILE_IMPORT_LINE_NUMBER, ITEM_TYPE_ID,MATCH_STATUS) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (Check check : checks) {
            parameters.add(new Object[]{
                    check.getAccount().getId(),
                    check.getRoutingNumber(),
                    check.getIssuedAmount(),
                    check.getCheckNumber(),
                    check.getIssueDate(),
                    check.getPayee(),
                    check.getAuditInfo().getCreatedBy(),
                    check.getAuditInfo().getDateCreated(),
                    check.getAuditInfo().getDateModified(),
                    check.getAuditInfo().getModifiedBy(),
                    check.getWorkflow().getId(),
                    //check.getLineNumber(),
                    //check.getLineItem(),
                    check.getItemType().getId(),
                    check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus()
            });
        }
        super.batchUpdate(sql, parameters);
        return checks;
    }

    public List<Check> findAllDuplicateChecks(List<Check> checks) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        List<String> digests = new ArrayList<String>();
        for (Check check : checks) {
            digests.add(check.getDigest());
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("digests", digests);
        String sql = "SELECT cd.*, a.id as account_id, a.name as account_name, a.number as account_number, it.id as ITEM_TYPE_ID, it.name as ITEM_NAME, it.item_code as ITEM_CODE, it.description as ITEM_DESCRIPTION "+
    			"FROM CHECK_DETAIL cd, ACCOUNT a, ITEM_TYPE it "+
    			"WHERE cd.account_id = a.id " +
                "AND cd.item_type_id = it.id "+
    			"AND DUPLICATE_IDENTIFIER IN (:digests)";
        List<Check> duplicateChecksInDB = namedParameterJdbcTemplate.query(sql, params, new CheckRowMapper());
        List<Check> duplicateChecks = new ArrayList<Check>();
        for(Check checkFromDb: duplicateChecksInDB) {
        	for(Check check: checks) {
        		if(checkFromDb.equals(check) && (checkFromDb.getItemType().getName().equalsIgnoreCase(check.getItemType().getName()))) {
        			duplicateChecks.add(check);
        		}
        	}
        }
        return duplicateChecks;
    }
    
    public List<Check> findAllDuplicateChecksForManualEntry(List<Check> checks) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        List<String> digests = new ArrayList<String>();
        for (Check check : checks) {
            digests.add(check.getDigest());
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("digests", digests);
        String sql = "SELECT cd.*, a.id as account_id, a.name as account_name, a.number as account_number, it.id as ITEM_TYPE_ID, it.name as ITEM_NAME, it.item_code as ITEM_CODE, it.description as ITEM_DESCRIPTION "+
    			"FROM CHECK_DETAIL cd, ACCOUNT a, ITEM_TYPE it "+
    			"WHERE cd.account_id = a.id " +
                "AND cd.item_type_id = it.id "+
    			"AND DUPLICATE_IDENTIFIER IN (:digests)";
        List<Check> duplicateChecksInDB = namedParameterJdbcTemplate.query(sql, params, new CheckRowMapper());
        List<Check> duplicateChecks = new ArrayList<Check>();
        for(Check checkFromDb: duplicateChecksInDB) {
        	for(Check check: checks) {
        		if(checkFromDb.equals(check)) {
        			duplicateChecks.add(checkFromDb);
        		}
        	}
        }
        return duplicateChecks;
    }
    
    public List<ReferenceData> findAllDuplicateReferenceDatas(List<ReferenceData> referenceDataList) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        List<String> digests = new ArrayList<String>();
        for (ReferenceData referenceData : referenceDataList) {
            digests.add(referenceData.getDigest());
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("digests", digests);
        String sql = "SELECT rd.*, a.id as account_id, a.name as account_name, a.number as account_number "+
        			"FROM REFERENCE_DATA rd, ACCOUNT a "+
        			"WHERE rd.account_id = a.id "+
        			"AND DUPLICATE_IDENTIFIER IN (:digests)";
        List<ReferenceData> duplicateReferenceDataListInDB = namedParameterJdbcTemplate.query(sql, params, new ReferenceDataRowMapper());
        List<ReferenceData> duplicateReferenceDataList = new ArrayList<ReferenceData>();
        for(ReferenceData referenceDataFromDb: duplicateReferenceDataListInDB) {
        	for(ReferenceData referenceData: referenceDataList) {
        		if(referenceDataFromDb.equals(referenceData)) {
                    referenceData.setId(referenceDataFromDb.getId());
        			duplicateReferenceDataList.add(referenceData);
        		}
        	}
        }
        return duplicateReferenceDataList;
    }

    /**
     * Please use is for to grep any checks in the given accounts with the given statuses
     * ReferenceData item code will be "Paid" Amount is not part of the criteria.
     * @param accountIds
     * @param checkStatusIds
     * @return
     */
    @Override
    public List<CheckDto> findAllChecksByAnyStatusNoAmount(List<Long> accountIds,
                                                           List<Long> checkStatusIds, ReferenceData.ITEM_TYPE item_type) {
        logger.info("findAllIssuedMatchedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT DISTINCT a.ID as ID, b.ID as REF_DATA_ID FROM "
                +" CHECK_DETAIL a, REFERENCE_DATA b "
                +" WHERE "
                +" b.ACCOUNT_ID IN (:accountIds) "
                +" AND "
                +" b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
                +" AND "
                +" b.ITEM_TYPE = '"+ item_type +"' "
                +" AND "
                +" a.CHECK_STATUS_ID IN (:checkStatusIds) "
                +" AND a.DUPLICATE_IDENTIFIER = b.DUPLICATE_IDENTIFIER ";

        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIds", accountIds);
        namedParameters.put("checkStatusIds", checkStatusIds);

        List<CheckDto> checkDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new CheckDtoRowMapper());
        return checkDtos;
    }

    /**
     * Please use is for to Match all paid checks from reference data with issued checks in check detail to make them Paid
     * @param accountIds
     * @param checkStatusIds
     * @return
     */
	@Override
	public List<CheckDto> findAllIssuedMatchedChecks(List<Long> accountIds,
		List<Long> checkStatusIds) {
		logger.info("findAllIssuedMatchedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		String sql = "SELECT DISTINCT a.ID as ID, b.ID as REF_DATA_ID FROM "
		    	+" CHECK_DETAIL a, REFERENCE_DATA b "
		    	+" WHERE "
		    	+" b.ACCOUNT_ID IN (:accountIds) "
		    	+" AND "
		    	+" b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
		    	+" AND "
		    	+" b.ITEM_TYPE = '"+ ReferenceData.ITEM_TYPE.PAID +"' "
		    	+" AND "
		    	+" a.CHECK_STATUS_ID IN (:checkStatusIds) "
		        +" AND a.CHECK_NUMBER = b.CHECK_NUMBER "
		        +" AND a.ACCOUNT_ID = b.ACCOUNT_ID "
		        +" AND a.ISSUED_AMOUNT = b.AMOUNT ";
		
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("accountIds", accountIds);      
		namedParameters.put("checkStatusIds", checkStatusIds);
		
		List<CheckDto> checkDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new CheckDtoRowMapper());
		return checkDtos;
	}

    @Override
    public List<CheckDto> findAllStopButPaidChecks(List<Long> accountIds,
                                                   List<Long> checkStatusIds) {
        logger.info("findAllIssuedMatchedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT DISTINCT a.ID as ID, b.ID as REF_DATA_ID FROM "
                +" CHECK_DETAIL a, REFERENCE_DATA b "
                +" WHERE "
                +" b.ACCOUNT_ID IN (:accountIds) "
                +" AND "
                +" b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
                +" AND "
                +" b.ITEM_TYPE = '"+ ReferenceData.ITEM_TYPE.PAID +"' "
                +" AND "
                +" a.CHECK_STATUS_ID IN (:checkStatusIds) "
                +" AND a.CHECK_NUMBER = b.CHECK_NUMBER "
                +" AND a.ACCOUNT_ID = b.ACCOUNT_ID ";

        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIds", accountIds);
        namedParameters.put("checkStatusIds", checkStatusIds);

        List<CheckDto> checkDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new CheckDtoRowMapper());
        return checkDtos;
    }

    @Override
    public List<UserDto> findUsersBy(Long companyId) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT DISTINCT" +
                "    (ud.id) AS id," +
                "    ud.first_name AS firstName," +
                "    ud.last_name AS lastName," +
                "    ud.email_address AS emailAddress," +
                "    ud.username AS userName " +
                " FROM" +
                "    user_detail ud" +
                "        JOIN\n" +
                "    user_detail_account ua ON ud.id = ua.user_detail_id " +
                " WHERE" +
                "    ua.account_id IN (SELECT " +
                "            a.id AS id" +
                "        FROM" +
                "            ACCOUNT a" +
                "        WHERE" +
                "            a.company_id = (:companyId))";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("companyId", companyId);
        List<UserDto> userDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new UserDtoRowMapper());
        return userDtos;
    }

    /**
     * Find all the checks in reference data with reference data item code as "Paid"
     * and "Not Processed" for the given accountid's and statuses.
     * @param accountIds
     * @param checkStatusIds
     * @return
     */
    @Override
    public List<CheckDto> findAllPaidChecksNoAmountClauseBy(List<Long> accountIds,
                                                            List<Long> checkStatusIds) {
        logger.info("findAllIssuedMatchedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT DISTINCT a.ID as ID, b.ID as REF_DATA_ID FROM "
                +" CHECK_DETAIL a, REFERENCE_DATA b "
                +" WHERE "
                +" b.ACCOUNT_ID IN (:accountIds) "
                +" AND "
                +" b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
                +" AND "
                +" b.ITEM_TYPE = '"+ ReferenceData.ITEM_TYPE.PAID +"' "
                +" AND "
                +" a.CHECK_STATUS_ID IN (:checkStatusIds) "
                +" AND a.CHECK_NUMBER = b.CHECK_NUMBER "
                +" AND a.ACCOUNT_ID = b.ACCOUNT_ID ";

        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIds", accountIds);
        namedParameters.put("checkStatusIds", checkStatusIds);

        List<CheckDto> checkDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new CheckDtoRowMapper());
        return checkDtos;
    }

    @Override
    public List<CheckDto> findAllVoidButStopChecks(List<Long> accountIds,
                                                   List<Long> checkStatusIds) {
        logger.info("findAllIssuedMatchedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT DISTINCT a.ID as ID, b.ID as REF_DATA_ID FROM "
                +" CHECK_DETAIL a, REFERENCE_DATA b "
                +" WHERE "
                +" b.ACCOUNT_ID IN (:accountIds) "
                +" AND "
                +" b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
                +" AND "
                +" b.ITEM_TYPE = '"+ ReferenceData.ITEM_TYPE.STOP +"' "
                +" AND "
                +" a.CHECK_STATUS_ID IN (:checkStatusIds) "
                +" AND a.CHECK_NUMBER = b.CHECK_NUMBER "
                +" AND a.ACCOUNT_ID = b.ACCOUNT_ID ";

        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIds", accountIds);
        namedParameters.put("checkStatusIds", checkStatusIds);

        List<CheckDto> checkDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new CheckDtoRowMapper());
        return checkDtos;
    }
    /**
     * Please use is for Invalid Amount with matching check numbers and account numbers but amount not matching
     * @param accountIds
     * @param checkStatusIds
     * @return
     */
    @Override
    public List<CheckDto> findAllIssuedUnMatchedChecks(List<Long> accountIds,
                                                       List<Long> checkStatusIds) {
        logger.info("findAllIssuedUnMatchedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT DISTINCT a.ID, b.ID as REF_DATA_ID FROM "
                +" CHECK_DETAIL a, REFERENCE_DATA b "
                +" WHERE "
                +" b.ACCOUNT_ID IN (:accountIds) "
                +" AND "
                +" b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
                +" AND "
                +" a.CHECK_STATUS_ID IN (:checkStatusIds) "
                +" AND a.CHECK_NUMBER = b.CHECK_NUMBER "
                +" AND a.ACCOUNT_ID = b.ACCOUNT_ID "
                +" AND a.ISSUED_AMOUNT != b.AMOUNT ";

        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIds", accountIds);
        namedParameters.put("checkStatusIds", checkStatusIds);
        List<CheckDto> checkDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new CheckDtoRowMapper());
        return checkDtos;
    }
    
    /**
     * Please use is for Stop Un Processed Checks
     * @param accountIds
     * @param checkStatusIds
     * @return
     */
    @Override
	public List<CheckDto> findAllStopUnProcessedChecks(List<Long> accountIds,List<Long> checkStatusIds) {
         logger.info("findAllStopUnProcessedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
         String sql = "SELECT DISTINCT a.ID, b.ID as REF_DATA_ID FROM "
                 +" CHECK_DETAIL a, REFERENCE_DATA b "
                 +" WHERE "
                 +" b.ACCOUNT_ID IN (:accountIds) "
                 +" AND "
                 +" b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
                 +" AND "
                 +" b.ITEM_TYPE = '"+ ReferenceData.ITEM_TYPE.STOP +"' "
                 +" AND "
                 +" a.CHECK_STATUS_ID IN (:checkStatusIds) "
                 +" AND a.CHECK_NUMBER = b.CHECK_NUMBER "
                 +" AND a.ACCOUNT_ID = b.ACCOUNT_ID "
                 +" AND a.ISSUED_AMOUNT = b.AMOUNT ";

         Map<String, Object> namedParameters = new HashMap<String, Object>();
         namedParameters.put("accountIds", accountIds);
         namedParameters.put("checkStatusIds", checkStatusIds);
         List<CheckDto> checkDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new CheckDtoRowMapper());
         return checkDtos;
    }

    /**
     * Please use is for to find out check numbers, account numbers and amount not matching Stop record to make it StopNotIssued in check detail
     * @param accountIds
     * @return List of ReferenceData
     */
	@Override
	public List<Long> findAllStopNotIssuedChecks(List<Long> accountIds) {
	     logger.info("findAllStopNotIssuedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
	     String sql = "SELECT b.id FROM "
	             +" REFERENCE_DATA b "
	             +" WHERE NOT EXISTS "
	             +"		( "
	             +"    	SELECT DISTINCT a.id, a.check_number, a.account_id, a.issued_amount FROM "
	             +" 	CHECK_DETAIL a "
	             +" 	WHERE "
	             +" 	b.ACCOUNT_ID IN (:accountIds) "
	             +" 	AND "
	             +" 	b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
	             +" 	AND a.CHECK_NUMBER = b.CHECK_NUMBER "
	             +" 	AND a.ACCOUNT_ID = b.ACCOUNT_ID "
	             +"		)"
	             +" AND b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
	             +" AND b.ITEM_TYPE = '"+ ReferenceData.ITEM_TYPE.STOP +"' ";
	     
	     Map<String, Object> namedParameters = new HashMap<String, Object>();
	     namedParameters.put("accountIds", accountIds);
	     List<Long> referenceDataIds = namedParameterJdbcTemplate.queryForList(sql, namedParameters,  Long.class);
	     return referenceDataIds;
		}

    /**
     * Please use is for to find out check numbers, account numbers and amount not matching Paid record to make it PaidNotIssued in check detail
     * @param accountIds
     * @return List of ReferenceData
     */
	@Override
	public List<Long> findAllPaidNotIssuedChecks(List<Long> accountIds) {
	     logger.info("findAllPaidNotIssuedChecks via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
	     String sql = "SELECT b.id FROM "
	             +" REFERENCE_DATA b "
	             +" WHERE NOT EXISTS "
	             +"		( "
	             +"    	SELECT DISTINCT a.id, a.check_number, a.account_id, a.issued_amount FROM "
	             +" 	CHECK_DETAIL a "
	             +" 	WHERE "
	             +" 	b.ACCOUNT_ID IN (:accountIds) "
	             +" 	AND "
	             +" 	b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
	             +" 	AND a.CHECK_NUMBER = b.CHECK_NUMBER "
	             +" 	AND a.ACCOUNT_ID = b.ACCOUNT_ID "
	             +"		)"
	             +" AND b.STATUS = '"+ ReferenceData.STATUS.NOT_PROCESSED+"' "
	             +" AND b.ITEM_TYPE = '"+ ReferenceData.ITEM_TYPE.PAID +"' ";
	     
	     Map<String, Object> namedParameters = new HashMap<String, Object>();
	     namedParameters.put("accountIds", accountIds);
	     List<Long> referenceDataIds = namedParameterJdbcTemplate.queryForList(sql, namedParameters,  Long.class);
	     return referenceDataIds;
		}

	@Override
	public List<ExceptionCheckDto> findAllPaidOrStopButVoidDuplicateChecks(List<Long> checkStatusIds) {
		 logger.info("findAllPaidButVoidDuplicateChecks via jdbc Template");
		 ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
         String sql = "SELECT DISTINCT a.ID, b.ID as EXC_CHECK_ID FROM "
                 +" CHECK_DETAIL a, CHECK_DETAIL_EXCEPTION b "
                 +" WHERE "
                 /*+" b.EXCEPTION_STATUS = '"+ ExceptionalCheck.EXCEPTION_STATUS.DUPLICATE_CHECK_IN_DATABASE+"' "*/
                 +" b.EXCEPTION_TYPE_ID = '"+ exceptionType.getId()+"' "
                 +" AND a.CHECK_STATUS_ID IN (:checkStatusIds) "
                 +" AND b.CHECK_STATUS = '"+ ExceptionalCheck.CHECK_STATUS.VOID+"' "
                 +" AND a.CHECK_NUMBER = b.CHECK_NUMBER ";

         Map<String, Object> namedParameters = new HashMap<String, Object>();
         namedParameters.put("checkStatusIds", checkStatusIds);
         List<ExceptionCheckDto> exceptionCheckDto = namedParameterJdbcTemplate.query(sql, namedParameters, new ExceptionCheckDtoRowMapper());
         return exceptionCheckDto;
	}
	
	@Override
	public List<ExceptionCheckDto> findAllVoidOrStopButIssuedDuplicateChecks(List<Long> checkStatusIds) {
		 logger.info("findAllPaidButVoidDuplicateChecks via jdbc Template");
		 ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE);
		 NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
         String sql = "SELECT DISTINCT a.ID, b.ID as EXC_CHECK_ID FROM "
                 +" CHECK_DETAIL a, CHECK_DETAIL_EXCEPTION b "
                 +" WHERE "
                 /*+" b.EXCEPTION_STATUS = '"+ ExceptionalCheck.EXCEPTION_STATUS.DUPLICATE_CHECK_IN_DATABASE+"' "*/
                 +" b.EXCEPTION_TYPE_ID = '"+ exceptionType.getId()+"' "
                 +" AND a.CHECK_STATUS_ID IN (:checkStatusIds) "
                 +" AND b.CHECK_STATUS = '"+ ExceptionalCheck.CHECK_STATUS.ISSUED+"' "
                 +" AND a.CHECK_NUMBER = b.CHECK_NUMBER ";

         Map<String, Object> namedParameters = new HashMap<String, Object>();
         namedParameters.put("checkStatusIds", checkStatusIds);
         List<ExceptionCheckDto> exceptionCheckDto = namedParameterJdbcTemplate.query(sql, namedParameters, new ExceptionCheckDtoRowMapper());
         return exceptionCheckDto;
	}

    @Override
    public Map<Long, Integer> getExceptionalItemsCountOfFile(List<Long> fileMetadataIds) {
        Map<Long, Integer> returnObject = new HashMap<Long, Integer>();
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT fmd.id AS id, COUNT(cd.id) " +
                "FROM file_meta_data fmd " +
                "JOIN check_detail_exception cd " +
                "ON cd.file_meta_data_id = fmd.id AND fmd.id IN (:fileMetadataIds) " +
                "GROUP BY fmd.id";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("fileMetadataIds", fileMetadataIds);
        List<Map<String, Object>> objects = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        for(Map<String, Object> row: objects) {
            //returnObject.put(row.get)
            Object[] keys = row.keySet().toArray();
                returnObject.put((Long)row.get(keys[0]),(Integer)row.get(keys[1]));
        }
        return  returnObject;
    }

    @Override
    public Map<Long, Integer> getProcessedItemsCountOfFile(List<Long> fileMetadataIds) {
        Map<Long, Integer> returnObject = new HashMap<Long, Integer>();
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT fmd.id AS id, COUNT(cd.id) " +
                "FROM file_meta_data fmd " +
                "JOIN check_detail cd " +
                "ON cd.file_meta_data_id = fmd.id AND fmd.id IN (:fileMetadataIds) " +
                "GROUP BY fmd.id";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("fileMetadataIds", fileMetadataIds);
        List<Map<String, Object>> objects = namedParameterJdbcTemplate.queryForList(sql, namedParameters);
        for(Map<String, Object> row: objects) {
            //returnObject.put(row.get)
            Object[] keys = row.keySet().toArray();
            returnObject.put((Long)row.get(keys[0]),(Integer)row.get(keys[1]));
        }
        return  returnObject;
    }
    
    @Override
    public void insertRolePermissions(Long roleId, List<Long> selectedPermissionIdsList) {
    	logger.info("insertRolePermissions via jdbc Template");
        String sql = "INSERT INTO ROLE_PERMISSION " +
                "(ROLE_ID, PERMISSION_ID) "
                + "VALUES (?, ?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (Long permissionId : selectedPermissionIdsList) {
            parameters.add(new Object[]{
            		roleId,
            		permissionId
            });
        }
        super.batchUpdate(sql, parameters);
    }
    
	@Override
	public void deleteRolePermissions(Long roleId, List<Long> assignedPermissionIdsList) {
	    logger.info("deleteRolePermissions via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
	    String sql = "DELETE FROM "
	             +" ROLE_PERMISSION "
	             +" WHERE PERMISSION_ID IN (:selectedPermissionIds) "
	             +" AND ROLE_ID = '"+ roleId+"' ";
	     
	     Map<String, Object> namedParameters = new HashMap<String, Object>();
	     namedParameters.put("selectedPermissionIds", assignedPermissionIdsList);
	     namedParameterJdbcTemplate.update(sql, namedParameters);
		}
	
	@Override
	public void insertUserPermissions(Long userDetailId, Set<Long> permissionIdsList) {
		if(permissionIdsList == null || permissionIdsList.size() == 0)
			return;
		logger.info("insertUserPermissions via jdbc Template");
        String sql = "INSERT INTO USER_DETAIL_PERMISSION " +
                "(USER_DETAIL_ID, PERMISSION_ID) "
                + "VALUES (?, ?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (Long permissionId : permissionIdsList) {
            parameters.add(new Object[]{
            		userDetailId,
            		permissionId
            });
        }
        super.batchUpdate(sql, parameters);
	}

	@Override
	public void deleteUserPermissions(Long userDetailId, Set<Long> permissionIdsList) {
		if(permissionIdsList == null || permissionIdsList.size() == 0)
			return;
		logger.info("deleteUserPermissions via jdbc Template");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
	    String sql = "DELETE FROM "
	             +" USER_DETAIL_PERMISSION "
	             +" WHERE PERMISSION_ID IN (:permissionIdsList) "
	             +" AND USER_DETAIL_ID = :userDetailId";
	     
	     Map<String, Object> namedParameters = new HashMap<String, Object>();
	     namedParameters.put("permissionIdsList", permissionIdsList);
	     namedParameters.put("userDetailId", userDetailId);
	     namedParameterJdbcTemplate.update(sql, namedParameters);
		
	}
	
	@Override
	public void updateUserBaseRole(Long userDetailId, Long roleId) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		String sql = "Update USER_DETAIL set ROLE_ID = :roleId where ID = :userDetailId";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
	     namedParameters.put("roleId", roleId);
	     namedParameters.put("userDetailId", userDetailId);
	     namedParameterJdbcTemplate.update(sql, namedParameters);
	}
	
	@Override
	public List<ManageUserDto> findUserBySearchCriteria(FindUserDto criteria) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		StringBuilder sql = new StringBuilder("select distinct user_detail.id as USERID, user_detail.username as USERNAME , user_detail.email_address as EMAIL,"
				+ " role.label as ROLE, user_detail.is_active as ACTIVE, bank.id as BANKID, company.id as COMPANYID"+ 
						" from user_detail " +
						" left join role on role.id = user_detail.role_id " +
						" left join user_detail_account on user_detail_account.user_detail_id = user_detail.id " +
						" left join account on account.id = user_detail_account.account_id " +
						" left join company on company.id = account.company_id " +
						" left join bank on bank.id = company.bank_id " +
						" where 1=1");
		
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(criteria.getUsername())) {
			sql.append("and (user_detail.username =  :username or user_detail.first_name =  :username or user_detail.last_name =  :username) ");
			namedParameters.put("username", criteria.getUsername());
		}
		if(criteria.getAccountNo() != null) {
			sql.append("and account.number = :accountNo ");
			namedParameters.put("accountNo", criteria.getAccountNo());
		}
		if(criteria.getBankId() != null || criteria.getAllowedBankIds() != null) {
			if(criteria.getBankId() != null) {
				sql.append("and bank.id = :bankId " );
				namedParameters.put("bankId", criteria.getBankId());
			}else{
				sql.append("and bank.id IN (:bankIds) " );
				namedParameters.put("bankIds", criteria.getAllowedBankIds());
			}

		}
		if(criteria.getCompanyId() != null || criteria.getAllowedCompanyIds() != null) {
			if(criteria.getCompanyId() != null) {
				sql.append("and company.id = :companyId ");
				namedParameters.put("companyId", criteria.getCompanyId());
			}else{
				sql.append("and company.id IN (:companyIds) ");
				namedParameters.put("companyIds", criteria.getAllowedCompanyIds());
			}
		}
		if(criteria.getArchivedUser() != null && criteria.getArchivedUser() == true) {
			sql.append("and user_detail.is_active = '0' ");
		}
		
		//sql.append(" order by USERNAME asc");
		
		List<ManageUserDto> result = namedParameterJdbcTemplate.query(sql.toString(), namedParameters, new RowMapper<ManageUserDto>() {

			@Override
			public ManageUserDto mapRow(ResultSet resultSet, int arg1)
					throws SQLException {
				ManageUserDto row = new ManageUserDto();
				row.setUserId(resultSet.getLong("USERID"));
				row.setUsername(resultSet.getString("USERNAME"));
				row.setEmail(resultSet.getString("EMAIL"));
				row.setRole(resultSet.getString("ROLE"));
				row.setActive(resultSet.getBoolean("ACTIVE"));
				row.setBankId(resultSet.getString("BANKID") == null ? null : resultSet.getLong("BANKID")); //check for null value as default getLong retruns 0 for Null
				row.setCompanyId(resultSet.getString("COMPANYID") == null ? null :resultSet.getLong("COMPANYID"));
				return row;
			}
		});
		
		return result;
	}
	
	@Override
	public void updateUserStatusAndInstitutionId(List<ManageUserDto> manageUsers) {
		String modifiedBy = SecurityUtility.getPrincipal();
		Date modifiedDate = new Date(System.currentTimeMillis());
        
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		String sql = "update USER_DETAIL set IS_ACTIVE = :active , BOTTOMLINE_INSTITUTION_ID = (select COALESCE(ASSIGNED_BANK_NUMBER,'0') from BANK where ID=:bankId),"
				+ " DATE_MODIFIED =:modifiedDate , MODIFIED_BY =:modifiedBy "
				+ " where ID =:userDetailId "; 
		MapSqlParameterSource[] paramMap = new MapSqlParameterSource[manageUsers.size()];
		int i=0;
		for(ManageUserDto manageUser : manageUsers) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("active", manageUser.getActive());
			param.put("bankId", manageUser.getBankId());
			param.put("userDetailId", manageUser.getUserId());
			param.put("modifiedDate", modifiedDate);
			param.put("modifiedBy", modifiedBy);
			paramMap[i]  = new MapSqlParameterSource(param);
			i++;
		}
		jdbcTemplate.batchUpdate(sql, paramMap);
	}
	
	@Override
	public void assignUsersToCompany(Map<Long, Set<Long>> companyUsersMap) {
		
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		
		Set<Long> userIds = new HashSet<Long>();
		for(Long companyId : companyUsersMap.keySet()) {
			userIds.addAll(companyUsersMap.get(companyId));
		}
		
		String sql = "select ID, COMPANY_ID from ACCOUNT where COMPANY_ID in (:companyIds)";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, Collections.singletonMap("companyIds", companyUsersMap.keySet()));
		//Construct Map of UserId and Account Id which needs to be inserted
		List<Object[]> userAccountParameters = new ArrayList<Object[]>();
		for(Map<String, Object> map : results) {
			Long companyId = (Long)map.get("COMPANY_ID");
			Long accountId = (Long)map.get("ID");
			Set<Long> companyUsers = companyUsersMap.get(companyId);
			for(Long userId : companyUsers) {
				userAccountParameters.add(new Object[]{userId, accountId});
			}
		}
		
		//Delete all records in USER_DETAIL_ACCOUNT table
		String removeUserAccountsSQL = "delete from USER_DETAIL_ACCOUNT where USER_DETAIL_ID in (:userIds)";
		jdbcTemplate.update(removeUserAccountsSQL, Collections.singletonMap("userIds", userIds));
		
		//Insert records in USER_DETAIL_ACCOUNT
		String insertUserAccounts = "insert into USER_DETAIL_ACCOUNT(USER_DETAIL_ID , ACCOUNT_ID) values(?,?)";
		super.batchUpdate(insertUserAccounts, userAccountParameters);
		
	}


    @Override
    public List<AccountInfoDto> getCustomerAccountInfo(List<Long> accountIdList) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT count(cd.id) as exceptionCount, acc.number as accountNumber, acc.id as accountId, acc.name as accountName, b.name as bankName,b.id as bankId,  b.routing_number as routingNumber,acct.name as accountType " +
                "FROM CHECK_DETAIL cd INNER JOIN ACCOUNT acc ON cd.account_id = acc.id " +
                "  INNER JOIN CHECK_STATUS cs ON cd.check_status_id = cs.id " +
                "  INNER JOIN BANK b  ON acc.bank_id = b.id " +
                "  LEFT JOIN ACCOUNT_TYPE acct ON acc.account_type_id = acct.id " +
                "WHERE cs.is_in_exception = 1 and acc.id in (:accountIdList) " +
                "GROUP BY acc.number,acc.name,acc.id, b.name, b.routing_number, acct.name, b.id,b.routing_number  ";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIdList", accountIdList);
        return namedParameterJdbcTemplate.query(sql.toString(), namedParameters, new RowMapper<AccountInfoDto>() {
            @Override
            public AccountInfoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                AccountInfoDto accountInfoDto = new AccountInfoDto();
                accountInfoDto.setAccountNumber(rs.getString("accountNumber"));
                accountInfoDto.setAccountName(rs.getString("accountName"));
                accountInfoDto.setAccountId(rs.getLong("accountId"));
                String accountType = rs.getString("accountType");
                accountInfoDto.setAccountType(accountType == null?"N/A":accountType);
                accountInfoDto.setBankName(rs.getString("bankName"));
                accountInfoDto.setExceptions(rs.getInt("exceptionCount"));
                accountInfoDto.setBankId(rs.getLong("bankId"));
                accountInfoDto.setBankRoutingNumber(rs.getString("routingNumber"));
                return accountInfoDto;
            }
        });
    }

    @Override
    public List<Long> getAllAccountsIds() {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT acc.id from ACCOUNT acc";
        List<Long> accountIds = namedParameterJdbcTemplate.queryForList(sql, Collections.EMPTY_MAP, Long.class);
        return accountIds;
    }

    @Override
    public List<AccountPaymentInfoDto> getAllPaymentsAndCountGroupedByCheckStatus(List<String> accountNumbers, List<String> checkStatus) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT sum(cd.issued_amount) AS totalAmount, count(cd.id) as totalCount, cs.name AS checkStatusName, cs.description as checkStatusDescription, " +
                "  acc.id AS accountId, acc.number as accountNumber " +
                "FROM CHECK_DETAIL cd INNER JOIN ACCOUNT acc ON cd.account_id = acc.id " +
                "  INNER JOIN CHECK_STATUS cs ON cd.check_status_id = cs.id " +
                "  WHERE acc.number IN (:accountNumbers) AND " +
                " cs.name IN (:checkStatus) " +
                " GROUP BY cs.name, acc.id, acc.number, cs.description " ;
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountNumbers", accountNumbers);
        namedParameters.put("checkStatus", checkStatus);
        return namedParameterJdbcTemplate.query(sql.toString(), namedParameters, new RowMapper<AccountPaymentInfoDto>() {
            @Override
            public AccountPaymentInfoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                AccountPaymentInfoDto accountPaymentInfoDto = new AccountPaymentInfoDto();
                accountPaymentInfoDto.setAccountId(rs.getLong("accountId"));
                accountPaymentInfoDto.setAccountNumber(rs.getString("accountNumber"));
                accountPaymentInfoDto.setTotalCount(rs.getInt("totalCount"));
                accountPaymentInfoDto.setCheckStatusName(rs.getString("checkStatusName"));
                accountPaymentInfoDto.setCheckStatusDescription(rs.getString("checkStatusDescription"));
                accountPaymentInfoDto.setTotalAmount(rs.getDouble("totalAmount"));
                return accountPaymentInfoDto;
            }
        });
    }

    @Override
    public List<CheckStatusDto> getDisplayableCheckStatuses() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        List<CheckStatusDto> checkStatusDtos = new ArrayList<CheckStatusDto>();
        String sql = "SELECT " +
                "  DISTINCT (cs.name) as name , " +
                "  cs.description as description , " +
                "  cs.version as version, " +
                " cs.id as id " +
                "FROM CHECK_DETAIL cd INNER JOIN CHECK_STATUS cs " +
                "    ON cd.check_status_id = cs.id " +
                "WHERE cs.is_in_exception = 0 and cs.name != 'start'";
        checkStatusDtos = jdbcTemplate.query(sql, new RowMapper<CheckStatusDto>() {
            @Override
            public CheckStatusDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                CheckStatusDto checkStatusDto = new CheckStatusDto();
                checkStatusDto.setId(rs.getLong("id"));
                checkStatusDto.setDescription(rs.getString("description"));
                checkStatusDto.setName(rs.getString("name"));
                checkStatusDto.setVersion(rs.getInt("version"));
                return checkStatusDto;
            }
        });
        return checkStatusDtos;
    }
    
    @Override
    public List<UserDetailFilterSearchValue> insertUserDetailFilterSearchValueList(List<UserDetailFilterSearchValue> UserDetailFilterSearchValueList) {
    	String sql = "INSERT INTO USER_DETAIL_FILTER_SEARCH_VALUES " +
                "(USER_DETAIL_DEFINED_FILTER_ID, SEARCH_PARAMETER_ID, PARAM_SEQUENCE, PARAMETER_VALUE, RELATIONAL_OPERATOR) "
                + "VALUES (?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (UserDetailFilterSearchValue searchValue : UserDetailFilterSearchValueList) {
            parameters.add(new Object[]{
            		searchValue.getUserDetailDefinedFilter().getId(),
            		searchValue.getSearchParameter().getId(),
            		searchValue.getParamSequence(),
            		searchValue.getParameterValue(),
            		searchValue.getRelationalOperator()
            });
        }
        super.batchUpdate(sql, parameters);
        return UserDetailFilterSearchValueList;
    }
    
    @Override
    public List<PaymentDetailDto> findAllPayments(Map<String, SearchParameterDto> searchParametersMap) {    	
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder = new StringBuilder("SELECT top "+maxRecords+" * FROM "+paymentsView+" WHERE 1=1");
        int paramNo = 0;
        for (Map.Entry<String, SearchParameterDto> entry : searchParametersMap.entrySet()) {
           SearchParameterDto searchParameterDto = entry.getValue();
            String relationalOperator = searchParameterDto.getRelationalOperator();            
            RelationalOperator relop = RelationalOperator.getOperatorByDescription(relationalOperator);
            String clause = "";
            
            switch(relop) {
                            case EQUALS : clause = "=";
                            	break;
                            case CONTAINS : clause = "in";
                                break;
                            case DOES_NOT_CONTAIN : clause = "not in";
                                break;
                            case IS_ONE_OF : clause = "in";
                                break;
                            case IS_NOT_ONE_OF : clause = "not in";
                                break;
                            case IS_GREATER_THAN : clause = ">";
                                break;
                            case IS_LESS_THAN : clause = "<";
                                break;
                            case IS_NOT_GREATER_THAN : clause = "!>";
                                break;
                            case IS_NOT_LESS_THAN : clause = "!<";
                                break;
                            case IS_AFTER : clause = ">";
                                break;
                            case IS_BEFORE : clause = "<";
                                break;
                            case IS_NOT_AFTER : clause = "!>";
                                break;
                            case IS_NOT_BEFORE : clause = "!<";
                                break;
                            case IS_BETWEEN : clause = "between";
                            			break;
                            default:
                            	break;            
            }
            
           if(entry.getKey().equalsIgnoreCase("Bank") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" bank_name "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
           }
           if(entry.getKey().equalsIgnoreCase("Customer") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" company_name "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") "); 
           }
           if(entry.getKey().equalsIgnoreCase("Account Number") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" account_number "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
           }
           if(entry.getKey().equalsIgnoreCase("Check Number")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
	              sqlBuilder.append(" check_number "+clause +"  :check_number ");
	              namedParameters.put("check_number", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" check_number "+clause +"  :check_numberFrom And :check_numberTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("check_numberFrom", fromAndToList[0]);
            	  namedParameters.put("check_numberTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Payment Status") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" payment_status "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
           }
           if(entry.getKey().equalsIgnoreCase("Payment Amount")) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" paid_amount "+clause +"  :paid_amount ");
              namedParameters.put("paid_amount", searchParameterDto.getParameterCsv());
           }
           if(entry.getKey().equalsIgnoreCase("Payment Date")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" paid_date "+clause +"  :paid_date ");
                  namedParameters.put("paid_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" paid_date "+clause +"  :paid_dateFrom And :paid_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("paid_dateFrom", fromAndToList[0]);
            	  namedParameters.put("paid_dateTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Match Status") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" match_status "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");              
           }
           if(entry.getKey().equalsIgnoreCase("Exception Type") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" exception_type_label "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
           }
           if(entry.getKey().equalsIgnoreCase("Exception Status") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" exception_status "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
           }
           if(entry.getKey().equalsIgnoreCase("Exception Create Date")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" (exception_type_creation_date "+clause +"  :exception_create_date ");
            	  sqlBuilder.append(" OR exception_type_resolved_date "+clause +"  :exception_resolved_date) ");
            	  
                  namedParameters.put("exception_create_date", searchParameterDto.getParameterCsv());
                  namedParameters.put("exception_resolved_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" (exception_type_creation_date "+clause +"  :exception_create_dateFrom And :exception_create_dateTo");
            	  sqlBuilder.append(" OR exception_type_resolved_date "+clause +"  :exception_resolved_dateFrom And :exception_resolved_dateTo) ");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("exception_create_dateFrom", fromAndToList[0]);
            	  namedParameters.put("exception_create_dateTo", fromAndToList[1]);
            	  namedParameters.put("exception_resolved_dateFrom", fromAndToList[0]);
            	  namedParameters.put("exception_resolved_dateTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Resolution Action") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
              sqlBuilder.append(" AND ");
              sqlBuilder.append(" resolution_action "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
           }
           if(entry.getKey().equalsIgnoreCase("Issued Amount")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" issued_amount "+clause +"  :issued_amount ");
                  namedParameters.put("issued_amount", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" issued_amount "+clause +"  :issued_amountFrom And :issued_amountTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("issued_amountFrom", fromAndToList[0]);
            	  namedParameters.put("issued_amountTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Issued Date")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" issued_date "+clause +"  :issue_date ");
                  namedParameters.put("issue_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" issued_date "+clause +"  :issued_dateFrom And :issued_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("issued_dateFrom", fromAndToList[0]);
            	  namedParameters.put("issued_dateTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Paid Amount")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" paid_amount "+clause +"  :paid_amount ");
                  namedParameters.put("paid_amount", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" paid_amount "+clause +"  :paid_amountFrom And :paid_amountTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("paid_amountFrom", fromAndToList[0]);
            	  namedParameters.put("paid_amountTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Paid Date")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" paid_date "+clause +"  :paid_date ");
                  namedParameters.put("paid_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" paid_date "+clause +"  :paid_dateFrom And :paid_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("paid_dateFrom", fromAndToList[0]);
            	  namedParameters.put("paid_dateTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Stop Date")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" stop_date "+clause +"  :stop_date ");
                  namedParameters.put("stop_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" stop_date "+clause +"  :stop_dateFrom And :stop_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("stop_dateFrom", fromAndToList[0]);
            	  namedParameters.put("stop_dateTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Stop Expiration Date")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" expiration_date "+clause +"  :expiration_date ");
                  namedParameters.put("expiration_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" expiration_date "+clause +"  :expiration_dateFrom And :expiration_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("expiration_dateFrom", fromAndToList[0]);
            	  namedParameters.put("expiration_dateTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Stop Amount")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" stop_amount "+clause +"  :stop_amount ");
                  namedParameters.put("stop_amount", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" stop_amount "+clause +"  :stop_amountFrom And :stop_amountTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("stop_amountFrom", fromAndToList[0]);
            	  namedParameters.put("stop_amountTo", fromAndToList[1]);
              }
           }
           
           if(entry.getKey().equalsIgnoreCase("Void Date")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" void_date "+clause +"  :void_date ");
                  namedParameters.put("void_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" void_date "+clause +"  :void_dateFrom And :void_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("void_dateFrom", fromAndToList[0]);
            	  namedParameters.put("void_dateTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Void Amount")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" void_amount "+clause +"  :void_amount ");
                  namedParameters.put("void_amount", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" void_amount "+clause +"  :void_amountFrom And :void_amountTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("void_amountFrom", fromAndToList[0]);
            	  namedParameters.put("void_amountTo", fromAndToList[1]);
              }
           }
           if(entry.getKey().equalsIgnoreCase("Trace Number")) {
              sqlBuilder.append(" AND ");
              if(relop != RelationalOperator.IS_BETWEEN) {
            	  sqlBuilder.append(" trace_number "+clause +"  :trace_number ");
                  namedParameters.put("trace_number", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" trace_number "+clause +"  :trace_numberFrom And :trace_numberTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("trace_numberFrom", fromAndToList[0]);
            	  namedParameters.put("trace_numberTo", fromAndToList[1]);
              }
           }
           /* if(entry.getKey().equalsIgnoreCase("Reference Number")) {
                  sqlBuilder.append(" AND ");
                  sqlBuilder.append(" reference_number "+clause +"  :reference_number ");
                  namedParameters.put("reference_number", searchParameterDto.getParameterCsv());
           }*/
        }
        logger.info("total params set ="+paramNo);
        logger.info("findAllPayments dynamic sql = "+sqlBuilder.toString());
        List<PaymentDetailDto> PaymentDetailDtoList = namedParameterJdbcTemplate.query(sqlBuilder.toString().trim(), namedParameters, new RowMapper<PaymentDetailDto>() {
               @Override
             public PaymentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                      PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
                      paymentDetailDto.setAccountNumber(rs.getString("account_number"));
                      paymentDetailDto.setAccountName(rs.getString("account_name"));
                      paymentDetailDto.setCheckId(rs.getLong("check_detail_id"));
                      paymentDetailDto.setCheckNumber(rs.getString("check_number"));
                      paymentDetailDto.setPaymentStatus(rs.getString("payment_status"));
                      paymentDetailDto.setMatchStatus(rs.getString("match_status"));
                      paymentDetailDto.setExceptionType(rs.getString("exception_type_label"));
                      paymentDetailDto.setExceptionStatus(rs.getString("exception_status"));
                      paymentDetailDto.setIssuedAmount(rs.getBigDecimal("issued_amount"));
                      paymentDetailDto.setIssuedDate(rs.getDate("issued_date"));
                      paymentDetailDto.setPaidAmount(rs.getBigDecimal("paid_amount"));
                      paymentDetailDto.setPaidDate(rs.getDate("paid_date"));
                      paymentDetailDto.setStopDate(rs.getDate("stop_date"));
                      paymentDetailDto.setVoidDate(rs.getDate("void_date"));
                      paymentDetailDto.setWorkflowId(rs.getLong("workflow_id"));
                      paymentDetailDto.setTraceNumber(rs.getString("trace_number"));
                 return paymentDetailDto;
             }
         });
         return PaymentDetailDtoList;
    }
    
    private String[] getListFromCSV(String parameterCsv) {
    	return parameterCsv.split(",");
	}

	@Override
    public List<PaymentDetailDto> findAllItems(Map<String, SearchParameterDto> searchParametersMap) {
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    	Map<String, Object> namedParameters = new HashMap<String, Object>();
    	StringBuilder sqlBuilder = new StringBuilder();
    	
    	sqlBuilder = new StringBuilder("SELECT top "+maxRecords+" * FROM "+itemsView+" WHERE 1=1");
    	int paramNo = 0;
    	for (Map.Entry<String, SearchParameterDto> entry : searchParametersMap.entrySet()) {
    		
    		SearchParameterDto searchParameterDto = entry.getValue();
            String relationalOperator = searchParameterDto.getRelationalOperator();
            
            RelationalOperator relop = RelationalOperator.getOperatorByDescription(relationalOperator);
            String clause = "";
            
            switch(relop) {
            			case EQUALS : clause = "=";
            				break;
            			case CONTAINS : clause = "in";
            				break;
            			case DOES_NOT_CONTAIN : clause = "not in";
            				break;
            			case IS_ONE_OF : clause = "in";
 							break;
            			case IS_NOT_ONE_OF : clause = "not in";
 							break;
            			case IS_GREATER_THAN : clause = ">";
 							break;
            			case IS_LESS_THAN : clause = "<";
 							break;
            			case IS_NOT_GREATER_THAN : clause = "!>";
 							break;
            			case IS_NOT_LESS_THAN : clause = "!<";
 							break;
            			case IS_AFTER : clause = ">";
 							break;
            			case IS_BEFORE : clause = "<";
            				break;
            			case IS_NOT_AFTER : clause = "!>";
            				break;
            			case IS_NOT_BEFORE : clause = "!<";
            				break;
                        case IS_BETWEEN : clause = "between";
            				break;
        				default:
        					break;
            
            }
    		
        if(entry.getKey().equalsIgnoreCase("Bank") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
            sqlBuilder.append(" AND ");
            sqlBuilder.append(" bank_name "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
         }
         if(entry.getKey().equalsIgnoreCase("Customer") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
            sqlBuilder.append(" AND ");
            sqlBuilder.append(" company_name "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") "); 
         }
         if(entry.getKey().equalsIgnoreCase("Account Number") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
            sqlBuilder.append(" AND ");
            sqlBuilder.append(" account_number "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
         }
         if(entry.getKey().equalsIgnoreCase("Check Number")) {
            sqlBuilder.append(" AND ");
            if(relop != RelationalOperator.IS_BETWEEN) {
              sqlBuilder.append(" check_number "+clause +"  :check_number ");
              namedParameters.put("check_number", searchParameterDto.getParameterCsv());
            }else {
          	  sqlBuilder.append(" check_number "+clause +"  :check_numberFrom And :check_numberTo");
          	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
          	  namedParameters.put("check_numberFrom", fromAndToList[0]);
          	  namedParameters.put("check_numberTo", fromAndToList[1]);
            }
         }
         if(entry.getKey().equalsIgnoreCase("Item Type") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
            sqlBuilder.append(" AND ");
            sqlBuilder.append(" item_type "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") ");
         }
         if(entry.getKey().equalsIgnoreCase("Item Amount")) {
            sqlBuilder.append(" AND ");
            if(relop != RelationalOperator.IS_BETWEEN) {
                sqlBuilder.append(" item_amount "+clause +"  :item_amount ");
                namedParameters.put("item_amount", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" item_amount "+clause +"  :item_amountFrom And :item_amountTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("item_amountFrom", fromAndToList[0]);
            	  namedParameters.put("item_amountTo", fromAndToList[1]);
              }
         }
         if(entry.getKey().equalsIgnoreCase("Item Date")) {
            sqlBuilder.append(" AND ");
            if(relop != RelationalOperator.IS_BETWEEN) {
                sqlBuilder.append(" item_date "+clause +"  :item_date ");
                namedParameters.put("item_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" item_date "+clause +"  :item_dateFrom And :item_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("item_dateFrom", fromAndToList[0]);
            	  namedParameters.put("item_dateTo", fromAndToList[1]);
              }
         }
         if(entry.getKey().equalsIgnoreCase("Created By") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
        	 sqlBuilder.append(" AND ");
             sqlBuilder.append(" created_by "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") "); 
         }
         if(entry.getKey().equalsIgnoreCase("Created Date")) {
            sqlBuilder.append(" AND ");
            if(relop != RelationalOperator.IS_BETWEEN) {
                sqlBuilder.append(" created_date "+clause +"  :created_date ");
                namedParameters.put("created_date", searchParameterDto.getParameterCsv());
              }else {
            	  sqlBuilder.append(" created_date "+clause +"  :created_dateFrom And :created_dateTo");
            	  String[] fromAndToList = this.getListFromCSV(searchParameterDto.getParameterCsv());
            	  namedParameters.put("created_dateFrom", fromAndToList[0]);
            	  namedParameters.put("created_dateTo", fromAndToList[1]);
              }               
         }
         if(entry.getKey().equalsIgnoreCase("Created Method") && org.springframework.util.StringUtils.hasText(searchParameterDto.getParameterCsv())) {
        	 sqlBuilder.append(" AND ");
             sqlBuilder.append(" creation_method "+clause +" ( "+ PPUtils.addQuotesToCSV(searchParameterDto.getParameterCsv()) +") "); 
         }
    	}
    	logger.info("total params set ="+paramNo);
    	logger.info("findAllItems dynamic sql = "+sqlBuilder.toString());
    	List<PaymentDetailDto> PaymentDetailDtoList = namedParameterJdbcTemplate.query(sqlBuilder.toString().trim(), namedParameters, new RowMapper<PaymentDetailDto>() {
        	@Override
            public PaymentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        		PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
        		paymentDetailDto.setAccountNumber(rs.getString("account_number"));
        		paymentDetailDto.setAccountName(rs.getString("account_name"));
        		paymentDetailDto.setCheckId(rs.getLong("check_detail_id"));
        		paymentDetailDto.setCheckNumber(rs.getString("check_number"));
        		paymentDetailDto.setBankName(rs.getString("bank_name"));
        		paymentDetailDto.setItemType(rs.getString("item_type"));
        		paymentDetailDto.setItemAmount(rs.getBigDecimal("item_amount"));
        		paymentDetailDto.setItemDate(rs.getDate("item_date"));
        		paymentDetailDto.setCreatedBy(rs.getString("created_by"));
        		paymentDetailDto.setCreatedMethod(rs.getString("creation_method"));
        		paymentDetailDto.setTraceNumber(rs.getString("trace_number"));
                return paymentDetailDto;
            }
        });
        return PaymentDetailDtoList;
    }
	
	 @Override
	    public PaymentDetailDto findItemDetails(String checkNumber, String accountNumber, String itemType){
	    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
	    	Map<String, Object> namedParameters = new HashMap<String, Object>();
	    	//based on the params available in the map, make a dynamic query
	    	String sql = "SELECT top "+maxRecords+" * FROM "+itemsView+" WHERE check_number = (:checkNumber) and account_number =  (:accountNumber) and item_type =  (:itemType)";
	    	namedParameters.put("checkNumber", checkNumber);
	    	namedParameters.put("accountNumber", accountNumber);
	    	namedParameters.put("itemType", itemType);
	    	List<PaymentDetailDto> paymentDetailDtoList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<PaymentDetailDto>() {
	        	@Override
	            public PaymentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
	        		PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
	        		paymentDetailDto.setCheckNumber(rs.getString("check_number"));
	        		paymentDetailDto.setPaymentStatus(rs.getString("payment_status"));
	        		paymentDetailDto.setItemAmount(rs.getBigDecimal("item_amount"));
	        		paymentDetailDto.setItemDate(rs.getDate("item_date"));
	        		paymentDetailDto.setCreatedDate(rs.getDate("created_date"));
	        		paymentDetailDto.setCreatedBy(rs.getString("created_by"));
	        		paymentDetailDto.setCreatedMethod(rs.getString("creation_method"));
	        		paymentDetailDto.setItemCode(rs.getString("item_code"));
	        		paymentDetailDto.setAccountNumber(rs.getString("account_number"));
	        		paymentDetailDto.setBankName(rs.getString("bank_name"));
	        		paymentDetailDto.setCompany(rs.getString("company_name"));
	        		paymentDetailDto.setPayee(rs.getString("payee"));
	        		paymentDetailDto.setTraceNumber(rs.getString("trace_number"));
	        		paymentDetailDto.setMatchStatus(rs.getString("match_status"));
	                return paymentDetailDto;
	            }
	        });
	        return paymentDetailDtoList.get(0);
	    }
	 
	 	@Override
	    public PaymentDetailDto findItemDetailsByTraceNumber(String traceNumber){
	    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
	    	Map<String, Object> namedParameters = new HashMap<String, Object>();
	    	//based on the params available in the map, make a dynamic query
	    	String sql = "SELECT top "+maxRecords+" * FROM "+itemsView+" WHERE trace_number = (:traceNumber)";
	    	namedParameters.put("traceNumber", traceNumber);
	    	List<PaymentDetailDto> paymentDetailDtoList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<PaymentDetailDto>() {
	        	@Override
	            public PaymentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
	        		PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
	        		paymentDetailDto.setCheckNumber(rs.getString("check_number"));
	        		paymentDetailDto.setPaymentStatus(rs.getString("payment_status"));
	        		paymentDetailDto.setItemAmount(rs.getBigDecimal("item_amount"));
	        		paymentDetailDto.setItemDate(rs.getDate("item_date"));
	        		paymentDetailDto.setCreatedDate(rs.getDate("created_date"));
	        		paymentDetailDto.setCreatedBy(rs.getString("created_by"));
	        		paymentDetailDto.setCreatedMethod(rs.getString("creation_method"));
	        		paymentDetailDto.setItemCode(rs.getString("item_code"));
	        		paymentDetailDto.setAccountNumber(rs.getString("account_number"));
	        		paymentDetailDto.setBankName(rs.getString("bank_name"));
	        		paymentDetailDto.setCompany(rs.getString("company_name"));
	        		paymentDetailDto.setPayee(rs.getString("payee"));
	        		paymentDetailDto.setTraceNumber(rs.getString("trace_number"));
	        		paymentDetailDto.setMatchStatus(rs.getString("match_status"));
	                return paymentDetailDto;
	            }
	        });
	        return paymentDetailDtoList.get(0);
	    }
    
    @Override
    public CheckDto findCheckById(Long checkId){
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    	Map<String, Object> namedParameters = new HashMap<String, Object>();
    	//based on the params available in the map, make a dynamic query
    	String sql = "SELECT top "+maxRecords+" * FROM "+paymentsView+" WHERE CHECK_DETAIL_ID = (:checkId)";
    	namedParameters.put("checkId", checkId);
    	CheckDto checkDto = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<CheckDto>() {
        	@Override
            public CheckDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        		CheckDto checkDto = new CheckDto();
        		checkDto.setCheckNumber(rs.getString("check_number"));
        		checkDto.setAccountNumber(rs.getString("account_number"));
        		checkDto.setAccountName(rs.getString("account_name"));
        		checkDto.setBankName(rs.getString("bank_name"));
        		checkDto.setBankNumber(rs.getString("bank_number"));
        		checkDto.setCompanyId(rs.getLong("company_id"));
        		checkDto.setCompanyName(rs.getString("company_name"));
        		checkDto.setPaymentStatus(rs.getString("payment_status"));
        		checkDto.setMatchStatus(rs.getString("match_status"));
        		checkDto.setExceptionType(rs.getString("exception_type_label"));
        		checkDto.setExceptionStatus(rs.getString("exception_status"));
        		checkDto.setPayee(rs.getString("payee"));
        		checkDto.setIssuedAmount(rs.getBigDecimal("issued_amount"));
        		checkDto.setIssueDate(rs.getDate("issued_date"));
        		checkDto.setPaidAmount(rs.getBigDecimal("paid_amount"));
        		checkDto.setPaidDate(rs.getDate("paid_date"));
        		checkDto.setStopDate(rs.getDate("stop_date"));
        		checkDto.setVoidDate(rs.getDate("void_date"));
        		checkDto.setTraceNumber(rs.getString("trace_number"));
        		checkDto.setWorkflowId(rs.getLong("workflow_id"));
                return checkDto;
            }
        });
        return checkDto;
    }
    
    @Override
    public CheckDto findCheckByTraceNumber(String traceNumber) {
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    	Map<String, Object> namedParameters = new HashMap<String, Object>();
    	//based on the params available in the map, make a dynamic query
    	String sql = "SELECT top "+maxRecords+" * FROM "+paymentsView+" WHERE trace_number = (:traceNumber)";
    	namedParameters.put("traceNumber", traceNumber);
    	List<CheckDto> checkDtoList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<CheckDto>() {
        	@Override
            public CheckDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        		CheckDto checkDto = new CheckDto();
        		checkDto.setCheckNumber(rs.getString("check_number"));
        		checkDto.setAccountNumber(rs.getString("account_number"));
        		checkDto.setAccountName(rs.getString("account_name"));
        		checkDto.setBankName(rs.getString("bank_name"));
        		checkDto.setBankNumber(rs.getString("bank_number"));
        		checkDto.setCompanyId(rs.getLong("company_id"));
        		checkDto.setCompanyName(rs.getString("company_name"));
        		checkDto.setPaymentStatus(rs.getString("payment_status"));
        		checkDto.setMatchStatus(rs.getString("match_status"));
        		checkDto.setExceptionType(rs.getString("exception_type_label"));
        		checkDto.setExceptionStatus(rs.getString("exception_status"));
        		checkDto.setPayee(rs.getString("payee"));
        		checkDto.setIssuedAmount(rs.getBigDecimal("issued_amount"));
        		checkDto.setIssueDate(rs.getDate("issued_date"));
        		checkDto.setPaidAmount(rs.getBigDecimal("paid_amount"));
        		checkDto.setPaidDate(rs.getDate("paid_date"));
        		checkDto.setStopDate(rs.getDate("stop_date"));
        		checkDto.setVoidDate(rs.getDate("void_date"));
        		checkDto.setTraceNumber(rs.getString("trace_number"));
        		checkDto.setWorkflowId(rs.getLong("workflow_id"));
                return checkDto;
            }
        });
        return checkDtoList.get(0);
    }
    
    @Override
    public List<PaymentDetailDto> findAllPaymentsForCompany(Company userCompany) {
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    	Map<String, Object> namedParameters = new HashMap<String, Object>();
    	//based on the params available in the map, make a dynamic query
    	String sql = "SELECT top "+maxRecords+" * FROM "+paymentsView+" WHERE COMPANY_ID = (:companyId)";
    	namedParameters.put("companyId", userCompany.getId());
    	List<PaymentDetailDto> PaymentDetailDtoList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<PaymentDetailDto>() {
        	@Override
            public PaymentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        		PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
        		paymentDetailDto.setAccountNumber(rs.getString("account_number"));
        		paymentDetailDto.setAccountName(rs.getString("account_name"));
        		paymentDetailDto.setCheckId(rs.getLong("check_detail_id"));
        		paymentDetailDto.setCheckNumber(rs.getString("check_number"));
        		paymentDetailDto.setPaymentStatus(rs.getString("payment_status"));
        		paymentDetailDto.setMatchStatus(rs.getString("match_status"));
        		paymentDetailDto.setExceptionType(rs.getString("exception_type"));
        		paymentDetailDto.setExceptionStatus(rs.getString("exception_status"));
        		paymentDetailDto.setIssuedAmount(rs.getBigDecimal("issued_amount"));
        		paymentDetailDto.setIssuedDate(rs.getDate("issued_date"));
        		paymentDetailDto.setPaidAmount(rs.getBigDecimal("paid_amount"));
        		paymentDetailDto.setPaidDate(rs.getDate("paid_date"));
        		paymentDetailDto.setStopDate(rs.getDate("stop_date"));
        		paymentDetailDto.setVoidDate(rs.getDate("void_date"));
        		paymentDetailDto.setWorkflowId(rs.getLong("workflow_id"));
                return paymentDetailDto;
            }
        });
        return PaymentDetailDtoList;
    }
    
    @Override
    public List<PaymentDetailDto> findAllPaymentsByDataCriteriaAndUserCompany(DataCriteriaDto dataCriteriaDto, Company userCompany) {
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    	Map<String, Object> namedParameters = new HashMap<String, Object>();
    	StringBuilder sqlBuilder = new StringBuilder("SELECT top "+maxRecords+" * FROM "+paymentsView+" WHERE COMPANY_ID = (:companyId)");
    	namedParameters.put("companyId", userCompany.getId());
    	int paramNo = 0;
    	if(dataCriteriaDto != null) {
    		if(dataCriteriaDto.getAccountNumbers() != null && dataCriteriaDto.getAccountNumbers().size() > 0) {
    			sqlBuilder.append(" AND ");
    			sqlBuilder.append(" account_number IN (:accountNumbers) ");
    			namedParameters.put("accountNumbers", dataCriteriaDto.getAccountNumbers());
    		}
    		if(dataCriteriaDto.getPaymentStatusTypes() != null && dataCriteriaDto.getPaymentStatusTypes().size() > 0) {
    			sqlBuilder.append(" AND ");
    			sqlBuilder.append(" payment_status IN (:paymentStatusTypes) ");
    			namedParameters.put("paymentStatusTypes", dataCriteriaDto.getPaymentStatusTypes());
    		}
    		if(dataCriteriaDto.getPaidExceptionStatus() != null && dataCriteriaDto.getPaidExceptionStatus().size() > 0) {
    			sqlBuilder.append(" AND ");
    			sqlBuilder.append(" exception_status IN (:paidExceptionStatusTypes) ");
    			namedParameters.put("paidExceptionStatusTypes", dataCriteriaDto.getPaidExceptionStatus());
    		}
    		if(dataCriteriaDto.getFromCheckNumber() != null && dataCriteriaDto.getToCheckNumber() != null) {
    			if(dataCriteriaDto.getFromCheckNumber() < dataCriteriaDto.getToCheckNumber()) {
    				sqlBuilder.append(" AND ");
    				sqlBuilder.append(" CAST(check_number AS BIGINT) between (:fromCheckNumber) AND (:toCheckNumber) ");
    				namedParameters.put("fromCheckNumber", dataCriteriaDto.getFromCheckNumber());
    				namedParameters.put("toCheckNumber", dataCriteriaDto.getToCheckNumber());
    			} else if(dataCriteriaDto.getFromCheckNumber().equals(dataCriteriaDto.getToCheckNumber())) {
    				sqlBuilder.append(" AND ");
    				sqlBuilder.append(" check_number = :checkNumber ");
    				namedParameters.put("checkNumber", String.valueOf(dataCriteriaDto.getFromCheckNumber()));
    			}
    		}
    		if(dataCriteriaDto.getAmountType() != null && !dataCriteriaDto.getAmountType().isEmpty()) {
    			String amountType = dataCriteriaDto.getAmountType();
    			if(dataCriteriaDto.getFromAmount().compareTo(dataCriteriaDto.getToAmount()) < 0) {
    				sqlBuilder.append(" AND ");
    				sqlBuilder.append(" "+amountType.toLowerCase()+"_amount between (:fromAmount) AND (:toAmount) ");
    				namedParameters.put("fromAmount", dataCriteriaDto.getFromAmount());
    				namedParameters.put("toAmount", dataCriteriaDto.getToAmount());
    			} else if(dataCriteriaDto.getFromAmount().compareTo(dataCriteriaDto.getToAmount()) == 0) {
    				sqlBuilder.append(" AND ");
    				sqlBuilder.append(" "+amountType.toLowerCase()+"_amount = :amount ");
    				namedParameters.put("amount", dataCriteriaDto.getFromAmount());
    			}
    		}
    		if(dataCriteriaDto.getDateType() != null && !dataCriteriaDto.getDateType().isEmpty()) {
    			try {
    				String dateType = dataCriteriaDto.getDateType();
    				if(DateUtils.getDateFromString(dataCriteriaDto.getFromDate()).before(DateUtils.getDateFromString(dataCriteriaDto.getToDate()))) {
    					sqlBuilder.append(" AND ");
    					sqlBuilder.append(" "+dateType.toLowerCase()+"_date between (:fromDate) AND (:toDate) ");
    					namedParameters.put("fromDate", DateUtils.getDateFromString(dataCriteriaDto.getFromDate()));
    					namedParameters.put("toDate", DateUtils.getDateFromString(dataCriteriaDto.getToDate()));
    				}else if(DateUtils.getDateFromString(dataCriteriaDto.getFromDate()).equals(DateUtils.getDateFromString(dataCriteriaDto.getToDate()))) {
        				sqlBuilder.append(" AND ");
        				sqlBuilder.append(" "+dateType.toLowerCase()+"_date = :createDate ");
        				namedParameters.put("createDate", dataCriteriaDto.getFromDate());
        			}
    			} catch (ParseException e) {
    				logger.warn("To date {} is earlier than from date {}, so ignoring.", dataCriteriaDto.getToDate(), dataCriteriaDto.getFromDate());
    			}
    		}
    	}
    	sqlBuilder.append(" order by date_created desc ");
		logger.info("total params set ="+paramNo);
    	logger.info("findAllPayments dynamic sql = "+sqlBuilder.toString());
    	List<PaymentDetailDto> PaymentDetailDtoList = namedParameterJdbcTemplate.query(sqlBuilder.toString().trim(), namedParameters, new RowMapper<PaymentDetailDto>() {
        	@Override
            public PaymentDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        		PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
        		paymentDetailDto.setAccountNumber(rs.getString("account_number"));
        		paymentDetailDto.setAccountName(rs.getString("account_name"));
        		paymentDetailDto.setCheckId(rs.getLong("check_detail_id"));
        		paymentDetailDto.setCheckNumber(rs.getString("check_number"));
        		paymentDetailDto.setPaymentStatus(rs.getString("payment_status"));
        		paymentDetailDto.setMatchStatus(rs.getString("match_status"));
        		paymentDetailDto.setExceptionType(rs.getString("exception_type"));
        		paymentDetailDto.setExceptionStatus(rs.getString("exception_status"));
        		paymentDetailDto.setIssuedAmount(rs.getBigDecimal("issued_amount"));
        		paymentDetailDto.setIssuedDate(rs.getDate("issued_date"));
        		paymentDetailDto.setPaidAmount(rs.getBigDecimal("paid_amount"));
        		paymentDetailDto.setPaidDate(rs.getDate("paid_date"));
        		paymentDetailDto.setStopDate(rs.getDate("stop_date"));
        		paymentDetailDto.setVoidDate(rs.getDate("void_date"));
        		paymentDetailDto.setWorkflowId(rs.getLong("workflow_id"));
                return paymentDetailDto;
            }
        });
        return PaymentDetailDtoList;
    }
    
    @Override
    public List<UserDetailFilterSearchValueDto> getUserDetailFilterSearchValuesByUserDetailDefinedFilterId(Long userDetailDefinedFilterId) {
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT * " +
                " FROM" +
                " USER_DETAIL_FILTER_SEARCH_VALUES" +
                " WHERE" +
                " USER_DETAIL_DEFINED_FILTER_ID = (:userDetailDefinedFilterId)";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("userDetailDefinedFilterId", userDetailDefinedFilterId);
        List<UserDetailFilterSearchValueDto> userDetailFilterSearchValueDtoList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<UserDetailFilterSearchValueDto>() {
        	@Override
            public UserDetailFilterSearchValueDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        		UserDetailFilterSearchValueDto userDetailFilterSearchValueDto = new UserDetailFilterSearchValueDto();
        		userDetailFilterSearchValueDto.setSearchParameterId(rs.getLong("search_parameter_id"));
        		userDetailFilterSearchValueDto.setParamSequence(rs.getInt("param_sequence"));
        		userDetailFilterSearchValueDto.setParameterValue(rs.getString("parameter_value"));
        		userDetailFilterSearchValueDto.setUserDetailDefinedFilterId(rs.getLong("user_detail_defined_filter_id"));
        		//GP
        		userDetailFilterSearchValueDto.setRelationalOperator(rs.getString("relational_operator"));
        		
                return userDetailFilterSearchValueDto;
            }
        });
        return userDetailFilterSearchValueDtoList;
    }

    @Override
    public List<PaymentByDateDto> findAllPaymentsMadeBetweenDate(List<String> accountNumbers, java.util.Date fromDate, java.util.Date toDate) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        List<PaymentByDateDto> paymentByDateDtos = new ArrayList<PaymentByDateDto>();
        String sql = "SELECT sum(rd.amount) AS totalAmount, count(cd.id)   AS totalCount, rd.paid_date AS paidDate, " +
        				" acc.id as accountId, acc.number as accountNumber " +
                "FROM check_detail cd INNER JOIN check_status cs ON cd.check_status_id = cs.id " +
                	"INNER JOIN ACCOUNT acc ON cd.account_id = acc.id " +
                	"INNER JOIN reference_data rd ON cd.reference_data_id = rd.id " +
                "WHERE cs.name='paid' and rd.paid_date BETWEEN :fromDate AND :toDate " +
                	"AND acc.number IN (:accountNumbers) " +
                	"GROUP BY rd.paid_date, acc.id , acc.number ";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("fromDate", fromDate);
        namedParameters.put("toDate", toDate);
        namedParameters.put("accountNumbers", accountNumbers);
        paymentByDateDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<PaymentByDateDto>() {
            @Override
            public PaymentByDateDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaymentByDateDto paymentByDateDto = new PaymentByDateDto();
                paymentByDateDto.setAmount(rs.getLong("totalAmount"));
                paymentByDateDto.setCount(rs.getLong("totalCount"));
                paymentByDateDto.setPaymentDate(rs.getDate("paidDate"));
                paymentByDateDto.setAccountId(rs.getLong("accountId"));
                paymentByDateDto.setAccountNumber(rs.getString("accountNumber"));
                return paymentByDateDto;
            }
        });
        return paymentByDateDtos;
    }


    @Override
    public List<Long> findAllChecksInExceptionalState(List<Long> accountIds) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        List<Long> checkDetailIdList = new ArrayList<Long>();
        String sql = "SELECT " +
                "  cd.id " +
                "FROM CHECK_DETAIL cd JOIN CHECK_STATUS cs " +
                "    ON cd.check_status_id = cs.id " +
                "WHERE cs.is_in_exception = 1 AND cd.account_id IN (:accountIds); ";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("accountIds", accountIds);
        checkDetailIdList = namedParameterJdbcTemplate.queryForList(sql, namedParameters, Long.class);
        return checkDetailIdList;
    }

    @Override
    public List<DecisionWindowDto> fetchAllBankCompanyDecisionWindowMapping() {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT B.NAME AS BANKNAME, B.ID AS BANKID, C.NAME AS COMPANYNAME, C.ID AS COMPANYID, DW.START_WINDOW, DW.END_WINDOW, DW.TIME_ZONE FROM BANK B, COMPANY C, DECISION_WINDOW DW WHERE B.ID = C.BANK_ID AND C.DECISION_WINDOW_ID = DW.ID;";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        List<DecisionWindowDto> decisionWindowDtoList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<DecisionWindowDto>() {
            @Override
            public DecisionWindowDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                DecisionWindowDto decisionWindowDto = new DecisionWindowDto();
                decisionWindowDto.setBankName(rs.getString("BANKNAME"));
                decisionWindowDto.setBankId(rs.getLong("BANKID"));
                decisionWindowDto.setCompanyName(rs.getString("COMPANYNAME"));
                decisionWindowDto.setCompanyId(rs.getLong("COMPANYID"));
                decisionWindowDto.setStart(rs.getString("START_WINDOW"));
                decisionWindowDto.setEnd(rs.getString("END_WINDOW"));
                decisionWindowDto.setTimezone(rs.getString("TIME_ZONE"));
                return decisionWindowDto;
            }
        });
        return decisionWindowDtoList;
    }
    
	@Override
	public ExceptionalReferenceDataDto getExceptionalReferenceDataInfo(
			Long exceptionalReferenceDataId) {
		 NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());

	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("exReferenceDataId", exceptionalReferenceDataId);
	        String sql = "SELECT rd.id as referenceDataId,erd.id as expReferenceDataId, a.id as account_id,a.number as account_number "+
	    			"FROM REFERENCE_DATA rd, ACCOUNT a ,reference_data_exception erd "+
	    			"WHERE rd.account_id = a.id  " +
	                "AND erd.check_number = rd.check_number "+
	                "AND erd.account_number = a.number "+
	    			"AND erd.id = :exceptionalReferenceDataId";
	        Map<String, Object> namedParameters = new HashMap<String, Object>();
	        namedParameters.put("exceptionalReferenceDataId", exceptionalReferenceDataId);
	        List<ExceptionalReferenceDataDto> expReferenceDataDto = namedParameterJdbcTemplate.query(sql, namedParameters, new ExceptionalReferenceDataRowMapper());
	        return expReferenceDataDto.get(0);
	}
	
	@Override
	public List<ExceptionalReferenceDataDto> getExceptionalReferenceData(
			String checkNumber, String accountNumber, final String itemType) {
		 NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		 
		 String selectColumn = "";
		 if(itemType.equals("PAID")) {
			 selectColumn = "erd.paid_date as date ";
		 } else if(itemType.equals("STOP")) {
			 selectColumn = "erd.stop_date as date ";
		 }
	        
	        String sql = "SELECT erd.id as Id, erd.amount as amount, erd.check_number as checkNumber, "+ selectColumn
	    			+ "FROM reference_data_exception erd "+
	    			"WHERE erd.check_number = :checkNumber  " +
	                "AND erd.account_number = :accountNumber "+
	                "AND erd.item_type = :itemType ";
	    			
	        Map<String, Object> namedParameters = new HashMap<String, Object>();
	        namedParameters.put("checkNumber", checkNumber);
	        namedParameters.put("accountNumber", accountNumber);
	        namedParameters.put("itemType", itemType);
	        List<ExceptionalReferenceDataDto> exceptionalReferenceDataDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<ExceptionalReferenceDataDto>() {
	        	 @Override
	             public ExceptionalReferenceDataDto mapRow(ResultSet rs, int rowNum) throws SQLException {
	        		 ExceptionalReferenceDataDto exceptionalReferenceDataDto = new ExceptionalReferenceDataDto();
	        		 exceptionalReferenceDataDto.setExpReferenceDataId(rs.getLong("Id"));
	        		 exceptionalReferenceDataDto.setAmount(rs.getString("amount"));
	        		 exceptionalReferenceDataDto.setCheckNumber(rs.getString("checkNumber"));
	        		 try {
                         if (itemType.equals("STOP")) {
                             exceptionalReferenceDataDto.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("date")));
                         } else if (itemType.equals("PAID")) {
                             exceptionalReferenceDataDto.setDate(new SimpleDateFormat("MMddyyyy").parse(rs.getString("date")));
                         }
                     } catch (ParseException e) {
						logger.error(Log.event(Event.DATE_PARSE_EXCEPTION,"Exception while parsing the date date received is "+ rs.getString("date")+" "+e.getMessage(),e),e);
					}
	                 return exceptionalReferenceDataDto;
	             }
	        });
	        return exceptionalReferenceDataDtos;
	}

    @Override
    public void deleteUsersFromAccount(Long accountId, List<Long> userIds) {
    	if(userIds == null || userIds.isEmpty())
    		return;
    	NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    	String sql = "DELETE FROM  USER_DETAIL_ACCOUNT "
	             +" WHERE ACCOUNT_ID = :accountId  AND USER_DETAIL_ID IN (:userIds) ";
	     
	     Map<String, Object> namedParameters = new HashMap<String, Object>();
	     namedParameters.put("accountId", accountId);
	     namedParameters.put("userIds", userIds);
	     namedParameterJdbcTemplate.update(sql, namedParameters);
    }
    
    @Override
    public void addUsersToAccount(Long accountId, List<Long> userIds) {
    	if(userIds == null || userIds.isEmpty())
    		return;
    	String sql = "INSERT INTO  USER_DETAIL_ACCOUNT (USER_DETAIL_ID, ACCOUNT_ID) VALUES (?, ?)";
    	List<Object[]> parameters = new ArrayList<Object[]>();
    	for (Long userId : userIds) {
           	parameters.add(new Object[]{userId,accountId});
        }
        super.batchUpdate(sql, parameters);
    }
    
    @Override
	public List<Long> systemMessageInUserDetailHistory(Long user_detail_id,
			Long messageId) {
		logger.info("Check Message Id Exists In User_Detail_History for specific User");
		String messageString = "'%"+messageId+"%'";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = "SELECT count(*) AS COUNT FROM USER_DETAIL_HISTORY WHERE USER_COMMENT LIKE "+messageString+" and user_detail_id=:userId";
        logger.debug(sql);
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("userId", user_detail_id);
        List<Long> count = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            	 Long count = rs.getLong("COUNT");
            	  return count;
            }
        	});
        return count;
       }
    
	@Override
	public List<CompanyDTO> findSelectedCompanies(List<Long> bankIds, Boolean fetchAll) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        List<CompanyDTO> companyDtos = new ArrayList<CompanyDTO>();
        String clause = "IN";
        if(!fetchAll) {
        	clause = "Not In";
        }
        String sql = "SELECT ID, NAME FROM COMPANY WHERE BANK_ID "+ clause + " (:bankIds)";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("bankIds", bankIds);
        companyDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<CompanyDTO>() {
            @Override
            public CompanyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            	CompanyDTO dto = new CompanyDTO();
                dto.setId(rs.getLong("ID"));
                dto.setName(rs.getString("NAME"));
                return dto;
            }
        });
        return companyDtos;
	}

    @Override
    public List<JobDto> getAllJobHistory() {
        String sql = "SELECT " +
                "  job2_.id            AS jobId, " +
                "  job2_.is_active    AS isActive, " +
                "  jobstatust1_.name   AS jobStatus, " +
                "  job2_.last_run_date AS lastRunDate, " +
                "  job2_.next_run_date AS nextRunDate " +
                "FROM " +
                "    job_history jobhistory0_ " +
                "    LEFT OUTER JOIN " +
                "    job_status_type jobstatust1_ " +
                "      ON jobhistory0_.job_status_type_id = jobstatust1_.id " +
                "    LEFT OUTER JOIN " +
                "    job job2_ " +
                "      ON jobhistory0_.job_id = job2_.id " +
                "    CROSS " +
                "    JOIN " +
                "    job job4_ " +
                "WHERE " +
                "  jobhistory0_.job_id = job4_.id " +
                "  AND ( " +
                "    jobhistory0_.id IN ( " +
                "      SELECT " +
                "        max(jobhistory3_.id) " +
                "      FROM " +
                "        job_history jobhistory3_ " +
                "      GROUP BY " +
                "        jobhistory3_.job_id " +
                "    ) " +
                "  ) " +
                "ORDER BY " +
                "  job4_.name ASC";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        List<JobDto> jobDtos = new ArrayList<JobDto>();
        jobDtos = jdbcTemplate.query(sql, new RowMapper<JobDto>() {
            @Override
            public JobDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                JobDto jobDto = new JobDto();
                jobDto.setJobStatusType(rs.getString("jobStatus"));
                jobDto.setJobNextRunDate(rs.getString("nextRunDate"));
                jobDto.setJobLastRunDate(rs.getString("lastRunDate"));
                jobDto.setJobId(rs.getLong("jobId"));
                jobDto.setActive(rs.getBoolean("isActive"));
                return jobDto;
            }
        });
        return jobDtos;
    }

	@Override
	public List<AccountDto> findSelectedAccounts(List<Long> compIds, List<Long> bankIds) {
		logger.debug("BankIds:"+bankIds);
		logger.debug("compIds:"+compIds);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        List<AccountDto> accountDtos = new ArrayList<AccountDto>();
        String sql = "SELECT NUMBER FROM ACCOUNT WHERE BANK_ID IN (:bankIds) AND COMPANY_ID IN (:compIds)";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("bankIds", bankIds);
        namedParameters.put("compIds", compIds);
        accountDtos = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<AccountDto>() {
            @Override
            public AccountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            	AccountDto dto = new AccountDto();
                dto.setAccountNumber(rs.getString("Number"));
                return dto;
            }
        });
        return accountDtos;
	}

	@Override
	public Long findJobStepNumOfItemsProcessedInFile(java.util.Date jobStepActualStartTime, java.util.Date jobStepActualEndTime, String fileType) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = " SELECT count(*) as COUNT " +
        			 " FROM reference_data rd " +
        			 " WHERE rd.file_meta_data_id " +
        			 " IN ( " +
        			 "   SELECT " +
        			 "   fmd.id " +
        			 "   FROM file_meta_data fmd " +
                     "   JOIN file_type ft " +
                     "   ON fmd.file_type_id = ft.id " +
                     "   AND ft.name = :fileType "+
        			 "   WHERE fmd.date_created BETWEEN :jobStepActualStartTime AND :jobStepActualEndTime )";    
        
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("fileType", fileType);
        namedParameters.put("jobStepActualStartTime", jobStepActualStartTime);
        namedParameters.put("jobStepActualEndTime", jobStepActualEndTime);
        Long itemsProcessedCount = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Long.class);        
        return itemsProcessedCount;
	}
	
	@Override
	public Long findJobStepNumOfErrorsInFile(java.util.Date jobStepActualStartTime, java.util.Date jobStepActualEndTime, String fileType) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = " SELECT count(*) " +
        			 " FROM reference_data_exception rde " +
        			 " WHERE rde.file_meta_data_id " +
        			 " IN ( " +
        			 "   SELECT " +
        			 "   fmd.id " +
        			 "   FROM file_meta_data fmd " +
                     "   JOIN file_type ft " +
                     "   ON fmd.file_type_id = ft.id " +
                     "   AND ft.name = :fileType "+
        			 "   WHERE fmd.date_created BETWEEN :jobStepActualStartTime AND :jobStepActualEndTime )";    
        
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("fileType", fileType);
        namedParameters.put("jobStepActualStartTime", jobStepActualStartTime);
        namedParameters.put("jobStepActualEndTime", jobStepActualEndTime);        
        Long errorCount = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Long.class);
        return errorCount;
	}
	
	@Override
	public List<ItemErrorRecordsDto> findErrorsInFile(java.util.Date jobStartDateTime, java.util.Date jobEndDateTime) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());	
		
        String sql = " SELECT " +  
        			 " 	rde.file_import_line_number as fileNumber, " +
        			 " 	rde.account_number as accountNumber, " + 
        			 " 	rde.trace_number as traceNumber, " +
        			 " 	rde.check_number as checkNumber, " +
        			 " 	rde.item_type as itemType, " +
        			 " 	rde.amount as amount, " +
        			 " 	rde.paid_date as paidDate, " +
        			 " 	rde.stop_date as stopDate, " +
        			 " 	rde.stop_presented_date as stopPresentedDate, " +
        			 " 	et.description as exceptionType" +
        			 " FROM reference_data_exception rde " +
        			 " LEFT OUTER JOIN exception_type et " +
        			 " ON rde.exception_type_id= et.id " +
        			 " WHERE rde.file_meta_data_id " +
        			 " IN ( " +
        			 "		SELECT fmd.id " +
        			 "		FROM file_meta_data fmd " +
        			 "		WHERE fmd.date_created BETWEEN :jobStartDateTime AND :jobEndDateTime ) " +
        			 " ORDER BY rde.file_import_line_number ASC";      
        
        
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("jobStartDateTime", jobStartDateTime);
        namedParameters.put("jobEndDateTime", jobEndDateTime);        
        
        List<ItemErrorRecordsDto> dto = new ArrayList<ItemErrorRecordsDto>();
        dto = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<ItemErrorRecordsDto>() {
            @Override
            public ItemErrorRecordsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            	ItemErrorRecordsDto dto = new ItemErrorRecordsDto();
        		dto.setFileLineNumber(rs.getString("fileNumber"));
        		dto.setAccountNumber(rs.getString("accountNumber"));
        		dto.setTraceNumber(rs.getString("traceNumber"));
        		dto.setCheckNumber(rs.getString("checkNumber"));
        		dto.setItemType(rs.getString("itemType"));
        		dto.setAmount(PPUtils.stripLeadingZeros(rs.getString("amount")));
        		dto.setPaidDate(rs.getString("paidDate"));
        		dto.setStopDate(rs.getString("stopDate"));
        		dto.setStopPresentedDate(rs.getString("stopPresentedDate"));
        		dto.setExceptionTypeName(rs.getString("exceptionType"));
                return dto;
            }
        });
        return dto;
	 }
	
	@Override
	public List<RecentFileDto> filterFilesBy(Company userCompany, java.util.Date uploadDate, List<Long> fileMetaDataIds, String noOfDaysBefore){
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    	Map<String, Object> namedParameters = new HashMap<String, Object>();
    	StringBuilder sqlBuilder = new StringBuilder(
    			 "select "+
    		     "filemetadata.id as file_meta_data_id, "+
    		     "company.name as company_name, "+
    		     "filemetadata.created_by, "+
    		     "filemetadata.date_created, "+
    		     "filemetadata.date_modified, "+
    		     "filemetadata.modified_by, "+
    		     "filemetadata.checksum, "+
    		     "filemetadata.file_name, "+
    		     "filemetadata.file_size, "+
    		     "filetype.description as file_type, "+
    		     "filemetadata.items_received, "+
    		     "filemetadata.original_file_name, "+
    		     "filemetadata.status, "+
    		     "filemetadata.upload_directory "+
    		 "from "+
    		     "file_meta_data filemetadata "+
    		 "left outer join "+
    		     "file_mapping filemapping "+
    		         "on filemetadata.file_mapping_id=filemapping.id "+
    		 "left outer join "+
    		     "company company "+
    		         "on filemapping.company_id=company.id cross "+
    		 "join "+
    		     "file_mapping filemapping2 cross "+
    		 "join "+
    		     "file_type filetype "+
    		 "where "+
    		     "filemetadata.file_mapping_id=filemapping2.id "+
    		     "and filemetadata.file_type_id=filetype.id "+
    		     //"and filemapping2.company_id=1 "+
    		     "and filetype.name='CUSTOMER_UPLOAD' "
    		     //"and CAST(filemetadata.date_created as datetime)>= CAST('2014-07-08' AS datetime) "+
    		 //"order by "+
    		     //"filemetadata.date_created desc"
    	);
    	sqlBuilder.append(" AND filemapping2.company_id = :companyId");
    	namedParameters.put("companyId", userCompany.getId());
    	if(fileMetaDataIds != null && fileMetaDataIds.size() > 0){
    		sqlBuilder.append(" AND filemetadata.id IN (:fileMetaDataIds)");
    		namedParameters.put("fileMetaDataIds", fileMetaDataIds);
		}
		if(!noOfDaysBefore.equalsIgnoreCase("")){
			sqlBuilder.append(" AND CAST(filemetadata.date_created as date) >= CAST(:dateCreated AS date)");
			namedParameters.put("dateCreated", DateUtils.getDateBefore(Integer.parseInt(noOfDaysBefore)));
		} else {
			if(uploadDate != null){
	    		sqlBuilder.append(" AND CAST(filemetadata.date_created as date) = CAST(:dateCreated AS date)");
	            namedParameters.put("dateCreated", uploadDate);
	    	}
		}
    	sqlBuilder.append(" order by filemetadata.date_created desc;");
    	logger.info("Dynamic sql built to get the file meta data :"+sqlBuilder.toString());
    	List<RecentFileDto> RecentFileDtoList = namedParameterJdbcTemplate.query(sqlBuilder.toString().trim(), namedParameters, new RowMapper<RecentFileDto>() {
            @Override
          public RecentFileDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            	   RecentFileDto recentFileDto = new RecentFileDto();
            	   recentFileDto.setFileMetaDataId(rs.getLong("FILE_META_DATA_ID"));
            	   recentFileDto.setFileName(rs.getString("ORIGINAL_FILE_NAME"));
            	   recentFileDto.setFileUid(rs.getString("FILE_NAME"));
            	   recentFileDto.setNoOfRecords(rs.getLong("ITEMS_RECEIVED"));
            	   recentFileDto.setUploadDate(rs.getTimestamp("date_created"));
            	   recentFileDto.setCompanyName(rs.getString("company_name"));     	   
              return recentFileDto;
          }
      });
      return RecentFileDtoList;
	}

	@Override
	public String findLastCronExpressionByJobId(Long jobId) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = " SELECT j.cron_expression " +
        			 " FROM job j " +
        			 " WHERE j.id = :jobId ";        
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("jobId", jobId);      
        String cronExpression = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
        return cronExpression;
	}
	
	@Override
	public JobDto findLastJobConfigurationBy(Long jobId) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = " SELECT 	j.id  AS jobId, " +
        			 " 			j.cron_expression AS cronExpression, " +
        			 " 			js.name AS jobStatus, " +
        			 " 			jh.actual_start_time AS jobActualStartTime, " +
        			 " 			jh.actual_end_time AS jobActualEndTime, " +
        			 " 			j.last_run_date AS lastRunDate " +
        			 " 	FROM  job_history jh " +
        			 " 	JOIN job_status_type js " +
        			 " 	ON jh.job_status_type_id = js.id " +
        			 " 	JOIN job j " +
        			 " 	ON jh.job_id = j.id " +
        			 " 	WHERE j.id = :jobId " +
        			 " 	AND jh.id IN ( " +
        			 " 		SELECT max(jh1.id) " +
        			 " 		FROM job_history jh1 " +
        			 " 		WHERE jh1.job_id = :jobId " +
        			 " 		GROUP BY jh1.job_id " +
        			 " 	 ) ";        
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("jobId", jobId);      
        JobDto jobDto = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<JobDto>() {
            @Override
            public JobDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                JobDto jobDto = new JobDto();
                jobDto.setJobStatusType(rs.getString("jobStatus"));
                jobDto.setCronExpression(rs.getString("cronExpression"));
                jobDto.setJobActualStartTime(rs.getString("jobActualStartTime"));
                jobDto.setJobActualEndTime(rs.getString("jobActualEndTime"));
                jobDto.setJobLastRunDate(rs.getString("lastRunDate"));
                jobDto.setJobId(rs.getLong("jobId"));
                return jobDto;
            }
        });
        return jobDto;
	}
	
	@Override
	public List<String> findJobStepFileNames(java.util.Date jobStepActualStartTime, java.util.Date jobStepActualEndTime, String fileType) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
        String sql = " 	SELECT " +
        			 "  fmd.original_file_name " +
        			 "  FROM file_meta_data fmd " +
                     "  JOIN file_type ft " +
                     "  ON fmd.file_type_id = ft.id " +
                     "  AND ft.name = :fileType "+
        			 "  WHERE fmd.date_created BETWEEN :jobStepActualStartTime AND :jobStepActualEndTime";    
        
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("fileType", fileType);
        namedParameters.put("jobStepActualStartTime", jobStepActualStartTime);
        namedParameters.put("jobStepActualEndTime", jobStepActualEndTime);        
        List<String> fileNameList = namedParameterJdbcTemplate.queryForList(sql, namedParameters, String.class);
        return fileNameList;
	}
	
	@Override
	public List<Long> findFileMetaDataIdsByAccountId(Long accountId){
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		String sql = "Select DISTINCT(cd.file_meta_data_id) from check_detail cd " +
					"WHERE cd.account_id = :accountId " +
					"AND cd.file_meta_data_id IS NOT NULL";    
	   Map<String, Object> namedParameters = new HashMap<String, Object>();
	   namedParameters.put("accountId", accountId);
	   List<Long> fileMetaDataIdList = namedParameterJdbcTemplate.queryForList(sql, namedParameters, Long.class);
	   return fileMetaDataIdList;
	}

}
