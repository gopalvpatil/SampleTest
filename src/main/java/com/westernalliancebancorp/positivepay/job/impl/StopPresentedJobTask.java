package com.westernalliancebancorp.positivepay.job.impl;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.JobCriteriaDataDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.service.StopPresentedService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.AbstractProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User:	Gopal Patil
 * Date:	Jan 27, 2014
 * Time:	1:02:08 PM
 */
@Component
public class StopPresentedJobTask extends AbstractProcessTask {

    @Loggable
    private Logger logger;

    @Autowired
    StopPresentedService stopPresentedService;

    @Autowired
    private JobCriteriaDataDao jobCriteriaDataDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private BatchDao batchDao;

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.job.AbstractProcessTask#processTask(org.quartz.JobExecutionContext)
	 */
	@Override
	public String processTask(JobExecutionContext executionContext)
			throws SchedulerException {
        try {
            logger.debug("Executing MakeStaleJobTask");
            JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
            Long jobStepId = (Long) jobDataMap.get(Constants.JOB_STEP_ID);
            List<JobCriteriaData> jobCriteriaDataList = jobCriteriaDataDao.findByJobId(jobStepId);
            Map<String, Integer> returnMap = null;
            if (jobCriteriaDataList != null) {
                if (jobCriteriaDataList.isEmpty()) {
                    List<Long> accountIdsList = ModelUtils.executeWithNoCriteria(userDetailDao, batchDao);
                    if (accountIdsList != null && !accountIdsList.isEmpty()) {
                        returnMap = stopPresentedService.markChecksStopByAccountIds(accountIdsList);
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
                            returnMap = stopPresentedService.markChecksStopByBankIds(bankIds);
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
                            returnMap = stopPresentedService.markChecksStopByCompanyIds(companyIds);
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
                            returnMap = stopPresentedService.markChecksStopByAccountIds(accountIds);
                            jobDataMap.put(Constants.ITEMS_IN_ERROR, returnMap.get(Constants.ITEMS_IN_ERROR));
                            jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY));
                        }
                    }
                }
            }
            //call CheckService
        } catch (Exception e) {
            logger.error("Exception thrown by: " + executionContext.getJobDetail().getName()
                    + " Exception: " + e.getMessage(), e);
            return JobStatus.FAILED.name();
        }
        logger.info("Job : " + executionContext.getJobDetail().getName() + " is finished...");
        return JobStatus.COMPLETED.name();
    }

}
