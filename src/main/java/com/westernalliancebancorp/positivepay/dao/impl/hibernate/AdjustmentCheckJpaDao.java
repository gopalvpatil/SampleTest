package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.AdjustmentCheckDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Set;

/**
 * AccountJpaDao is
 * 
 * @author Giridhar Duggirala
 */
@Repository("adjustmentCheckDao")
public class AdjustmentCheckJpaDao extends GenericJpaDao<AdjustmentCheck, Long> implements
        AdjustmentCheckDao {

}
