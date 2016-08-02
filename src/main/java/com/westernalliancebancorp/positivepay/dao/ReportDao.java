package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Report;

public interface ReportDao extends GenericDao<Report, Long> {
    List<Report> findByUserName(String userName);
}
