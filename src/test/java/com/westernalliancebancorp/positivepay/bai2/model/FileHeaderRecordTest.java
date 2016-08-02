package com.westernalliancebancorp.positivepay.bai2.model;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * FileHeaderRecordTest is
 *
 * @author Giridhar Duggirala
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class FileHeaderRecordTest {
    @Ignore
    @Test
    public void testProcess() throws Exception {
        Map<Integer, String> testMap = new HashMap<Integer, String>();
        testMap.put(0, "01,122099999,123456789,940621,0200,1,55,,2/");
        FileHeaderRecord fileHeaderRecord = new FileHeaderRecord();
        fileHeaderRecord.process(testMap, 0);
    }

}
