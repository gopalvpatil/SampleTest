package com.westernalliancebancorp.positivepay.job;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.JobDao;
import com.westernalliancebancorp.positivepay.dao.JobExecutionLockerDao;
import com.westernalliancebancorp.positivepay.dao.JobStepDao;
import com.westernalliancebancorp.positivepay.exception.LockAcquisitionException;
import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobExecutionLocker;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.JobStatusType;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.model.JobStepHistory;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.JobHistoryService;
import com.westernalliancebancorp.positivepay.service.JobService;
import com.westernalliancebancorp.positivepay.service.JobStepHistoryService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

/**
 * @author Gopal Patil
 */
public abstract class AbstractProcessTask implements ProcessTask {

    @Loggable
    private Logger logger;
    @Autowired
    JobDao jobDao;
    @Autowired
    JobService jobService;    
    @Autowired
    JobStepDao jobStepDao;
    @Autowired
    JobHistoryService jobHistoryService;    
    @Autowired
    JobStepHistoryService jobStepHistoryService;
    @Autowired
    JobExecutionLockerDao jobExecutionLockerDao;

    @Value("${positivepay.machine.name}")
    private String machineName;
	@Value("${positivepay.thresholdTime}")
    private String thresholdTime;
    @Value("${positivepay.run.jobs.on.this.machine}")
    private boolean runJobsOnThisMachine;    
    @Value("${file.not.present}")
    private String fileNotPresent;    
    @Value("${duplicate.file.present}")
    private String duplicateFilePresent;
    
