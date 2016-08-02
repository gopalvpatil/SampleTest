package com.westernalliancebancorp.positivepay.job;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.FtpPollingService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * FtpPollingTask is
 *
 * @author Giridhar Duggirala
 */

@Component
public class FtpPollingTask {
    @Loggable
    private Logger logger;
    @Autowired
    FtpPollingService ftpPollingService;
}
