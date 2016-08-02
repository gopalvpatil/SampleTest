package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.SystemMessage;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 12/5/14
 * Time: 11:56 AM
 */
public interface SystemMessageDao extends GenericDao<SystemMessage, Long> {
    List<SystemMessage> getSystemMessages(SystemMessage.TYPE type);
    void deleteByType(SystemMessage.TYPE type);
}
