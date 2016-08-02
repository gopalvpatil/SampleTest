package com.westernalliancebancorp.positivepay.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.EmailService;

import freemarker.template.Configuration;

/**
 * EmailServiceImpl is
 *
 * @author Giridhar Duggirala
 */
@Service(value = "emailService")
public class EmailServiceImpl implements EmailService {

    @Autowired
    private Configuration freemarkerMailConfiguration;

    /** The logger object */
    @Loggable
    private Logger logger;

    @Autowired
    private JavaMailSender javaMailSender;
    

    @Value("${positivepay.email.from.address}")
    private String fromAddress;

    @Override
    public boolean sendEmail(final String templateName, final Map<String, Object> emailData, final List<String> toAddress, final List<String> ccAddress, final boolean html, final String fromEmail,final String subject) {
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(javax.mail.internet.MimeMessage mimeMessage) throws Exception {
                if ((toAddress == null || toAddress.size() <= 0) && (ccAddress == null || ccAddress.size() <= 0)) {
                    throw new RuntimeException("To and CC Address list is null, please check.");
                }
                Address toAddresses[] = new Address[toAddress.size()];
                int i = 0;
                for (String to : toAddress) {
                    toAddresses[i] = new InternetAddress(to);
                    i++;
                }
                mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
                if (ccAddress != null && ccAddress.size() > 0) {
                    i = 0;
                    Address ccAddresses[] = new Address[ccAddress.size()];
                    for (String cc : ccAddress) {
                        ccAddresses[i] = new InternetAddress(cc);
                    }
                    mimeMessage.addRecipients(Message.RecipientType.CC, ccAddresses);
                    mimeMessage.setSubject(subject);

                }
               // freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/");
                String messageText = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate(templateName), emailData);
                logger.info("Message text"+messageText);
                mimeMessage.setFrom(fromEmail == null ? new InternetAddress(fromAddress) : new InternetAddress(fromEmail));
                mimeMessage.setContent(messageText, "text/html");
                
            }
        };
        javaMailSender.send(mimeMessagePreparator);
        return Boolean.TRUE;
    }

    @Override
    public boolean sendEmail(final String templateName, final Map<String, Object> emailData, final List<String> toAddress, final List<String> ccAddress, final boolean html, final String fromEmail, final String attachmentLocation, final String attachmentFileName,final String subject) {
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                if ((toAddress == null || toAddress.size() <= 0) && (ccAddress == null || ccAddress.size() <= 0)) {
                    throw new RuntimeException("To and CC Address list is null, please check.");
                }
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, Boolean.TRUE);
                Address toAddresses[] = new Address[toAddress.size()];
                int i = 0;
                for (String to : toAddress) {
                    toAddresses[i] = new InternetAddress(to);
                    i++;
                }
                mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
                if (ccAddress != null && ccAddress.size() > 0) {
                    i = 0;
                    Address ccAddresses[] = new Address[ccAddress.size()];
                    for (String cc : ccAddress) {
                        ccAddresses[i] = new InternetAddress(cc);
                    }
                    mimeMessage.addRecipients(Message.RecipientType.CC, ccAddresses);
                }
                mimeMessage.setFrom(fromEmail == null ? new InternetAddress(fromAddress) : new InternetAddress(fromEmail));
                String messageText = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate(templateName), emailData);
                logger.info("Message text"+messageText);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(messageText,true);
                
                FileSystemResource file = new FileSystemResource(attachmentLocation+attachmentFileName);
                mimeMessageHelper.addAttachment(file.getFilename(), file);
            }
        };
        javaMailSender.send(mimeMessagePreparator);
        return Boolean.TRUE;
    }
    
   
}
