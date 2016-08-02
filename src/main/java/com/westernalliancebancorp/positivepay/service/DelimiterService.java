package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.Delimiter;
/**
 * Interface providing service methods to work with the Delimiter Model
 * @author Anand Kumar
 */
public interface DelimiterService {
	Delimiter update(Delimiter delimiter);
	Delimiter save(Delimiter delimiter);
	void delete(Delimiter delimiter);
	Delimiter findById(Long id);
	List<Delimiter> findAll();
	List<Delimiter> saveAll(List<Delimiter> delimiters);	
}
