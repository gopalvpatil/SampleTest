package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dto.CheckHistoryDto;
import com.westernalliancebancorp.positivepay.dto.CheckHistoryDtoBuilder;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.service.CheckHistoryService;

/**
 * providing implementation for service methods to work with the CheckHistory Model.
 * @author Anand Kumar
 */
@Service
public class CheckHistoryServiceImpl implements CheckHistoryService {

	/** The logger object */
	@Loggable
	private Logger logger;
	
	/** The CheckHistoryDao dependency */
	@Autowired
	private CheckHistoryDao checkHistoryDao;
	
	@Autowired
	private UserDetailDao userDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public CheckHistory update(CheckHistory checkHistory) {
		return checkHistoryDao.update(checkHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public CheckHistory save(CheckHistory checkHistory) {
		return checkHistoryDao.save(checkHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(CheckHistory checkHistory) {
		checkHistoryDao.delete(checkHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public CheckHistory findById(Long id) {
		return checkHistoryDao.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<CheckHistory> findAll() {
		return checkHistoryDao.findAll();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<CheckHistory> saveAll(List<CheckHistory> checkHistoryList) {
		return checkHistoryDao.saveAll(checkHistoryList);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<CheckHistoryDto> findAllByCheckId(Long checkId) {
		List<CheckHistory> checkHistoryList = checkHistoryDao.findByCheckId(checkId);
		List<CheckHistoryDto> checkHistoryDtoList = new ArrayList<CheckHistoryDto>();
		for(CheckHistory history: checkHistoryList) {
			CheckHistoryDtoBuilder dtoBuilder = new CheckHistoryDtoBuilder();
			CheckHistoryDto dto = dtoBuilder.modelToDto(history);
			checkHistoryDtoList.add(dto);
		}
		return checkHistoryDtoList;
	}

}
