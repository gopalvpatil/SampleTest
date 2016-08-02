package com.westernalliancebancorp.positivepay.dao.impl.jdbc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalCheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.dto.ExceptionalReferenceDataDto;
import com.westernalliancebancorp.positivepay.dto.ItemErrorRecordsDto;
import com.westernalliancebancorp.positivepay.dto.JobDto;
import com.westernalliancebancorp.positivepay.dto.RecentFileDto;
import com.westernalliancebancorp.positivepay.model.Company;

/**
 * User: gduggirala
 * Date: 16/5/14
 * Time: 8:29 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class BatchJdbcDaoTest {
    @Autowired
    BatchDao batchJdbcDao;
    
    @Autowired
    ReferenceDataDao referenceDataDao;
    
    @Autowired
    ExceptionalReferenceDataDao expReferenceDataDao;
    
    @Autowired
    ExceptionalCheckDao exceptionalCheckDao; 

    @Test
    @Ignore
    public void testGetItemsProcessedCount(){
        List<Long> fileMetadataIds = new ArrayList<Long>();
        fileMetadataIds.add(new Long(7));
        fileMetadataIds.add(new Long(8));
        batchJdbcDao.getExceptionalItemsCountOfFile(fileMetadataIds);
    }

    @Test
    @Ignore
    public void testGetDisplayableCheckStatuses(){
        List<CheckStatusDto> checkStatusDtos = batchJdbcDao.getDisplayableCheckStatuses();
        Assert.assertTrue("CheckStatusDto's cannot be null", checkStatusDtos != null);
    }
    
    @Test
    @Ignore
    public void testExceptionalReferenceDataInfo(){
        ExceptionalReferenceDataDto exceptionalReferenceDataDto = batchJdbcDao.getExceptionalReferenceDataInfo(new Long(10));
        Assert.assertTrue("ExceptionalReferenceDataInfo's cannot be null", exceptionalReferenceDataDto != null);
        Assert.assertTrue(exceptionalReferenceDataDto.getReferenceDataId().equals(new Long(54254)));
        Assert.assertTrue(exceptionalReferenceDataDto.getExpReferenceDataId().equals(new Long(10)));
    }

    @Test
    @Ignore
    public void testGetAllJobs(){
        List<JobDto> jobDtoList = batchJdbcDao.getAllJobHistory();
        for(JobDto jobDto:jobDtoList){
            System.out.println(jobDto);
        }
    }
    
    @Test
    @Ignore
    public void testGetItemsProcessedCountInFile() {    	
    	SimpleDateFormat dbWALFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");    	
    	try {
			Date startDate = dbWALFormat.parse("2014-07-11 19:44:49");
	    	Date endDate = dbWALFormat.parse("2014-07-11 19:44:49");
			Long itemsProcessedCount = batchJdbcDao.findJobStepNumOfItemsProcessedInFile(startDate, endDate, "CRS_PAID");
			System.out.println(itemsProcessedCount);
		} catch (ParseException e) {			
			e.printStackTrace();
		}
    }
    
    @Test
    @Ignore
    public void testGetErrorsCountInFile() {
    	SimpleDateFormat dbWALFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");    	
    	try {
			Date startDate = dbWALFormat.parse("2014-07-11 19:44:49");
	    	Date endDate = dbWALFormat.parse("2014-07-11 19:44:49");
			Long errorCount = batchJdbcDao.findJobStepNumOfErrorsInFile(startDate, endDate, "CRS_PAID");
	    	System.out.println(errorCount);
		} catch (ParseException e) {			
			e.printStackTrace();
		} 
    }
    
    @Test
    @Ignore
    public void testFindErrorRecordsUploaded() {
    	exceptionalCheckDao.findErrorRecordsUploadedBy(40256L);
    }
    
    @Test
    @Ignore
    public void testFindErrorsInFile(){
    	SimpleDateFormat dbWALFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");    	
    	try {
			Date startDate = dbWALFormat.parse("2014-07-10 19:44:49");
	    	Date endDate = dbWALFormat.parse("2014-07-12 19:44:49");
			List<ItemErrorRecordsDto> itemList = batchJdbcDao.findErrorsInFile(startDate, endDate);
			System.out.println(itemList.size());
		} catch (ParseException e) {			
			e.printStackTrace();
		}
    }
    
    @Test
    @Ignore
    public void filterFilesBy(){
    	Company userCompany = new Company();
    	userCompany.setId(new Long(1));
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR, 2014);
    	calendar.set(Calendar.MONTH, 7);
    	calendar.set(Calendar.DAY_OF_MONTH, 8);
    	List<RecentFileDto> recentFiles = batchJdbcDao.filterFilesBy(userCompany, calendar.getTime(), null, "");
    	for(RecentFileDto recentFile: recentFiles){
    		System.out.println(recentFile.toString());
    	}
    }
    
    @Test
    @Ignore
    public void testFindFileNames() {
    	SimpleDateFormat dbWALFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");    	
    	try {
			Date startDate = dbWALFormat.parse("2014-07-11 19:44:49");
	    	Date endDate = dbWALFormat.parse("2014-07-17 19:44:49");
			List<String> fileNames = batchJdbcDao.findJobStepFileNames(startDate, endDate, "CRS_PAID");
			String fileName = "";
			String prefix = "";
			StringBuilder fileNameBuilder = new StringBuilder();
			
			if(!fileNames.isEmpty()) {
				if(fileNames.size() > 1) {
					for(String name : fileNames){
						fileNameBuilder.append(prefix);
						prefix = ",  ";
						fileNameBuilder.append(name);
					}			
					fileName = fileNameBuilder.toString();					
				} else if(fileNames.size() == 1) {
					fileName = fileNames.get(0);					
				}				
			}			
	    	System.out.println(fileName);
		} catch (ParseException e) {			
			e.printStackTrace();
		} 
    }

    @Test
    public void testGetExceptionalReferenceData() throws Exception{
        List<ExceptionalReferenceDataDto> exceptionalReferenceDataDtos = batchJdbcDao.getExceptionalReferenceData("121212","120089807","STOP");
        System.out.println(exceptionalReferenceDataDtos==null);
    }
}
