package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.service.WorkflowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ReferenceData;

import java.util.Map;

/**
 * This class will be called for certain actions when you think that the reference data will be updated for those actions.
 * Once the reference data is is set to check then the map entry will made null.
 * User:	Gopal Patil
 * Date:	Mar 25, 2014
 * Time:	5:01:24 PM
 */
@Service("referenceDataStatusUpdate")
public class ReferenceDataStatusUpdate implements PreActionCallback, PostActionCallback {

    @Autowired
    ReferenceDataDao referenceDataDao;

    @Autowired
    CheckDao checkDao;

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.workflow.PreActionCallback#executePreActionCallback(com.westernalliancebancorp.positivepay.workflow.CallbackContext)
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean executePreActionCallback(CallbackContext callbackContext) {
        return executePostActionCallback(callbackContext);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executePostActionCallback(CallbackContext callbackContext) {
        Check check = callbackContext.getCheck();
        ReferenceData referenceData = null;
        //There are two way we can get the reference data.
        //1. Background jobs which will execute a JDBC and populated the reference data Id in CheckDto object.
        //2. Status callbacks or post actions and pre action which will populate either Reference_data or Reference_id.
        Map<String, Object> map = callbackContext.getUserData();
        if (map.get(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_ID.name()) != null) {
            referenceData = referenceDataDao.findById(((Long) map.get(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_ID.name())));
        } else if (map.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_DTO.name()) != null) {
            referenceData = referenceDataDao.findById(((CheckDto) map.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_DTO.name())).getReferenceDataId());
            map.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_DTO.name(), null);
        } else if (map.get(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name()) != null) {
            referenceData = (ReferenceData) map.get(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name());
            map.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), null);
        } else if (map.get(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name()) != null &&
                (Boolean) map.get(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name())) {
            //Incase we are nullyfying the referencedata of the existing check then we have to set the existing reference data status to "Not Processed"
            map.put(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name(), null);
          //Incase we are nullyfying the referencedata of the existing check but dont want to change the existing reference data status to "Not Processed",MARK_PROCESSED should be set
            if (map.get(WorkflowService.STANDARD_MAP_KEYS.MARK_PROCESSED.name()) == null)
            {	
            	 ReferenceData referenceData1 = check.getReferenceData();	
            	 referenceData1.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
            	 referenceDataDao.update(referenceData1);
            }
            else if ((Boolean) map.get(WorkflowService.STANDARD_MAP_KEYS.MARK_PROCESSED.name()))
            {
            	//Do nothing,the status should not be changed to NOT_PROCESSED
            	 map.put(WorkflowService.STANDARD_MAP_KEYS.MARK_PROCESSED.name(), null);
            }
            check.setReferenceData(null);
        }
        if (referenceData != null) {
            referenceData.setStatus(ReferenceData.STATUS.PROCESSED);
            referenceDataDao.update(referenceData);
            check.setReferenceData(referenceData);
        }
        checkDao.update(check);
        return Boolean.TRUE;
    }
}
