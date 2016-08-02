package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.PermissionDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;

/**
 * @author moumita
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class PermissionJpaDaoTest {
    @Autowired
    private PermissionDao permissionDao;

	@Loggable
	private Logger logger;
	
	   @Before
	    public void setup(){
	        PositivePayThreadLocal.set("gduggira");
	    }
	
	@Test
	@Ignore
	 public void testFindResourcesByUser() {
		List<Permission> resourceList =permissionDao.findResourcesByUser("gduggira"); 
		assertTrue(resourceList.size()>0);

	 }
	
	@Test
	@Ignore
	 public void testFindResourcesByUserAndResourceName() {
		List<Permission> resourceList =permissionDao.findResourcesByUserAndResourceName("gduggira","ADJUST_AMOUNT" ); 
		assertTrue(resourceList.size()>0);
	 }
	

}