        @Override
    public void process(JobExecutionContext executionContext) {
        if(!runJobsOnThisMachine) {
            logger.info("Running jobs on this machine is forbidden, so not running any jobs");
            return;
        }
        PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Batch.name());
        JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
        //Boolean jobStepExcpetionFlag = (Boolean) jobDataMap.get(Constants.JOB_STEP_EXCEPTION_FLAG);
        Integer jobStepsCount = (Integer) jobDataMap.get(Constants.JOB_STEPS_COUNT);
        Integer jobStepsSize = (Integer) jobDataMap.get(Constants.JOB_STEPS_SIZE);
        try {        	
        	//If previous step got exception, lock need to be released before it process the next task
        	/*if(jobStepExcpetionFlag) {
            	this.releaseLock(executionContext);
            	jobDataMap.put(Constants.JOB_STEP_EXCEPTION_FLAG, Boolean.FALSE); 
            }  */
            if (isLockAcquired(executionContext)) {
                String jobStatus = Constants.RUNNING;
                //Save job history to insert running status record at first time
                if (jobStepsSize.intValue() == jobStepsCount.intValue()) {
                    //save history to show job is running
                    this.saveJobHistory(executionContext, jobStatus);
                }
                TransactionIdThreadLocal.remove();
                TransactionIdThreadLocal.set(RandomStringUtils.random(10, Boolean.TRUE, Boolean.TRUE));

                String status = processTask(executionContext);

                int itemsInError = jobDataMap.get(Constants.ITEMS_IN_ERROR) == null ? 0 : (Integer) jobDataMap.get(Constants.ITEMS_IN_ERROR);

                if (status.equals(JobStatus.FAILED.name()) || itemsInError > 0) {
                    jobDataMap.put(Constants.JOB_FAILED_FLAG, Boolean.TRUE);
                    this.saveJobStepHistory(executionContext, Constants.COMPLETED_ERROR);
                } else {
                    //If status already failed then don't enable flag to true
                    if (jobDataMap.get(Constants.JOB_FAILED_FLAG) != null) {
                        Boolean verify = (Boolean) jobDataMap.get(Constants.JOB_FAILED_FLAG);
                        if (verify) {
                            jobDataMap.put(Constants.JOB_FAILED_FLAG, Boolean.TRUE);
                        } else {
                            jobDataMap.put(Constants.JOB_FAILED_FLAG, Boolean.FALSE);
                        }
                    } else {
                        jobDataMap.put(Constants.JOB_FAILED_FLAG, Boolean.FALSE);
                    }
                    this.saveJobStepHistory(executionContext, Constants.COMPLETED_SUCCESSFUL);
                }
                Boolean jobFailedFlag = (Boolean) jobDataMap.get(Constants.JOB_FAILED_FLAG);
                if (jobStepsCount.intValue() == 1) {
                    if (jobFailedFlag) {
                        jobStatus = Constants.COMPLETED_ERROR;
                    } else {
                        jobStatus = Constants.COMPLETED_SUCCESSFUL;
                    }
                    this.updateJob(executionContext);
                    this.updateJobHistory(executionContext, jobStatus);
                }
                this.releaseLock(executionContext);
            }
        } catch(LockAcquisitionException lae) {
        	// If job is already running, don't update job history and don't release a lock.
            logger.error(Log.event(Event.JOB_RUN_ERROR, "Job is already running. Hence for this job, history can't be maintained and lock wont release till previous job not get completed",lae),lae);
        	jobDataMap.put(Constants.JOB_STEP_EXCEPTION_FLAG, Boolean.TRUE);
        	jobDataMap.put(Constants.JOB_FAILED_FLAG, Boolean.TRUE);
        } catch (Exception e) {
        	//If exception occurred in background job service, then job should be updated with completed-error status and lock should be release
        	try {
        		this.updateJob(executionContext);
        		this.updateJobHistory(executionContext, Constants.COMPLETED_ERROR);
			} catch (SchedulerException e1) {
                logger.error(Log.event(Event.JOB_RUN_ERROR, "--Exception occurred while updating the job history"+executionContext.getJobDetail().getFullName()+" with the error",e1),e1);
            } catch (Exception e1) {
                logger.error(Log.event(Event.JOB_RUN_ERROR, "--Exception occurred while updating the job history"+executionContext.getJobDetail().getFullName()+" with the error",e1),e1);
            }
            //release lock separately catch, because job history can throw exception
			try {
				this.releaseLock(executionContext);
			} catch (LockAcquisitionException e1) {
                e1.printStackTrace();
                logger.error(Log.event(Event.JOB_RUN_ERROR, "---SchedulerException occurred while releasing lock"+executionContext.getJobDetail().getFullName()+" with the error",e1),e1);
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.error(Log.event(Event.JOB_RUN_ERROR, "---Exception occurred while releasing lock"+executionContext.getJobDetail().getFullName()+" with the error",e1),e1);
            }
            jobDataMap.put(Constants.JOB_STEP_EXCEPTION_FLAG, Boolean.TRUE);
        	jobDataMap.put(Constants.JOB_FAILED_FLAG, Boolean.TRUE);
            logger.error(Log.event(Event.JOB_RUN_ERROR, "Exception occurred in background job service, then job should be updated with completed-error status and lock should be release",e),e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void releaseLock(JobExecutionContext executionContext) throws LockAcquisitionException {
        String jobName = executionContext.getJobDetail().getName();
        logger.info(String.format("Trying to release lock with the job name %s", jobName));
        Long jobId = (Long)executionContext.getJobDetail().getJobDataMap().get(Constants.JOB_DB_ID);
        Job job = jobDao.findById(jobId);
        List<JobExecutionLocker> jobExecutionLockerList = jobExecutionLockerDao.findByJobId(job.getId());
        if(jobExecutionLockerList != null && jobExecutionLockerList.size()>0) {
            JobExecutionLocker jobExecutionLocker = jobExecutionLockerList.get(0);
            jobExecutionLockerDao.delete(jobExecutionLocker);
        }
    }

    private boolean acquireLock(JobExecutionContext executionContext) {
        logger.info(String.format("Acquiring the lock for the job with name : %s scheduled to file at : %s", executionContext.getJobDetail().getName(), executionContext.getTrigger().getStartTime()));
        JobExecutionLocker jobExecutionLocker = new JobExecutionLocker();
        jobExecutionLocker.setRunning(Boolean.TRUE);
        Long jobId = (Long) executionContext.getJobDetail().getJobDataMap().get(Constants.JOB_DB_ID);

        try {
            Job job = jobService.findJobById(String.valueOf(jobId.longValue()));
            jobExecutionLocker.setScheduledStartTime(executionContext.getTrigger().getStartTime());
            jobExecutionLocker.setActualStartTime(new Date());
            jobExecutionLocker.setJob(job);
            jobExecutionLocker.setExecutingOnMachine(getMachineName());
             /*if(SecurityUtility.getPrincipal() == null)
             SecurityUtility.setPrincipal(job.getAuditInfo().getModifiedBy());*/
            handlePrincipal(job);
            jobExecutionLockerDao.save(jobExecutionLocker);
        } catch (DataIntegrityViolationException div) {
            List<JobExecutionLocker> jobExecutionLockerList = jobExecutionLockerDao.findByJobId(jobId);
            if (jobExecutionLockerList != null && jobExecutionLockerList.size() > 0) {
                logger.info("Racing condition.. machine " + jobExecutionLockerList.get(0).getExecutingOnMachine() + " has already acquired  the lock and I failed to get the lock :( ");
            } else {
                logger.info("Racing condition.. machine  has already acquired  the lock and I failed to get the lock :( ");
                //return acquireLock(executionContext);
            }
            return Boolean.FALSE;
        } catch (RuntimeException e) {
            logger.error(Log.event(Event.JOB_RUN_ERROR, "Exception occurred while acquiring the lock  ", e), e);
            return Boolean.FALSE;
        }
        String transactionId = SecurityUtility.setTransactionId();
        logger.info(String.format("Successfully acquired the lock for the job with name : %s, scheduled to file at : %s, Transaction Id : %s ", executionContext.getJobDetail().getName(), executionContext.getTrigger().getStartTime(), transactionId));
        return Boolean.TRUE;
    }

    private String getMachineName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error(Log.event(Event.JOB_RUN_ERROR, "UnknownHostException occurred while acquiring the lock ",e),e);
            e.printStackTrace();
            return machineName;
        }
    }

