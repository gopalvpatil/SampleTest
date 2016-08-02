package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;

import java.util.List;

/**
 * CheckStatusDao is
 *
 * @author Giridhar Duggirala
 */

public interface CheckStatusDao extends GenericDao<CheckStatus, Long> {
    CheckStatus findByNameAndVersion(String statusName, Integer version);
    List<CheckStatus> findByName(String statusName);
}
