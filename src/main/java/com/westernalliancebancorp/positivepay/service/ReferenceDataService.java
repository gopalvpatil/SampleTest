package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.ReferenceData;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface providing service methods to working with reference data model.
 * @author Anand Kumar
 */
public interface ReferenceDataService {
	ReferenceData update(ReferenceData referenceData);
	ReferenceData save(ReferenceData referenceData);
	void delete(ReferenceData referenceData);
	ReferenceData findById(Long id);
	List<ReferenceData> findAll();
	List<ReferenceData> saveAll(List<ReferenceData> referenceDataList);

    @Transactional(propagation = Propagation.REQUIRED)
    ReferenceData correctTheReferenceData(Long referenceDataId, String checkNumber, String accountNumber);

    @Transactional(propagation = Propagation.REQUIRED)
    ReferenceData correctCheckNumber(Long referenceDataId, String checkNumber);

    @Transactional(propagation = Propagation.REQUIRED)
    ReferenceData correctAccountNumber(Long referenceDataId, String accountNumber);
}
