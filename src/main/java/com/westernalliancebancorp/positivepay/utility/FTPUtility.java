package com.westernalliancebancorp.positivepay.utility;

import com.westernalliancebancorp.positivepay.log.Loggable;
import it.sauronsoftware.ftp4j.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTPUtility is
 *
 * @author Giridhar Duggirala
 */

public class FTPUtility {
    @Loggable
    private static Logger logger = LoggerFactory.getLogger(FTPUtility.class);

    public static FTPClient connect(String ipAddress, String userName, String password) {
        FTPClient client = new FTPClient();
        logger.debug("Connecting to " + ipAddress + " as " + userName + "/" + password);
        try {
            client.setType(FTPClient.TYPE_BINARY);
            client.connect(ipAddress);
            client.login(userName, password);
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
