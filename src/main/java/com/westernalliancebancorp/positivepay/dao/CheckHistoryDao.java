package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.CheckHistory;

/**
 * CheckHistoryDao is
 *
 * @author Giridhar Duggirala
 */

public interface CheckHistoryDao  extends GenericDao<CheckHistory, Long> {
	List<CheckHistory> findByCheckId(Long checkId);
	List<CheckHistory> findByCheckIdandStatusId(Long checkId, Long statusId);

    List<CheckHistory> findByCheckIdWithCheckStatus(Long checkId);
    List<CheckHistory> findOrderedCheckHistory(Long checkId);
}
