package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.dto.CheckHistoryDto;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
/**
 * Interface providing service methods to work with the CheckHistory Model
 * @author Anand Kumar
 */
public interface CheckHistoryService {
	CheckHistory update(CheckHistory checkHistory);
	CheckHistory save(CheckHistory checkHistory);
	void delete(CheckHistory checkHistory);
	CheckHistory findById(Long id);
	List<CheckHistory> findAll();
	List<CheckHistory> saveAll(List<CheckHistory> checkHistoryList);
	List<CheckHistoryDto> findAllByCheckId(Long checkId);
}
