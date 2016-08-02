package com.westernalliancebancorp.positivepay.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dto.FileUploadResponse;
import com.westernalliancebancorp.positivepay.service.FileUploadService;
import com.westernalliancebancorp.positivepay.service.FtpPollingService;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;

/**
 * User: gduggirala
 * Date: 7/5/14
 * Time: 9:27 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class FtpPollingServiceImplTest {
    @Autowired
    private
    FtpPollingService ftpPollingService;

    FileUploadService fileUploadService;
    FileUploadResponse fileUploadResponse;
    //private FileMetaData fileMetaData;

    @Before
    public void setUp() throws Exception {
        fileUploadService = Mockito.mock(FileUploadService.class);
        fileUploadResponse = Mockito.mock(FileUploadResponse.class);
        //fileMetaData = Mockito.mock(FileMetaData.class);
        Mockito.when(fileUploadService.uploadFile(Mockito.any(PositivePayFtpFile.class), Mockito.any(Long.class))).thenReturn(fileUploadResponse);
        //Mockito.when(fileUploadResponse.getFileMetaData()).thenReturn(fileMetaData);
    }

    /*@Test
    public void testGetFiles() throws Exception {
        ftpPollingService.getFiles();
    }*/

    @Test
    public void testRetrieveAndStoreFiles() throws Exception {
        ftpPollingService.retrieveAndStoreFiles(ftpPollingService.getFiles());
    }
}
