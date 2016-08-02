package com.westernalliancebancorp.positivepay.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.ReasonDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Reason;
import com.westernalliancebancorp.positivepay.service.ReasonService;

/**
 * providing implementation for service methods to work with the Reason Model.
 * @author Anand Kumar
 */
@Service
public class ReasonServiceImpl implements ReasonService {

	@Loggable
	private Logger logger;
	
	@Autowired
	private ReasonDao reasonDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Reason update(Reason reason) {
		return reasonDao.update(reason);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Reason save(Reason reason) {
		return reasonDao.save(reason);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Reason reason) {
		reasonDao.delete(reason);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Reason findById(Long id) {
		return reasonDao.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Reason> findAll() {
		return reasonDao.findAll();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Reason> findAllActiveReasons(boolean isPay) {
		return reasonDao.findAllActiveReasons(isPay);
	}
}
