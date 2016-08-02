package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Sso;
import com.westernalliancebancorp.positivepay.model.SsoAttribute;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:41 PM
 */
public interface SsoAttributeDao extends GenericDao<SsoAttribute, Long> {
    List<SsoAttribute> findBySsoId(Long ssoId);
}
