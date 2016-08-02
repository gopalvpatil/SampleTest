package com.westernalliancebancorp.positivepay.utility;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.JobService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
@Ignore
public class CacheContextTest {

    @Autowired
    CacheManager cacheManager;    
    
    @Autowired
    private JobService jobService;
    
	/** The logger object */
	@Loggable
	private Logger logger;
	
    @Before
    public void setUp() throws Exception {
    	Assert.notNull(cacheManager);
    	Assert.notNull(jobService);
    	Assert.notNull(logger);
    }

    @Test
    public void shouldStartContext() throws Exception {   
        Assert.notNull(cacheManager.getCache("JobStatusType"));
        Assert.notNull(cacheManager.getCache("JobType"));
        Assert.notNull(cacheManager.getCache("CheckStatus"));
        Assert.isNull(cacheManager.getCache("nonExistentCache"));
        
        Cache cache = cacheManager.getCache("JobStatusType");
 
		final String key = "key";
		final String value1 = "value";
		cache.put(key, value1);
		
		Assert.isTrue(cache.get(key).get().equals(value1));
		
		final String key1 = "key1";
		final String key2 = "key2";
		final String value = "value";
		
		final Cache cache1 = cacheManager.getCache("JobStatusType");

		// Put two values in the caches (there's only room for one)
		cache1.put(key1, value);
		cache1.put(key2, value);

		// Make sure the first item that got put in the cache was evicted
		Assert.notNull(cache1.get(key1));		
    }
    
    @Test
    public void observeCaching() throws Exception{    	
    	logger.info("First Start******************************************");
    	final long start1 = System.nanoTime();    	
    	//jobService.findAllJobTypes();    	
    	final long timeTaken1 = System.nanoTime() - start1;    	
    	
    	System.out.printf("First Time Taken: %d ", timeTaken1);
    	logger.info("First End******************************************Second Start");
    	final long start2 = System.nanoTime();
    	//jobService.findAllJobTypes(); 
    	final long timeTaken2 = System.nanoTime() - start2;
    	
    	System.out.printf("Second Time Taken: %d ", timeTaken2);
    	logger.info("Second End******************************************");    	
    }    
    
}