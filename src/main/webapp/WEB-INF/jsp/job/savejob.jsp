<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script	src="<%= request.getContextPath() %>/static/positivepay/js/job.js" type="text/javascript"></script>
<script src="<%= request.getContextPath() %>/static/positivepay/js/controllers/JobController.js" type="text/javascript"></script>
<script src="<%= request.getContextPath() %>/static/positivepay/js/angular/angular.min.js" type="text/javascript"></script>   
<style>
	input.ng-invalid.ng-dirty {
		border: 1px solid red;
	}
	
	input.ng-valid.ng-dirty {
		border: 1px solid green;
	}
	
	.error-message {
		color: #E44848;
	}
	
	.footer {
		margin-top:-70px;
	}
</style>

<script>
 var jobsType = '${jobsType}';
 var jobSteps = '${jobSteps}';
</script>

<div class="row">
	<div class="positivepay-column-spacer" ng-controller="JobController">
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
			
			<div class="panel-body" id="pb">
				<form:form id="savejob-form" enctype="form-data" name="jobform"
					modelAttribute="jobDto" class="form-horizontal" commandName="jobDto"
					role="form" method="post" action="cancel">
		
					<div id="userCreateInfo" style="display: none"></div>
					<div id="userCreateError" style="display: none"></div>
		
					<c:if test="${not empty errors}">
						<div class="alert alert-danger alert-dismissable">
							<button type="button" class="close" data-dismiss="alert">&times;</button>
							<i class="icon-fixed-width icon-remove-sign"></i>
							<form:errors path="jobTypeId" />
						</div>
					</c:if>
		
					<input type="hidden" id="edit" name="edit" value="${edit}" />
					<div id="info" class="alert alert-success alert-dismissable"
						style="display: none">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<span>Jobs Created Successfully</span>
					</div>
		
					<div id="error" class="alert alert-danger alert-dismissable"
						style="display: none">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<span data-dismiss="alert">Jobs Creation Failed</span>
					</div>
					
					<div id="editError" class="alert alert-danger alert-dismissable"
						style="display: none">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<span data-dismiss="alert"></span>
					</div>
		
					<div class="form-group">
						<label for="jobName" class="col-sm-2 control-label">Job
							Name</label>
						<div class="col-sm-5">
							<p class="form-control-static" ng-model="newjob.jobName">${jobDto.jobName}
							</p>
						</div>
					</div>
		
					<div class="form-group">
						<label class="col-sm-2 control-label">Select Job Type</label> 
						<label class="float: left;" ng-repeat="c in radios"> 
							<label class="clear:both;"> &nbsp;&nbsp;&nbsp;
								<input type="radio" name="custom_type"  id="{{c.id}}" ng-model="options.value" value="{{c.id}}"
										ng-change="populateCombo(options.value)" /> {{c.name}}
							</label>
						</label>
					</div>
		
					<div class="form-group">
						<label for="jobStepName" class="col-sm-2 control-label">
							Step Name
						</label>
						<div class="col-lg-8">
							<input type="text" class="form-control" id="jobStepName" name="jobStepName" 
								ng-model="jobStepName" placeholder="Job Step Name" required
								<c:if test="${not empty edit}">value="${jobDto.jobStepName}"</c:if>/>
						</div>
						<span
							ng-show="jobform.jobStepName.$dirty && jobform.jobStepName.$error.required"
							class="error-message">This is a required field</span>
					</div>

					<div class="form-group">
						<label for="jobStepDescription" class="col-sm-2 control-label">Step
							Description</label>
						<div class="col-lg-8">
							<textarea class="form-control" id="jobStepDescription"
								ng-model="jobStepDesc" name="jobStepDescription"
								placeholder="Job Step Description" rows="3"><c:if test="${not empty errors || not empty edit}">${jobDto.jobStepDescription}</c:if></textarea>
						</div>
					</div>

					<div class="form-group">
						<label for="jobActionType" class="col-sm-2 control-label">Action
							Type</label>
						<div class="col-lg-8">
							<select class="pp-width-full" ng-model="jobActionType" id="actionType"
								ng-options="jobActionType.name for jobActionType in combosubarray">
								<option value="">Select</option>
							</select>
						</div>
					</div>
		
					<div class="form-group">
						<label for="jobActionType" class="col-sm-2 control-label">Filter
							Criteria</label>
						<div class="col-sm-1">
							<p class="form-control-static">Bank</p>
						</div>
						<div class="col-sm-2">
							<select class="pp-width-full" id="isOneOf" name="isOneOf">
								<option>is one of</option>
							</select>
						</div>
						<div class="col-lg-6">
							<select class="pp-width-full" name="bank" ng-model="bank"
								ng-options="bank.name for bank in banks | orderBy:'name'"
								ng-change="getCompaniesByBank(jobform.bank)">
								<option value="">Select Bank</option>
							</select>
						</div>
					</div>
		
					<div class="form-group">
						<label for="jobActionType" class="col-sm-2 control-label"></label>
						<div class="col-sm-1">
							<p class="form-control-static">Customer</p>
						</div>
						<div class="col-sm-2">
							<select class="pp-width-full" id="isOneOf" name="isOneOf">
								<option>is one of</option>
							</select>
						</div>
						<div class="col-lg-6">
							<select class="pp-width-full" name="comp" ng-model="comp"
								ng-options="company.companyName for company in fiteredCompany | orderBy:'companyName'"
								ng-change="getAccountsByCompany(jobform.comp)">
								<option value="">Select Company</option>
							</select>
						</div>
					</div>
		
					<div class="form-group">
						<label for="jobActionType" class="col-sm-2 control-label"></label>
						<div class="col-sm-1">
							<p class="form-control-static">Account No.</p>
						</div>
						<div class="col-sm-2">
							<select class="pp-width-full" id="isOneOf" name="isOneOf">
								<option>is one of</option>
							</select>
						</div>
						<div class="col-lg-6">
							<select class="pp-width-full" name="accnt" ng-model="accnt"
								ng-options="accnt for accnt in accounts">
								<option value="">Select Account</option>
							</select>
						</div>
					</div>		
					<hr>		
					<div class="form-group">
						<ol>
							<li>Define Time Period to Run Job</li>
							<li ng-repeat="j in jobs">{{j.jobStepName}}</li>
						</ol>
					</div>
					<br>
		
					<div class="form-actions" align="right">
						<input type="hidden" ng-model="clickcount" />
						<button type="submit" class="btn button" name="action" value="cancel">Back</button>						
						<button type="button" ng-show="${edit}" class="btn button" ng-click="nextEdit()">Next</button>						
						<button type="button" ng-disabled="jobform.$invalid" class="btn button" ng-click="appendJobInfo()">Add Another Step</button>
					</div>
					<hr>		
					<input type="hidden" id="jobName" name="jobName" value="${continueJobDto.jobName}" />
					<input type="hidden" id="jobDescription" name="jobDescription" value="${continueJobDto.jobDescription}" />
					<input type="hidden" id="jobFrequency" name="jobFrequency" value="${continueJobDto.jobFrequency}" />
					<input type="hidden" id="jobStartDate" name="jobStartDate" value="${continueJobDto.jobStartDate}" />
					<input type="hidden" id="jobEndDate" name="jobEndDate" value="${continueJobDto.jobEndDate}" />
					<input type="hidden" id="indefinitely" name="indefinitely" value="${continueJobDto.indefinitely}" />
					<input type="hidden" id="weekly" name="weekly" value="${continueJobDto.weekly}" />
					<input type="hidden" id="jobRunDay" name="jobRunDay" value="${continueJobDto.jobRunDay}" />
					<input type="hidden" id="jobRunTime" name="jobRunTime" value="${continueJobDto.jobRunTime}" />
					<input type="hidden" id="jobEndRunTime" name="jobEndRunTime" value="${continueJobDto.jobEndRunTime}" />
					<input type="hidden" id="timezone" name="timezone" value="${continueJobDto.timezone}" />	
					<input type="hidden" id="intervalTime" name="intervalTime" value="${continueJobDto.intervalTime}" />
					<input type="hidden" id="savePage" name="savePage" value="true" />
					<input type="hidden" id="thresholdTime" name="thresholdTime" value="1000" />		
					<c:if test="${not empty edit}">
						<input type="hidden" id="id" name="id" value="${continueJobDto.jobId}" />
						<input type="hidden" id="createdBy" name="createdBy" value="${jobDto.createdBy}" />
						<input type="hidden" id="dateCreated" name="dateCreated" value="${jobDto.dateCreated}" />
						<input type="hidden" id="olderStartDateTime" name="olderStartDateTime" value="${jobDto.olderStartDateTime}"/>
					</c:if>
		
					<div class="form-actions" align="right">
						<button type="button" class="btn button" name="action"value="cancel" onclick="location.href='<%= request.getContextPath() %>/job/cancelcontinuejob'">
							Cancel
						</button>
						<button type="button" class="btn button" value="save" ng-click="submitArrayOfJobs()" ng-disabled="jobform.$invalid">Save And Complete</button>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
