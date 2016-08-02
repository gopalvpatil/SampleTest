package com.westernalliancebancorp.positivepay.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.westernalliancebancorp.positivepay.log.Loggable;

/**
 * SFTPUtility class is to connect SFTP server.
 *
 * @author Gopal Patil
 */
public class SFTPUtility {
	
    @Loggable
    private static Logger logger = LoggerFactory.getLogger(SFTPUtility.class);
    
    public static Session connect(String host, int port, String userName, String password) throws JSchException {
    	Session session = null;
        logger.debug("Connecting to SFTP server:" + host + " as " + userName + "/" + password);      	
    	JSch jsch = new JSch();
        session = jsch.getSession(userName, host, port);
        session.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        logger.debug("Host connected.");
        return session;
    }
    
    public static void disconnect(ChannelSftp channelSftp, Channel channel, Session session) {
        if (channelSftp != null) {
            channelSftp.exit();
            logger.debug("sftp Channel exited."); 
        }        
        if (channel != null) {
        	channel.disconnect();
        	logger.debug("Channel disconnected.");
        }        
        if ((session != null) && session.isConnected()) {
            session.disconnect();
            logger.debug("Host Session disconnected.");
        }
    }


}
