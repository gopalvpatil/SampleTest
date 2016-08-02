<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/job.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/controllers/JobController.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js" type="text/javascript"></script>

<style>
	.cust-col-sm-10 {
		width: 11% !important;
	}
	
	.cust-margin {
		margin-left: 5%;
	}
	
	.cust-checkbox-inline {
		padding-left: 37px;
	}
</style>
<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">
					<c:choose>
						<c:when test="${not empty edit}">
							Edit A Job
						</c:when>
						<c:otherwise>
							Create A Job
						</c:otherwise>
					</c:choose>		
				</div>
			</div>	
			<div class="panel-body">
			<div id="errorBox" class="alert alert-danger hidden">
	  			<strong>Warning!</strong> Please fix the following errors before you proceed:
	  			<ol id="errors"></ol>
				</div>
				<div id="successBox" class="alert alert-success hidden">
				</div>
				<form:form id="continuejob-form" method="post" enctype="form-data"
					modelAttribute="jobDto" class="form-horizontal" commandName="jobDto" action="savejob"
					role="form" onsubmit="return validateContinueJob()">
					
					<c:if test="${not empty errors}">
						<c:set var="jobNameErrors"><form:errors path="jobName"/></c:set>
						<c:set var="jobStartDateErrors"><form:errors path="jobStartDate"/></c:set>
						<c:set var="jobEndDateErrors"><form:errors path="jobEndDate"/></c:set>	
						<c:set var="jobRunDayErrors"><form:errors path="jobRunDay"/></c:set>	
		    			<div class="alert alert-danger alert-dismissable">
		    				<button type="button" class="close" data-dismiss="alert">&times;</button>
		    				<c:if test="${not empty jobNameErrors}">
		    					<i class="icon-fixed-width icon-remove-sign"></i> <form:errors path="jobName" /><br>
		    				</c:if>	
		    				<c:if test="${not empty jobStartDateErrors}">	    				
		    					<i class="icon-fixed-width icon-remove-sign"></i> <form:errors path="jobStartDate" /><br>
		    				</c:if>		 
		    				<c:if test="${not empty jobEndDateErrors}">   				
		    					<i class="icon-fixed-width icon-remove-sign"></i> <form:errors path="jobEndDate" /><br>
		    				</c:if>
		    				<c:if test="${not empty jobRunDayErrors}">   				
		    					<i class="icon-fixed-width icon-remove-sign"></i> <form:errors path="jobRunDay" /><br>
		    				</c:if>	
		    			</div>	    			
					</c:if>	
					
					<div class="form-group">
						<label for="jobName" class="col-sm-2 control-label">Job Name</label>
						<div class="col-lg-8">
							<input type="text" class="col-lg-8 form-control" id="jobName" name="jobName" 
								placeholder="Job Name" <c:if test="${not empty errors || not empty edit}">value="${jobDto.jobName}"</c:if>/>
						</div>
					</div>

					<div class="form-group">					
						<label for="jobDescription" class="col-sm-2 control-label">Description</label>	
						<div class="col-lg-8">				
							<textarea class="col-lg-8 form-control" id="jobDescription"
								name="jobDescription" rows="3"><c:if test="${not empty errors || not empty edit}">${jobDto.jobDescription}</c:if></textarea>
						</div>
					</div>
							
					<div class="form-group">					
						<label for="jobFrequency" class="col-sm-2 control-label">Frequency</label>	
						<div class="col-sm-3">
							<select id="jobFrequency" name="jobFrequency"></select>
						</div>	
					</div>	
					
					<div class="form-group">	
						<label for="jobStartDate" class="col-sm-2 control-label">Start Date</label>				
						<div id="imageStartDate" class="col-sm-2 date-input">
							<input type="text" class="form-control" id="jobStartDate" name="jobStartDate"
								<c:if test="${not empty errors || not empty edit}">value="${jobDto.jobStartDate}"</c:if>/>										
						</div>
						<div id="endDate">
							<label for="jobEndDate" class="col-sm-1 control-label">End Date</label>	
							<div id="imageEndDate" class="col-sm-2 date-input">
								<input type="text" class="form-control" id="jobEndDate" name="jobEndDate"
									<c:if test="${not empty errors || not empty edit}">value="${jobDto.jobEndDate}"</c:if>/>											
							</div>
						</div>
						<div class="col-sm-2" style="width: auto;" id="interval">
							<select id="intervalTime" name="intervalTime">
								<option value="0">Interval</option>
							</select>
						</div>	
						
						<div class="col-sm-2" style="margin-top: 8px;" id="indifinite">
				  	 		<input type="checkbox" id="indefinitely" name="indefinitely" value="true" <c:if test="${jobDto.indefinitely}">checked="checked"</c:if>>
							<label for="indefinitely">Indefinitely</label>		
						</div>
					</div>				
					
					<div class="form-group col-sm-12" id="run-on">
						<label class="col-sm-2 control-label">Run On</label>					
						<div class="checkbox-inline cust-checkbox-inline">
							<label>
								<input type="checkbox" id="monday" name="monday" value="MON" <c:if test="${not empty monday}">checked</c:if>>			    
						    	Monday
						  	</label>
						</div>	
						<div class="checkbox-inline">
						  	<label>
						 		<input type="checkbox" id="tuesday" name="tuesday" value="TUE" <c:if test="${not empty tuesday}">checked</c:if>>	
						    	Tuesday
						  	</label>
						</div>	
						<div class="checkbox-inline">
							<label>
								<input type="checkbox" id="wednesday" name="wednesday" value="WED" <c:if test="${not empty wednesday}">checked</c:if>>																				    
						    	Wednesday
						  	</label>
						</div>
						<div class="checkbox-inline">
							<label>
								<input type="checkbox" id="thursday" name="thursday" value="THU" <c:if test="${not empty thursday}">checked</c:if>>												  
						    	Thursday
						  	</label>
						</div>
						<div class="checkbox-inline">
							<label>
								<input type="checkbox" id="friday" name="friday" value="FRI" <c:if test="${not empty friday}">checked</c:if>>						
								Friday
						  	</label>
						</div>
						<div class="checkbox-inline">
						  	<label>
								<input type="checkbox" id="saturday" name="saturday" value="SAT" <c:if test="${not empty saturday}">checked</c:if>>						
						    	Saturday
						  	</label>
						</div>
						<div class="checkbox-inline">
							<label>
							    <input type="checkbox" id="sunday" name="sunday" value="SUN" <c:if test="${not empty sunday}">checked</c:if>>						
						    	Sunday
						  	</label>
						</div>
						<div class="checkbox-inline">
							<label>
							    <input type="checkbox" id="weekly" name="weekly" value="true" <c:if test="${jobDto.weekly}">checked</c:if>> 						
								Weekly
						  	</label>
						</div>
					</div>		
					
					<div class="form-group" id="startTime">					
						<label class="col-sm-2 control-label">Start Time</label>	
						<div class="col-sm-2">
							<select class="pp-width-full" id="jobStartHour" name="jobStartHour"></select>
						</div>
						<div class="col-sm-2">
							<select class="pp-width-full" id="jobStartMinute" name="jobStartMinute"></select>
						</div>		
						<div class="col-sm-2">
							<select class="pp-width-full" id="jobStartMeridiem" name="jobStartMeridiem"></select>
						</div>
						<div class="col-sm-2">
							<select class="pp-width-full" id="timezone" name="timezone">
								<option value="000">Timezone</option>
							</select>
						</div>
					</div>
					
					<div class="form-group" id="endTime">					
						<label class="col-sm-2 control-label">End Time</label>	
						<div class="col-sm-2">
							<select class="pp-width-full" id="jobEndHour" name="jobEndHour"></select>
						</div>
						<div class="col-sm-2">
							<select class="pp-width-full" id="jobEndMinute" name="jobEndMinute"></select>
						</div>		
						<div class="col-sm-2">
							<select class="pp-width-full" id="jobEndMeridiem" name="jobEndMeridiem"></select>
						</div>
					</div>						
						
					<c:if test="${not empty edit}">
						<input type="hidden" id="edit" name="edit" value="${edit}"/>
						<input type="hidden" id="id" name="id" value="${jobDto.jobId}"/>
						<input type="hidden" id="createdBy" name="createdBy" value="${jobDto.createdBy}"/>
						<input type="hidden" id="dateCreated" name="dateCreated" value="${jobDto.dateCreated}"/>
						<input type="hidden" id="olderStartDateTime" name="olderStartDateTime" value="${jobDto.olderStartDateTime}"/>											
					</c:if>
					
					<input type="hidden" id="savePage" name="savePage" value="false" />
					<input type="hidden" id="frequencySelected" name="frequencySelected" value="${jobDto.jobFrequency}"/>
					<input type="hidden" id="intervalTimeSelected" name="intervalTimeSelected" value="${jobDto.intervalTime}"/>						
					<input type="hidden" id="jobStartHourSelected" name="jobStartHourSelected" value="${jobStartHour}"/>
					<input type="hidden" id="jobStartMinuteSelected" name="jobStartMinuteSelected" value="${jobStartMinute}"/>
					<input type="hidden" id="jobStartMeridiemSelected" name="jobStartMeridiemSelected" value="${jobStartMeridiem}"/>
					<input type="hidden" id="timezoneSelected" name="timezoneSelected" value="${jobDto.timezone}"/>
					<input type="hidden" id="jobEndHourSelected" name="jobEndHourSelected" value="${jobEndHour}"/>
					<input type="hidden" id="jobEndMinuteSelected" name="jobEndMinuteSelected" value="${jobEndMinute}"/>
					<input type="hidden" id="jobEndMeridiemSelected" name="jobEndMeridiemSelected" value="${jobEndMeridiem}"/>
							
					<div class="pull-right">					
						<button type="button" class="btn button" name="cancel" onclick="location.href='<%=request.getContextPath()%>/job/cancelcontinuejob'" value="cancel">
							Cancel
						</button>
						<button type="submit" class="btn button" name="action" value="save">
							Save And Continue
						</button>
					</div>	
				</form:form>	
			</div>
		</div>
	</div>
</div>