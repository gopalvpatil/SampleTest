<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script	src="<%=request.getContextPath()%>/static/positivepay/js/job.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/controllers/ViewJobHistory.js" type="text/javascript"></script>  
<script src="<%=request.getContextPath()%>/static/positivepay/js/controllers/JobController.js" type="text/javascript"></script>    
<script src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js" type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer" ng-controller="ViewJobHistory">
		<div class="panel panel-primary" onload="viewjob()">
			<div class="panel-heading">
				<div class="panel-title">Jobs</div>
			</div>
			<div class="panel-body">
				<form:form method="post" enctype="form-data" modelAttribute="jobDto" class="form-inline" action="operation" role="form" id="viewjobform"> 
					<input type="hidden" id="isViewPage" name="isViewPage" value="true" />
					<div id="info" class="alert alert-success alert-dismissable" hidden="true"></div>
					
					<div id="error" class="alert alert-danger alert-dismissable" hidden="true">
						<button type="button" class="close" data-dismiss="alert">&times;</button>				 	
					</div>
					<span class="pp-ajax-loader {{waitStepHistory ? 'show' : 'hide'}}">Please wait...</span>
					<div class="form-group">
						<div>
							<button id="run" name="Run" value="Run" type="button" class="btn button">
								Run Selected Jobs
							</button>
						</div>
					</div>				
					<br><br>
					<table class="table table-striped table-bordered">					
						<thead>
							<tr>
						    	<th class="col-sm-1" style="width: 1.4%;"><input type="checkbox" id="selectAll" name="selectAll" value="true"/></th>
						    	<th class="col-sm-2">Job Name</th>
						    	<th class="col-sm-1">Status</th>
						    	<th class="col-sm-1">Start Date/Time</th>
						    	<th class="col-sm-1">End Date/Time</th>
						    	<th class="col-sm-1">Schedule Date/Time</th>
						    	<th class="col-sm-1">Last Run</th>
						    	<th class="col-sm-1" style="text-align:center">Edit</th>
						    	<th class="col-sm-1" style="text-align:center">Delete</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var='jobDto' items="${jobDtoList}">
								<c:if test="${jobDto.active}">
									<tr id ="row-${jobDto.jobId}">
								    	<td><input type="checkbox" class="case runCheckbox" id="case" name="jobId" value="${jobDto.jobId}"/></td>
								    	<td class="pp-text-wrap"> <a href="#" ng-click="getJobStepHistory(${jobDto.jobId})">${jobDto.jobName}</a></td>
								    	<td id="statusStaticColumn-${jobDto.jobId}">${jobDto.jobStatusType}</td><td id="statusDynamicColumn-${jobDto.jobId}" style="display: none;"></td>
								    	<td>${jobDto.jobStartDateTime}</td>
								    	<td>${jobDto.jobEndDateTime}</td>
								    	<td id="nextRunDateStaticColumn-${jobDto.jobId}">${jobDto.jobNextRunDate}</td><td id="nextRunDateDynamicColumn-${jobDto.jobId}" style="display: none;"></td>
								    	<td id="lastRunDateStaticColumn-${jobDto.jobId}">${jobDto.jobLastRunDate}</td><td id="lastRunDateDynamicColumn-${jobDto.jobId}" style="display: none;"></td>
								    	<td align="center">					    	
			                                <a href="<%=request.getContextPath()%>/job/editjob?id=${jobDto.jobId}" class=""> 
				                             	<div class="pp-sprite pp-sprite-edit"></div>
			                             	</a>
				                         </td>  			                         	
				                         <td align="center">				                         					                         	
			                         		<a href="#" href1="<%=request.getContextPath()%>/job/deleteJobById?id=${jobDto.jobId}" class="confirm-link">
			                         			<div class="pp-sprite pp-sprite-delete"></div>
			                         		</a>			                         					                         		
			                        	</td>
									</tr>
							  	</c:if>
						   </c:forEach>
						</tbody>						
					</table>				
				</form:form>
			</div>
		</div>
		<!-- Job Step History Modal -->
		<form method="post" enctype="form-data" modelAttribute="jobDto"  action="errors" role="form" id="errorForm">
			<div class="modal fade bs-modal-xs" id="jobStepHistory" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
				<div class="modal-dialog modal-xs" style="margin-left:5%">
			      <div>
			      	<div class="positivepay-column-spacer">
			        	<div class="panel panel-primary">
				            <div class="modal-header">
					            <label class="modal-title">Load Job Step:</label>
								<div style="float: right">
									<button type="button" class="pp-sprite-close-window" data-dismiss="modal" aria-hidden="true"></button>
								</div>
				            </div>         
			                    <div class="panel-body">
			                   		<div class="col-sm-5">
			                   			<div class="form-group">
			                   				<label class="col-sm-5 control-label" for="startDateTime">Start Date-Time:</label>
			                   				<input type="hidden" id="jobActualStartTime" name="jobActualStartTime" value="{{jobActualStartTime}}">
			                   				<input type="hidden" id="timezone" name="timezone" value="{{jobTimezone}}">
			                   				<div class="col-sm-7">
												<label class="col-sm-9 control-label acc-control-label" for="actualStartTime">{{jobActualStartTime}}</label>
											</div>
			                   			</div>
			                   			<div class="form-group">
			                   				<label class="col-sm-6 control-label" for="filesProccessed">No. of Files Proccessed:</label>
			                   				<div class="col-sm-6">
												<label class="col-sm-5 control-label acc-control-label" for="filesProccessedVal">{{jobNumOfFilesProcessed}}</label>
											</div>
			                   			</div>
			                   			<div class="form-group">
			                   				<label class="col-sm-6 control-label" for="itemsProccessed">No. of Items Proccessed:</label>
			                   				<div class="col-sm-6">
			                   					<label class="col-sm-5 control-label" for="itemsProccessedVal">{{jobNumOfItemsProcessed}}</label>
			                   				</div>
			                   			</div>
			                   		</div>
			                   		<div class="col-sm-5">
			                   			<div class="form-group">
			                   				<label class="col-sm-5 control-label acc-control-label" for="endDateTime">End Date-Time:</label>
			                   				<input type="hidden" id="jobActualEndTime" name="jobActualEndTime" value="{{jobActualEndTime}}">
			                   				<div class="col-sm-7">
			                   					<label class="col-sm-10 control-label acc-control-label" for="actualEndTime">{{jobActualEndTime}}</label>
			                   				</div>
			                   			</div>
			                   			<div class="form-group">
			                   				<label class="col-sm-6 control-label" for="filesFailed">No. of Files Failed:</label>
			                   				<div class="col-sm-6">
			                   					<label class="col-sm-5 control-label" for="filesFailedVal">{{jobNumOfFilesFailed}}</label>
			                   				</div>
			                   			</div>
			                   			<div class="form-group">
			                   				<label class="col-sm-6 control-label" for="noOFErrors">No. of Errors:</label>
			                   				<div class="col-sm-6">
			                   					<a id="errorLink" class="col-sm-5" href="#" onclick="document.getElementById('errorForm').submit();">
			                   						{{jobNumOfErrors}}
			                   					</a>
			                   				</div>
			                   			</div>
			                   		</div>
			                   		<div class="col-sm-11">
			                   			<div class="form-group">
			            					<label class="col-sm-2 control-label" for="jobStepSummary">Job Step Summary:</label>
					           				<div id="jobStepSummaryText"class="col-sm-9"></div>
			           					</div>
			                   		</div>
			                    	<br><br>
				                    <table class="table table-striped table-bordered">
				                        <thead>
					                        <tr>
					                            <th class="col-sm-1" style="width: 2.5%;">#</th>
					                            <th class="col-sm-1">File Name</th>
					                            <th class="col-sm-1">Result</th>
					                            <th class="col-sm-2">Item Processed</th>
					                            <th class="col-sm-2">Errors</th>
					                            <th class="col-sm-1">Start Date-Time</th>
					                            <th class="col-sm-1">End Date-Time</th>
					                        </tr>
				                        </thead>
				                        <tbody ng-repeat="jobStepHistory in jobStepHistoryArray">
				                            <tr id="row-{{jobStepHistory.jobStepId}}">
				                                <td class="col-sm-1" style="width: 2.5%;"><div id="showStepComments" ng-click="showStepComments(jobStepHistory.jobStepId)" class="pp-sprite-black-dropdown"></div></td>
				                                <td class="col-sm-1">{{jobStepHistory.jobStepFilename}}</td>
				                                <td class="col-sm-3">{{jobStepHistory.jobStepStatus}}</td>
				                                <td class="col-sm-1">{{jobStepHistory.jobStepNumOfItemsProcessed}}</td>
												<td class="col-sm-1">{{jobStepHistory.jobStepNumOfErrors}}</td>
				                                <td class="col-sm-2">{{jobStepHistory.jobStepActualStartTime}}</td>
				                                <td class="col-sm-2">{{jobStepHistory.jobStepActualEndTime}}</td>
				                            </tr>
				                            <tr id="row-{{jobStepHistory.jobStepId}}" ng-show="toggle[jobStepHistory.jobStepId]">
				                           		<td class="col-sm-1" style="width: 2.5%;"></td>
					                            <td colspan="6">{{jobStepHistory.comments}}</td>
				                            </tr>
				                        </tbody>
				                    </table>
			                    </div>
			        		</div>
			    		</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div class="modal fade bs-modal-xs" id="deleteConfirmModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-xs">
	    <div class="modal-content">
			<div class="modal-header">
			    <label class="modal-title">Confirm Delete?</label>
				<div style="float: right">
				<button type="button" class="pp-sprite-close-window" data-dismiss="modal" aria-hidden="true"></button>
				</div>				   
			</div>
		    <div class="modal-body">
				Do you want to delete this job?
		    </div>
		    <div class="modal-footer">
				<button type="button" class="btn button" data-dismiss="modal">No</button>
				<a href="#" data-id="${deleteConfirmModal.data-id}" class="btn button del-link" role="button">Yes</a>
		    </div>
	    </div>
	</div>
</div>

<div class="modal fade bs-modal-xs" id="NoJobStepHistoryModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-xs">
	    <div class="modal-content">
			<div class="modal-header">
			    <label class="modal-title">Load Job Step:</label>
				<div style="float: right">
				<button type="button" class="pp-sprite-close-window" data-dismiss="modal" aria-hidden="true"></button>
				</div>				   
			</div>
		    <div class="modal-body">
				Job step history is not available because this job has just been created.
		    </div>
		    <div class="modal-footer">
		    </div>
	    </div>
	</div>
</div>