    private String handlePrincipal(Job job) {
        String userName = null;
        try {
            userName = SecurityUtility.getPrincipal();
        } catch (Exception ex) {
            logger.error("No user in security context, which means job is not triggered by logged inn user", ex);
        }
        if (userName == null) {
            SecurityUtility.setPrincipal(job.getAuditInfo().getModifiedBy());
            userName = job.getAuditInfo().getModifiedBy();
        }
        return userName;
    }
    
	@Override
    public void setPrincipal(String principal) {
        if (principal == null) {
            throw new NullPointerException("Principal value passed is null please check");
        }
        PositivePayThreadLocal.set(principal);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean isLockAcquired(JobExecutionContext executionContext) throws LockAcquisitionException {
        Long jobId = (Long)executionContext.getJobDetail().getJobDataMap().get(Constants.JOB_DB_ID);
        Job job= jobDao.findById(jobId);
        logger.info(String.format("Trying to acquire lock with the job name %s", job.getName()));

        List<JobExecutionLocker> jobExecutionLockerList = jobExecutionLockerDao.findByJobId(jobId);
        if (jobExecutionLockerList == null || jobExecutionLockerList.size() <= 0) {
            logger.info(String.format("No jobs with the job name %s is being executed with cron expression %s so creating a new one and starting", job.getName(), job.getCronExpression()));
            return acquireLock(executionContext);
        } else {
            if (jobExecutionLockerList.size() > 1) {
                logger.error(String.format("Functionality error size of the list should not be more than 1 currently found %d jobs for job name %s scheduled to start at %s", jobExecutionLockerList.size(), job.getName(), job.getCronExpression()));
                throw new LockAcquisitionException(String.format("More than one scheduled jobs found with the same name and the scheduled time, please check job name %s with cron expression %s", jobId, job.getCronExpression()));
            } else {
                JobExecutionLocker jobExecutionLocker = jobExecutionLockerList.get(0);
                long thresholdTimeL = Long.parseLong(thresholdTime);
                long jobCompletionTime = jobExecutionLocker.getActualStartTime().getTime() + thresholdTimeL;
                long systemTimeInMillis = System.currentTimeMillis();
                if (systemTimeInMillis > jobCompletionTime) {
                    logger.info(String.format("System time : %d, Job completion time : %d as system time is more than completion time remove the entry for job %s scheduled to fire at %s", systemTimeInMillis, jobCompletionTime, job.getName(), job.getCronExpression()));
                    jobExecutionLockerDao.delete(jobExecutionLocker);
                    return acquireLock(executionContext);
                } else {
                    logger.info(String.format("Jobs threshold time is still with in the limits, System time  %d Job completion time %d", systemTimeInMillis, jobCompletionTime));
                    return Boolean.FALSE;
                }
            }
        }
    }

    public abstract String processTask(JobExecutionContext executionContext) throws SchedulerException;

    @Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveJobStepHistory(JobExecutionContext executionContext,
			String status) throws SchedulerException, Exception {
		
		JobStepHistory jobStepHistory = new JobStepHistory();		
        JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
        
        Long jobStepId = (Long) jobDataMap.get(Constants.JOB_STEP_ID);        
        JobStep jobStep = jobStepDao.findById(jobStepId);
        
    	Long jobId = (Long) jobDataMap.get(Constants.JOB_DB_ID);
    	Job job = jobService.findJobById(String.valueOf(jobId.longValue()));
    	
        JobStatusType jobStatusType = jobHistoryService.findJobStatusTypeBy(status); 
        
        int itemProcessed = (Integer) jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) == null ? 0 : (Integer) jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
        int itemsInError = (Integer) jobDataMap.get(Constants.ITEMS_IN_ERROR) == null ? 0 : (Integer) jobDataMap.get(Constants.ITEMS_IN_ERROR);
        
        StringBuilder commentsBuilder = new StringBuilder();
        if(status.equals(Constants.COMPLETED_SUCCESSFUL) && itemsInError <= 0) {
        	commentsBuilder.append(jobStep.getName()).append(" completed successfully.");
        }else{
        	commentsBuilder.append("Tracking ID-").append(TransactionIdThreadLocal.get()).append(", ");
        	commentsBuilder.append(jobStep.getName()).append(" completed with errors.");
        }
        
        if(jobDataMap.containsKey(Constants.FILE_STATUS_CODE)){
            int code =  (Integer) jobDataMap.get(Constants.FILE_STATUS_CODE);
            String fileStatusCode = String.valueOf(code);
            if(fileStatusCode.equals(Constants.NO_FILE)) {
            	commentsBuilder.append(" "+fileNotPresent);
            } else if (fileStatusCode.equals(Constants.DUPLICATE_FILE)){
            	commentsBuilder.append(" "+duplicateFilePresent);
            }            
        }
        
    	//TIMEZONE changes
    	Date actualStartTime = (Date) jobDataMap.get(Constants.JOB_STEP_ACTUAL_START_TIME);
        Date actualStartTimeZone = DateUtils.convertDateToUserTimezone(actualStartTime, job.getTimezone());
        Date actualEndTimeZone = DateUtils.convertDateToUserTimezone(new Date(), job.getTimezone());
    	
    	jobStepHistory.setActualStartTime(actualStartTimeZone);
    	jobStepHistory.setActualEndTime(actualEndTimeZone);    	
    	jobStepHistory.setExecutingOnMachine(InetAddress.getLocalHost().getHostName());        
    	jobStepHistory.setJob(job);
    	jobStepHistory.setJobStep(jobStep);
    	jobStepHistory.setJobStatusType(jobStatusType);
    	jobStepHistory.setComments(commentsBuilder.toString());


        jobStepHistory.setNumberItemsProcessed((long) itemProcessed);
        jobStepHistory.setNumberOfErrors((long) itemsInError);
        SecurityUtility.setPrincipal(job.getAuditInfo().getModifiedBy());    	
    	jobStepHistoryService.save(jobStepHistory);
	    logger.info(String.format("Job Step history updated for job name: [ %s ]  and job Step: [ %s ] with ::\n"
	        		+ "[actual start time: %s], "
	        		+ "[actual end date: %s], "
	        		+ "[and executing on machine: %s ]", 
	        		job.getName(), jobStep.getName(), jobStepHistory.getActualStartTime(), jobStepHistory.getActualEndTime(), jobStepHistory.getExecutingOnMachine()));

	}

