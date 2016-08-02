package com.westernalliancebancorp.positivepay.service;

import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.model.SystemMessage;

/**
 * User: gduggirala
 * Date: 12/5/14
 * Time: 11:47 AM
 */
public interface SystemMessageService {
    List<SystemMessage> getSystemMessages(SystemMessage.TYPE type) throws Exception;
    List<SystemMessage> getSystemMessages(SystemMessage.TYPE type,Date startDate, Date endDate);
    SystemMessage setSystemMessage(String message, SystemMessage.TYPE type, Date fromDate, Date toDate, String timezone) throws Exception;
    void deleteAllSystemMessages(SystemMessage.TYPE type);
    List<Long> systemMessageInUserDetailHistory(Long user_detail_id, Long messageId);
}
