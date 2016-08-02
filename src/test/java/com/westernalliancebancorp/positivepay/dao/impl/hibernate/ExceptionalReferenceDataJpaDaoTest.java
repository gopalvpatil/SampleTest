package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;

/**
 * @author moumita
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:positivepay-test-context.xml" })
public class ExceptionalReferenceDataJpaDaoTest {

	@Autowired
	private ExceptionalReferenceDataDao exceptionalReferenceDataDao;
    @Autowired
    private ExceptionTypeService exceptionTypeService;
	@Loggable
	private Logger logger;

	@Before
	public void setup() {
		PositivePayThreadLocal.set("gduggira");
	}

	@Test
	@Ignore
	public void testFindAllExceptionalChecks() {
		List<com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData> expExpRefDataList = exceptionalReferenceDataDao
				.findAllExceptionalReferenceDataByExceptionTypeId(1l);
		assertTrue(expExpRefDataList.size() > 0);
	}

	@Test
	@Ignore
	public void testFindAllExceptionalChecksForStatus() {
		List<com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData> expExpRefDataList = exceptionalReferenceDataDao
				.findAllExceptionalReferenceDataByExceptionTypeId(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicatePaidItemException).getId());
		assertTrue(expExpRefDataList.size() > 0);
	}

}
