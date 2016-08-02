package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.CheckLinkage;
import com.westernalliancebancorp.positivepay.model.LinkageType;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 4/4/14
 * Time: 4:59 PM
 */
public interface LinkageTypeDao extends GenericDao<LinkageType, Long> {
    LinkageType findByName(LinkageType.NAME name);
}
