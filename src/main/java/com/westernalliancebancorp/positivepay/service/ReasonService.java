package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.Reason;
/**
 * Interface providing service methods to work with the Reason Model
 * @author Anand Kumar
 */
public interface ReasonService {
	Reason update(Reason reason);
	Reason save(Reason reason);
	void delete(Reason reason);
	Reason findById(Long id);
	List<Reason> findAll();
	List<Reason> findAllActiveReasons(boolean isPay);
}
