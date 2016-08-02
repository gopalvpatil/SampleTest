package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.UserActivity;

/**
 * UserActivityDao is
 *
 * @author Giridhar Duggirala
 */

public interface UserActivityDao extends GenericDao<UserActivity, Long> {
    UserActivity findByName(String userActivityName);
}
