<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script src="<%=request.getContextPath()%>/static/thirdparty/moment/js/moment.min.js" type="text/javascript" ></script>
<script src="<%=request.getContextPath()%>/static/thirdparty/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js" type="text/javascript" ></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/bootstrap/js/jstz.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/system.js" type="text/javascript" ></script>



<script type="text/javascript">
    $(function () {
        $('#dateFromSystemMessage').datetimepicker({ sideBySide: true });
        $('#dateToSystemMessage').datetimepicker({ sideBySide: true });
        $('#dateFromMaintenanceMessage').datetimepicker({ sideBySide: true });
        $('#dateToMaintenanceMessage').datetimepicker({ sideBySide: true });        
    });


</script>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Set System Message</div>
			</div>
			
			<div class="panel-body">
				<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
					<form:form id="formSystemMessageClear" method="POST" role="form" class="form-horizontal" action="${pageContext.request.contextPath}/messages/clearSystemMessage" target="_blank">	
	                	<div>
	                		<label id="labelSystemMessageCurrent" for="textSystemMessageCurrent">Current Message</label>
	                	</div>
	                	<div class="pp-margin-bottom-small">
	                		<textarea id="textSystemMessageCurrent" class="form-control textarea-vertical-resize" type="textArea" rows="4" disabled></textarea>
	                	</div>
	                	<div>
	                		<label id="labelSystemMessageValid"></label>
	                	</div>	                	
	                	<div class="pp-margin-bottom-small pull-right">
	                		<button id="buttonSystemMessageClear" type="submit" class="clearfix button">Clear Message</button>
	                	</div>
	                </form:form>
                </div>
                <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                	<form:form id="formSystemMessageUpdate" method="POST" role="form" class="form-horizontal" action="${pageContext.request.contextPath}/messages/saveSystemMessage" target="_blank">	
	                	<div>
	                		<label id="labelSystemMessageUpdate" for="textSystemMessageUpdate">Update Message</label>
	                	</div> 
	                	<div class="pp-margin-bottom-small">
	                		<textarea id="textSystemMessageUpdate" name="message" class="form-control textarea-vertical-resize" type="textArea" rows="2"></textarea>
	                	</div>
	                	<div class="pp-margin-bottom-small">
                			<label for="dateFromSystemMessage">From</label>
                			<input type="text" id="dateFromSystemMessage" name="fromDate" class="input-append date"/>
                			<label for="dateToSystemMessage">To</label>
                			<input type="text" id="dateToSystemMessage" name="toDate" class="input-append date"/>
                			<input type="hidden" id="logintimezone" name="logintimezone" value=""/>
	                	</div>
	                	<div class="pp-margin-bottom-small">
		                	<div class="pull-right">
		                		<button id="buttonSystemMessageUpdate" type="submit" class="clearfix button">Update Message</button>
		                	</div>
		                </div>
	                </form:form>
                </div>
			</div>
		</div>
		
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Set System Maintenance Message</div>
			</div>
			
			<div class="panel-body">
				<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
					<form:form id="formMaintenanceMessageClear" method="POST" role="form" class="form-horizontal" action="${pageContext.request.contextPath}/messages/clearMaintenanceMessage" target="_blank">
	                	<div>
	                		<label id="labelMaintenanceMessageCurrent" for="textMaintenanceMessageCurrent">Current Message</label>
	                	</div>
	                	<div>
	                		<textarea id="textMaintenanceMessageCurrent" class="form-control textarea-vertical-resize" type="textArea" rows="4" disabled></textarea>
	                	</div>
	                	<div>
	                		<label id="labelMaintenanceMessageValid"></label>
	                	</div>	                	
	                	<div class="pull-right">
	                		<button id="buttonMaintenanceMessageClear" type="submit" class="clearfix button">Clear Message</button>
	                	</div>
                	</form:form>
                </div>
                <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
                	<form:form id="formMaintenanceMessageUpdate" method="POST" role="form" class="form-horizontal" action="${pageContext.request.contextPath}/messages/saveMaintenanceMessage" target="_blank">	
	                	<div>
	                		<label id="labelMaintenanceMessageUpdate" for="textMaintenanceMessageUpdate">Update Message</label>
	                	</div> 
	                	<div class="pp-margin-bottom-small">
	                		<textarea id="textMaintenanceMessageUpdate" name="message" class="form-control textarea-vertical-resize" type="textArea" rows="2"></textarea>
	                	</div>
	                	<div class="pp-margin-bottom-small">
                			<label for="dateFromMaintenanceMessage">From</label>
                			<input type="text" id="dateFromMaintenanceMessage" name="fromDate"/>
                			<label for="dateToMaintenanceMessage">To</label>
                			<input type="text" id="dateToMaintenanceMessage" name="toDate"/>	
                			<input type="hidden" id="systimezone" name="systimezone" value=""/>
	                	</div>
	                	<div class="pp-margincontrol-group">
		                	<div class="pull-right">
		                		<button id="buttonMaintenanceMessageUpdate" type="submit" class="clearfix button">Update Message</button>
		                	</div>
		                </div>
	                </form:form>
                </div>
			</div>
		</div>
	</div>
</div>

