package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/11/14
 * Time: 10:20 PM
 */
public interface JobCriteriaDataDao extends GenericDao<JobCriteriaData, Long> {
    List<JobCriteriaData> findByJobId(Long jobStepId);
}
