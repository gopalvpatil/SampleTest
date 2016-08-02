package com.westernalliancebancorp.positivepay.dto;

import com.westernalliancebancorp.positivepay.model.CheckHistory;

public class CheckHistoryDtoBuilder {

	public CheckHistoryDto modelToDto(CheckHistory model) {
		CheckHistoryDto dto = new CheckHistoryDto();
		dto.setUser(model.getAuditInfo().getCreatedBy());
		dto.setResultingStatus(model.getPaymentStatus());
		if(model.getSystemComment() != null)
			dto.setDescription(model.getSystemComment());
		else
			dto.setDescription("");
		dto.setDateTime(model.getAuditInfo().getDateCreated());
		dto.setCreatedMethod(model.getSource());
		/* Fix for JIRA WALPP-249 */
		/*String actionType = model.getAction().getActionType().name();
		if(actionType != null){
			if(actionType.equalsIgnoreCase(Action.ACTION_TYPE.NON_WORK_FLOW_ACTION.name())){
				dto.setCreatedMethod("Action");
			} else if(actionType.equalsIgnoreCase(Action.ACTION_TYPE.WORK_FLOW_ACTION.name())){
				dto.setCreatedMethod("Batch");
			} else {
				dto.setCreatedMethod("Unknown");
			}
		} else {
			dto.setCreatedMethod("N/A");
		}*/
		if(model.getUserComment() != null)
			dto.setComment(model.getUserComment());
		else
			dto.setComment("");
		//dto.setCategory(category);
		return dto;
	}
}
