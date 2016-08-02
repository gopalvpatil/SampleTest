package com.westernalliancebancorp.positivepay.utility;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.log.Loggable;

/**
 * @author Gopal Patil
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class LdapSecurityUtilityTest {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Loggable
	private Logger logger;

	@Test
	@Ignore
	public void testLdapConnection() {
		String[] bnames = applicationContext.getBeanNamesForType(LdapAuthenticationProvider.class);
		LdapAuthenticationProvider provider = (LdapAuthenticationProvider) applicationContext.getBean(bnames[0]);
		long startTime = 0l;
		long stopTime = 0l;
		try {
			startTime = System.currentTimeMillis();
			Authentication auth = provider.authenticate(new UsernamePasswordAuthenticationToken("ppaytestuser", "9009le,123"));
			stopTime = System.currentTimeMillis();
			logger.info("Connection Time in { "+ (stopTime - startTime) + " } ms");			
			logger.info(auth.getPrincipal().toString());	
			SecurityContextHolder.getContext().setAuthentication(auth);		
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
	}

}
