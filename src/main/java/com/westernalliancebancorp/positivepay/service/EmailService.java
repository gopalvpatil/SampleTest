package com.westernalliancebancorp.positivepay.service;

import java.util.List;
import java.util.Map;

/**
 * EmailService is
 *
 * @author Giridhar Duggirala
 */

public interface EmailService {
    boolean sendEmail(String templateName, Map<String, Object> emailData, List<String> toAddress, List<String> ccAddress, boolean html, String fromEmail,String subject);
    boolean sendEmail(String templateName, Map<String, Object> emailData, List<String> toAddress, List<String> ccAddress, boolean html, String fromEmail, String attachmentLocation, String attachmentFileName,String subject);
}
