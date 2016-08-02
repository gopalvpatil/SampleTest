package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Action;

/**
 * ActionDao is
 *
 * @author Giridhar Duggirala
 */

public interface ActionDao extends GenericDao<Action, Long> {
    Action findByNameAndVersion(String actionName, Integer version, Action.ACTION_TYPE action_type);
    List<Action> findAllAdminActions();
}