    @Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateJob(JobExecutionContext executionContext)	throws SchedulerException, Exception {		
    	JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
    	Long jobId = (Long) jobDataMap.get(Constants.JOB_DB_ID);
    	Job job = jobService.findJobById(String.valueOf(jobId.longValue()));
    	
    	//TIMEZONE changes
        Date lastJobFiredTime = DateUtils.convertDateToUserTimezone(executionContext.getFireTime(), job.getTimezone());
    	job.setLastRunDate(lastJobFiredTime);   
		
    	//executionContext.getNextFireTime() will be null if job run manually to overcome this used below condition
        if(executionContext.getNextFireTime() != null) {
            Date nextJobFireTime = DateUtils.convertDateToUserTimezone(executionContext.getNextFireTime(), job.getTimezone());
        	job.setNextRunDate(nextJobFireTime);
        } else{ 
    		CronExpression expression = new CronExpression(job.getCronExpression());
    		Date jobNextRunDate = expression.getNextValidTimeAfter(new Date());
    		//if job is one time then scheduled start date should be null, so set it to initial start time
    		if(jobNextRunDate != null) {
    			Date jobNextFireTimeZone = DateUtils.convertDateToUserTimezone(jobNextRunDate, job.getTimezone());
    			job.setNextRunDate(jobNextFireTimeZone);
    		} else {
    			Date jobNextFireTimeZone = DateUtils.convertDateToUserTimezone(job.getStartDate(), job.getTimezone());
    			job.setNextRunDate(jobNextFireTimeZone);
    		}
        }  
      
        logger.info("Last run date according to server timezone : "+executionContext.getFireTime());
        logger.info("Next run date according to server timezone : "+executionContext.getNextFireTime());
        logger.info(String.format("Job dates converted to user timezone: %s executed with last run date : %s and next run date : %s", job.getName(), job.getLastRunDate(), job.getNextRunDate()));
        SecurityUtility.setPrincipal(job.getAuditInfo().getModifiedBy());        
        jobDao.update(job);
	}

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveJobHistory(JobExecutionContext executionContext, String status) throws SchedulerException, Exception {
        JobHistory jobHistory = new JobHistory();
        
    	JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
    	Long jobId = (Long) jobDataMap.get(Constants.JOB_DB_ID);
    	Job job = jobService.findJobById(String.valueOf(jobId.longValue()));
        jobHistory.setJob(job);
        
        JobStatusType jobStatusType = jobHistoryService.findJobStatusTypeBy(status);
        jobHistory.setJobStatusType(jobStatusType);   
        
        //executionContext.getNextFireTime() will be null if job run manually to overcome this used below condition
        if(executionContext.getNextFireTime() != null) {
            Date jobNextFireTime = DateUtils.convertDateToUserTimezone(executionContext.getNextFireTime(), job.getTimezone());
        	jobHistory.setScheduledStartDate(jobNextFireTime);
        } else{ 
			CronExpression expression = new CronExpression(job.getCronExpression());
			Date jobNextRunDate = expression.getNextValidTimeAfter(new Date());

			//if job is one time then scheduled start date should be null, so set it to initial start time
			if(jobNextRunDate != null) {
				Date jobNextFireTimeZone = DateUtils.convertDateToUserTimezone(jobNextRunDate, job.getTimezone());
				jobHistory.setScheduledStartDate(jobNextFireTimeZone);
			} else {
				Date jobNextFireTimeZone = DateUtils.convertDateToUserTimezone(job.getStartDate(), job.getTimezone());
				jobHistory.setScheduledStartDate(jobNextFireTimeZone);
			}
        }

    	//TIMEZONE changes
    	Date actualStartTime = (Date) jobDataMap.get(Constants.JOB_STEP_ACTUAL_START_TIME);
        Date actualStartTimeZone = DateUtils.convertDateToUserTimezone(actualStartTime, job.getTimezone());
        Date actualEndTimeZone = DateUtils.convertDateToUserTimezone(new Date(), job.getTimezone());
    	
        jobHistory.setActualStartTime(actualStartTimeZone);
        jobHistory.setActualEndTime(actualEndTimeZone); 

        jobHistory.setExecutingOnMachine(InetAddress.getLocalHost().getHostName());
        SecurityUtility.setPrincipal(job.getAuditInfo().getModifiedBy());
        
        jobHistoryService.save(jobHistory);        
        jobDataMap.put(Constants.JOB_HISTORY_ID, jobHistory.getId());
        logger.info(String.format("Job history updated for job name %s  with ::\n"
        		+ "[actual start time %s], "
        		+ "[actual end date %s], "
        		+ "[Scheduled Start Date %s] "
        		+ "and [executing on machine is %s] ", 
        		job.getName(), jobHistory.getActualStartTime(), jobHistory.getActualEndTime(), jobHistory.getScheduledStartDate(), jobHistory.getExecutingOnMachine()));    
    }
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateJobHistory(JobExecutionContext executionContext, String status) throws SchedulerException, Exception {
    	
    	JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();

    	Long jobId = (Long) jobDataMap.get(Constants.JOB_DB_ID);
    	Job job = jobService.findJobById(String.valueOf(jobId.longValue()));  
        
    	//Get job history by unique key constraint of job_id and actual start time
        //JobHistory jobHistory = jobHistoryService.findByJobIdAndActualStartTime(job.getId(), actualStartTime);
    	Long jobHistoryId = (Long) jobDataMap.get(Constants.JOB_HISTORY_ID);
    	JobHistory jobHistory = jobHistoryService.findById(jobHistoryId);
        jobHistory.setJob(job);
        
        JobStatusType jobStatusType = jobHistoryService.findJobStatusTypeBy(status);
        jobHistory.setJobStatusType(jobStatusType);   
        
    	//TIMEZONE changes
        //executionContext.getNextFireTime() will be null if job run manually to overcome this used below condition
        if(executionContext.getNextFireTime() != null) {
            Date jobNextFireTime = DateUtils.convertDateToUserTimezone(executionContext.getNextFireTime(), job.getTimezone());
        	jobHistory.setScheduledStartDate(jobNextFireTime);
        } else{ 
			CronExpression expression = new CronExpression(job.getCronExpression());
			Date jobNextRunDate = expression.getNextValidTimeAfter(new Date());

			//if job is one time then scheduled start date should be null, so set it to initial start time
			if(jobNextRunDate != null) {
				Date jobNextFireTimeZone = DateUtils.convertDateToUserTimezone(jobNextRunDate, job.getTimezone());
				jobHistory.setScheduledStartDate(jobNextFireTimeZone);
			} else {
				Date jobNextFireTimeZone = DateUtils.convertDateToUserTimezone(job.getStartDate(), job.getTimezone());
				jobHistory.setScheduledStartDate(jobNextFireTimeZone);
			}
        }
        
    	//TIMEZONE changes
    	Date actualStartTime = (Date) jobDataMap.get(Constants.JOB_STEP_ACTUAL_START_TIME);
        Date actualStartTimeZone = DateUtils.convertDateToUserTimezone(actualStartTime, job.getTimezone());
        Date actualEndTimeZone = DateUtils.convertDateToUserTimezone(new Date(), job.getTimezone());

        jobHistory.setActualStartTime(actualStartTimeZone);
        jobHistory.setActualEndTime(actualEndTimeZone);
        jobHistory.setExecutingOnMachine(InetAddress.getLocalHost().getHostName());
        SecurityUtility.setPrincipal(job.getAuditInfo().getModifiedBy());
        
        jobHistoryService.update(jobHistory);
        logger.info(String.format("Job history updated for [job name: %s]  with ::\n"
        		+ "[actual start time %s], "
        		+ "[actual end date %s], "
        		+ "[Scheduled Start Date %s] "
        		+ "and[ executing on machine %s] ", 
        		job.getName(), jobHistory.getActualStartTime(), jobHistory.getActualEndTime(), jobHistory.getScheduledStartDate(), jobHistory.getExecutingOnMachine()));
    }
}
