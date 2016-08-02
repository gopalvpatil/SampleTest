/**
 *
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckLinkageDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Data access object JPA impl to work with CheckLinkage model database operations.
 *
 * @author Anand Kumar
 */
@Repository
public class CheckLinkageJpaDao extends GenericJpaDao<CheckLinkage, Long> implements CheckLinkageDao {
}