<!-- Decision Window UI -->
<script	src="<%=request.getContextPath()%>/static/positivepay/js/job.js" type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/controllers/DecisionWindowController.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js"
	type="text/javascript"></script>
<div ng-controller="DecisionWindowController">
	<form name="decisionWindowForm">
    	<div class="positivepay-column-spacer">
        	<div class="panel panel-primary">
	            <div class="panel-heading">
	                <div class="panel-title">Set Decision Window</div>
	            </div>
           		<div class="panel-body" id="pb">
					<span id="show"> </span>
					<!-- Start of FORM -->
					<div id="userCreateInfo" style="display: none"></div>
					<div id="userCreateError" style="display: none"></div>
					<div class="col-sm-1 pp-width-full">
						<div class="pp-width-full pull-left">
							<div class="pull-left" style="width: 25%">
								<label for="startDate"
									class="control-label pull-left pp-width-full" style="margin-top:5px;">Start</label>
                            </div>
                           	<div class="pull-left" style="width: 25%">
								<label for="companyNameSearchCriteria"
									class="control-label pull-left pp-width-full" style="margin-top:5px;">End</label>
                            </div>
                            <div class="pull-left" style="width: 15%">
								<label for="companyNameSearchCriteria"
									class="control-label pull-left pp-width-full" style="margin-top:5px;">Time Zone</label>
							</div>
						</div>
						<div class="pp-width-full pull-left" style="margin-bottom: 10px;">
							<div class="pull-left" style="width:25%">
	                            <div class="pull-left">
	                            	<select class="pp-width-full" id="jobStartHour" name="jobStartHour" style="padding-left: 10px;"></select>
	                            </div>
	                            <div class="pull-left" style="margin-left: 5px;">
	                            	<select class="pp-width-full" id="jobStartMinute" name="jobStartMinute" style="padding-left: 10px;"></select>
	                           	</div>
	                           	<div class="pull-left" style="width:32%; margin-left: 5px;">
	                            	<select class="pp-width-full" id="jobStartMeridiem" name="jobStartMeridiem" style="padding-left: 10px;"></select>
	                            </div>
                            </div>
                           	<div class="pull-left" style="width: 25%">
								<div class="pull-left">
									<select class="pp-width-full" id="jobEndHour" name="jobEndHour" style="padding-left:10px;"></select>
	                            </div>
	                            <div class="pull-left" style="margin-left: 5px;">
	                            	<select class="pp-width-full" id="jobEndMinute" name="jobEndMinute" style="padding-left:10px;"></select>
	                           	</div>
	                           	<div class="pull-left" style="width:32%; margin-left: 5px;">
	                            	<select class="pp-width-full" id="jobEndMeridiem" name="jobEndMeridiem" style="padding-left:10px;"></select>
	                            </div>
                            </div>
                            <div class="pull-left" style="width: 15%;">
								<select class="pp-width-full" id="timezone" name="timezone" required>
									<option value="000">Time Zone</option>
								</select>
							</div>
							<div class="pull-right">
								<span class="pull-right pp-ajax-loader {{waitprocess ? 'show' : 'hide'}}">Please wait...</span>
							</div>
                        </div>
                        <div class="pp-width-full" style="float: left; margin-bottom: 10px;">
                        	<div style="float: left; width: 25%; float: left">
								<select id="timeZone" class="pp-width-full" name="bank" ng-model="bank"
						          ng-options="bank.name for bank in banks | orderBy:'name'" ng-change="getCompaniesByBank(bank)">
                                	<option value="">Select Bank</option>
                            	</select>
                            </div>
                            <label for="applytoAll"
									class="control-label" style="float:left;margin-left:10px;margin-top:5px;">Apply to All</label>
							<div class="pull-left" style="width:6%;margin-left:3px;">
								<input type="checkbox" id="selectAllCheck" name="selectAll" value="true" style="float:left;margin-top:7px;" ng-model="all"/>
							</div>
                        </div>
					
                        <div class="pp-width-full" style="float: left; margin-bottom: 10px;">
							<table class="table table-striped table-bordered">
								<thead>
									<tr class="row-bg-color">
										<th  style="width: 2%" align="center">#</th>
										<th >Company</th>
										<th >Start</th>
										<th >End</th>
										<th >Time Zone</th>
									</tr>
								</thead>
								<tbody>
									<tr ng-repeat="comp in fiteredCompany | orderBy:'companyName'">
										<td style="width: 2%" align="center">
											<input type="checkbox" id="selectAll" name="selectAll" ng-model="individual" value="true"style="float: left" ng-click="selectedCompany(comp.companyId)" class="checkBoxes"/>
										</td>
										<td>
											{{comp.companyName}}
										</td>
										<td>
											{{comp.start}}
										</td>
										<td>
											{{comp.end}}
										</td>
										<td>
											{{comp.timezone}}
										</td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="pp-width-full">
							<button class="btn button" id="savebank" ng-click="save()" style="float:right" ng-disabled="decisionWindowForm.$invalid">Save</button>
   					   	</div>
   					</div>
           		</div>
    		</div>
		</div>
	</form>
</div>
<script>
    // Determines the time zone of the browser client
    var tz = jstz.determine();
    var zone = tz.name();
    document.getElementById('systimezone').value = zone;
    document.getElementById('logintimezone').value = zone;
</script>