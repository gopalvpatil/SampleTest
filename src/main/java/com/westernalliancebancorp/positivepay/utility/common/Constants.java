package com.westernalliancebancorp.positivepay.utility.common;

/**
 * @author Gopal Patil
 *
 */
public final class Constants {

    private Constants() {    
    }
    
    //Jobs
	public static final String COMPLETED_SUCCESSFUL  = "Completed-Successful";
	public static final String COMPLETED_ERROR = "Completed-Error";
	public static final String RUNNING = "Running";
	public static final String CREATED = "CREATED";
	
	public static final String ONE_TIME = "One-time";
	public static final String RECURRING = "Recurring";	
	
    public static final String JOB_GROUP_NAME = "PositivePay"; 
    public static final String ONE_TIME_JOB_NAME = "OneTime";  
    public static final String RUN_SELECTED_JOB_NAME = "RunSelected";  
    public static final String CREATED_BY = "CreatedBy";
    public static final String PRINCIPAL = "principal";
    public static final String SOURCE = "source";
    public static final String JOB_DB_ID = "JOB_DB_ID";
    public static final String JOB_HISTORY_ID = "JOB_HISTORY_ID";
    public static final String JOB_STEP_ID = "JOB_STEP_ID";
    public static final String JOB_STEPS_COUNT = "JOB_STEP_COUNT";
    public static final String JOB_STEPS_SIZE = "JOB_STEP_SIZE";
    public static final String JOB_FAILED_FLAG = "JOB_FAILED_FLAG";
    public static final String JOB_STEP_EXCEPTION_FLAG = "JOB_STEP_EXCEPTION_FLAG";
    public static final String JOB_ACTUAL_START_TIME = "JobActualStartTime";
    public static final String JOB_STEP_ACTUAL_START_TIME = "JobStepActualStartTime";
    public static final String CHECKS_LIST = "CHECKS_LIST";
    public static final String CHECK_PROCESS_JOB = "CheckProcessJob";
    public static final String ITEMS_PROCESSED_SUCCESSFULLY = "ITEMS_PROCESSED_SUCCESSFULLY";
    public static final String ITEMS_IN_ERROR = "ITEMS_IN_ERROR";
	public static final String FILE_STATUS_CODE = "FILE_STATUS_CODE";
	public static final String NO_FILE = "1000";
	public static final String DUPLICATE_FILE = "1001";

    public static final String ACTION = "Action";
    public static final String CONVERT = "Convert"; 
    public static final String EXTERNAL_PROGRAM = "External Program";
    public static final String FILE_MANAGEMENT = "File Management";
    public static final String LOAD = "Load";
    public static final String EXCEPTION_TYPES = "Exception Types";
    public static final String REPORT_EXTRACT = "Report/Extract";
    
    
    //Reports
	public static final String MEDIA_TYPE_EXCEL = "application/vnd.ms-excel";
	public static final String MEDIA_TYPE_PDF = "application/pdf";
	public static final String MEDIA_TYPE_CSV = "text/csv";
	public static final String MEDIA_TYPE_HTML = "text/html";
	public static final String MEDIA_TYPE_TIFF = "image/tiff";
	public static final String MEDIA_TYPE_MHTML = "application/x-mimearchive";

	public static final String FILE_EXTENSION_EXCEL = ".xls";
	public static final String FILE_EXTENSION_PDF = ".pdf";
	public static final String FILE_EXTENSION_CSV = ".csv";
	public static final String FILE_EXTENSION_HTML = ".html";
	public static final String FILE_EXTENSION_TIFF = ".tiff";
	public static final String FILE_EXTENSION_MHTML = ".mhtml";

    //Formats
    public static final String CHECK_ISSUE_DATE_SQL_FORMAT = "yyyy-MM-dd";

    //Background Services User data names.
    public static final String CHECK_DTO = "CHECK_DTO";
    
    //check status
    public static final String MATCHED = "MATCHED";
    public static final String UNMATCHED = "UNMATCHED";
	public static final String CRS_PAID = "CRS_PAID";
	public static final String DAILY_STOP = "DAILY_STOP";
	public static final String STOP_PRESENTED = "STOP_PRESENTED";

    public static final String ZERO_CHECK_NUMBER = "0";
}
