package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Workflow;
import org.springframework.stereotype.Repository;

/**
 * WorkflowDaoImpl is
 *
 * @author Giridhar Duggirala
 */
@Repository
public class WorkflowDaoImpl extends GenericJpaDao<Workflow, Long> implements WorkflowDao {
}
