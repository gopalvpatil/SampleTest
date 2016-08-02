package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.DecisionWindow;
/**
 * Interface providing service methods to work with the DecisionWindow Model
 * @author Anand Kumar
 */
public interface DecisionWindowService {
	DecisionWindow update(DecisionWindow decisionWindow);
	DecisionWindow save(DecisionWindow decisionWindow);
	void delete(DecisionWindow decisionWindow);
	DecisionWindow findById(Long id);
	List<DecisionWindow> findAll();
	List<DecisionWindow> saveAll(List<DecisionWindow> decisionWindows);
	void updateCompanyWithDecisionWindow(Long compId, DecisionWindow decisionWindow);
	boolean isWithinDecisionWindow(DecisionWindow decisionWindow);
}
