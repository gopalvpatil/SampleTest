package com.westernalliancebancorp.positivepay.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.DelimiterDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Delimiter;
import com.westernalliancebancorp.positivepay.service.DelimiterService;

/**
 * providing implementation for service methods to work with the Delimiter Model.
 * @author Anand Kumar
 */
@Service
public class DelimiterServiceImpl implements DelimiterService {

	/** The logger object */
	@Loggable
	private Logger logger;
	
	/** The DelimiterDao dependency */
	@Autowired
	private DelimiterDao delimiterDao;
	
	@Autowired
	private UserDetailDao userDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Delimiter update(Delimiter delimiter) {
		return delimiterDao.update(delimiter);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Delimiter save(Delimiter delimiter) {
		return delimiterDao.save(delimiter);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Delimiter delimiter) {
		delimiterDao.delete(delimiter);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Delimiter findById(Long id) {
		return delimiterDao.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Delimiter> findAll() {
		return delimiterDao.findAll();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Delimiter> saveAll(List<Delimiter> delimiters) {
		return delimiterDao.saveAll(delimiters);
	}

}
