package com.westernalliancebancorp.positivepay.job.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.dao.JobCriteriaDataDao;
import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.AbstractProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.service.PaidService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

/**
 * This class is executed by CheckProcessJob class
 * User:	Gopal Patil
 * Date:	Jan 7, 2014
 * Time:	1:47:50 PM
 */
@Component
public class CheckProcessTask extends AbstractProcessTask {

    @Loggable
    private Logger logger;

    @Autowired
    private JobCriteriaDataDao jobCriteriaDataDao;

    @Autowired
    private PaidService paidService;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private BatchDao batchDao;

    /**
     * This method is processed by CheckProcessJob class
     *
     * @param executionContext
     */
    @Override
    public String processTask(JobExecutionContext executionContext) throws SchedulerException {
        try {
            logger.debug("Executing Check Process Task");
            JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
            Long jobStepId = (Long) jobDataMap.get(Constants.JOB_STEP_ID);
            List<JobCriteriaData> jobCriteriaDataList = jobCriteriaDataDao.findByJobId(jobStepId);
            if (jobCriteriaDataList != null) {
                if (jobCriteriaDataList.isEmpty()) {
                    List<Long> accountIdsList = ModelUtils.executeWithNoCriteria(userDetailDao, batchDao);
                    if (accountIdsList != null && !accountIdsList.isEmpty()) {
                        Map<String, Integer> returnMap = paidService.markChecksPaidByAccountIds(accountIdsList);
                        jobDataMap.put(Constants.ITEMS_IN_ERROR, returnMap.get(Constants.ITEMS_IN_ERROR));
                        jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY));
                        return JobStatus.COMPLETED.name();
                    } else {
                        logger.error("User doesn't seems to have any accounts attached, so silently exiting");
                        return JobStatus.COMPLETED.name();
                    }
                }
                for (JobCriteriaData jobCriteriaData : jobCriteriaDataList) {
                    if (jobCriteriaData.getCriteriaName().name().equals(JobCriteriaData.CRITERIA_NAME.BANK.name())) {
                        String csvValues = jobCriteriaData.getValue();
                        if (csvValues != null && !csvValues.isEmpty()) {
                            List<Long> bankIds = new ArrayList<Long>();
                            for (String value : csvValues.split(",")) {
                                bankIds.add(new Long(value));
                            }
                            Map<String, Integer> returnMap = paidService.markChecksPaidByBankIds(bankIds);
                            jobDataMap.put(Constants.ITEMS_IN_ERROR, returnMap.get(Constants.ITEMS_IN_ERROR));
                            jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY));
                        }
                    }

                    if (jobCriteriaData.getCriteriaName().name().equals(JobCriteriaData.CRITERIA_NAME.COMPANY.name())) {
                        String csvValues = jobCriteriaData.getValue();
                        if (csvValues != null && !csvValues.isEmpty()) {
                            List<Long> companyIds = new ArrayList<Long>();
                            for (String value : csvValues.split(",")) {
                                companyIds.add(new Long(value));
                            }
                            Map<String, Integer> returnMap = paidService.markChecksPaidByCompanyIds(companyIds);
                            jobDataMap.put(Constants.ITEMS_IN_ERROR, returnMap.get(Constants.ITEMS_IN_ERROR));
                            jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY));
                        }
                    }

                    if (jobCriteriaData.getCriteriaName().name().equals(JobCriteriaData.CRITERIA_NAME.ACCOUNT.name())) {
                        String csvValues = jobCriteriaData.getValue();
                        if (csvValues != null && !csvValues.isEmpty()) {
                            List<Long> accountIds = new ArrayList<Long>();
                            for (String value : csvValues.split(",")) {
                                accountIds.add(new Long(value));
                            }
                            Map<String, Integer> returnMap = paidService.markChecksPaidByAccountIds(accountIds);
                            jobDataMap.put(Constants.ITEMS_IN_ERROR, returnMap.get(Constants.ITEMS_IN_ERROR));
                            jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY));
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error(String.format("Exception thrown by: %s Exception: %s", executionContext.getJobDetail().getName(), e.getMessage()), e);
            return JobStatus.FAILED.name();
        }
        logger.info(String.format("Job : %s is finished...", executionContext.getJobDetail().getName()));
        return JobStatus.COMPLETED.name();
    }

}