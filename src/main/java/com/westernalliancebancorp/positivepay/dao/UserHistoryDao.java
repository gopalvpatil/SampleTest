package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.UserHistory;

/**
 * UserHistoryDao is
 *
 * @author Giridhar Duggirala
 */

public interface UserHistoryDao extends  GenericDao<UserHistory, Long> {
	/**
	 * This method will fetch user activity history by user id
	 * @param userId
	 * @return
	 */
	List<UserHistory> getUserDetailHistoryBy(Long userId, Integer startIndex, Integer maxResult);
}
