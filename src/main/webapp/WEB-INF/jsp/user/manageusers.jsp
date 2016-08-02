<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="com.westernalliancebancorp.positivepay.utility.SecurityUtility"%>
<%@ page import="com.westernalliancebancorp.positivepay.model.Permission" %>

<script src="<%=request.getContextPath()%>/static/positivepay/js/controllers/ManageUserController.js?v=1" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js" type="text/javascript"></script>

<script>
	var banks = ${banks};
	var companies = ${companies};
</script>

<div class="row" ng-controller="ManageUserController">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Manage Users</div>
			</div>
			<div class="panel-body" id="pb">
				<span id="show"></span>
				<div class="pp-ajax-loader {{waitProcessMessage !='' ? 'show' : 'hide'}}">{{waitProcessMessage}}</div>
				<div id="manageUserInfoBox" class="alert alert-success {{manageUserMessage ? 'show' : 'hidden'}}">{{manageUserMessage}}</div>
				<div id="manageUserErrorBox" class="alert alert-danger {{manageUserError ? 'show' : 'hidden'}}">{{manageUserError}}</div>
				<form:form id="companysetup-form" method="post" enctype="form-data"
					modelAttribute="companyDto" class="form-horizontal"
					commandName="companyDto" action="saveCompanySetup" role="form">
					<div class="form-group">
						<div class="col-sm-2">
							<label for="userSearch">User Search</label>
							<input id="userSearch" class="form-control" id="username"
								name="userSearch" type="text" ng-model="findUserCriteria.username">
						</div>
						<div class="col-sm-2">
							<label for="bank">Bank</label>

							<select id="bank" name="bank" class="pp-width-full" ng-model="findUserCriteria.bankId"
								ng-options="bank.id as bank.bankName for bank in banks | orderBy:'bankName'" id="allBanks"
								ng-change="findUserCriteria.companyId=''">
								<option value="">Select Bank</option>
							</select>
						</div>
						<div class="col-sm-2">
							<label for="accountNo">Account No.</label>
							<input id="accountNo" class="form-control" id="accountNo"
								name="accountNo" type="text" ng-model="findUserCriteria.accountNo">
						</div>
						<div class="col-sm-2">
							<label for="comp">Company</label>
							<select id="comp" name="comp" class="pp-width-full" ng-model="findUserCriteria.companyId"
								ng-options="company.id as company.name for company in companies | orderBy:'name' | filter:searchCompanyFilter(findUserCriteria.bankId)" id="allComps">
								<option value="">Select Company</option>
							</select>
						</div>
						<div class="col-sm-2" style="margin-top: 15px;">
							<button class="col-sm-12 btn button" id="buttonFindUser" type="button"
								ng-click="findUsersBySearchCriteria()">
								Find Users
							</button>
						</div>
						<div class="col-sm-2" style="margin-top: 22px;">
							<input type="checkbox" id="archive"
								name="archive" value="true" ng-model="findUserCriteria.archivedUser"> 
								Archived Users Only
							</input>					
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-12">
							<hr/>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-12">
							<div class="checkbox-inline">
								<input type="checkbox" id="reactive"
									name="reactive" ng-model="reactivate" ng-change="archive = (reactivate ? false : archive) "> 
									Reactivate
							</div>
							<div class="checkbox-inline">
								<input type="checkbox" id="archive"
									name="archive" value="true" ng-model="archive" ng-change="reactivate  = (archive ? false : reactivate ) "> 
									Archive
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-12">
							<table class="table table-striped table-bordered">
								<thead>
									<tr class="row-bg-color">
										<th class="col-sm-2">User Name</th>
										<th class="col-sm-2">Role</th>
										<th class="col-sm-1">Status</th>
										<th class="col-sm-2">Bank</th>
										<th class="col-sm-2">Company</th>
										<th class="col-sm-1">Edit</th>
										<th class="col-sm-1">Audit Trail</th>
									</tr>
								</thead>
								<tbody>
									<tr ng-repeat="users in manageUsers | orderBy:'username'">
										<td>{{users.username}}</td>
										<td>{{users.role}}</td>
										<td>{{users.active ? 'Active' : 'Inactive'}}</td>
										<td>
											<select class="pp-width-full {{users.bankId ? '' : 'highlight'}}" ng-model="users.bankId" 
												ng-options="bank.id as bank.bankName for bank in banks | orderBy:'bankName'"
												ng-change="users.companyId=null">
												<option value="">Select</option>
											</select>
										</td>
										<td>
											<select class="pp-width-full {{users.companyId ?  '' : 'highlight'}}" ng-model="users.companyId" 
												ng-options="company.id as company.name for company in companies | orderBy:'name' | filter:{bankId:users.bankId}">
												<option value="">Select</option>
											</select>										
										</td>
										<td>
											<a href="#" ng-click="fetchPermissions(users.username)" 
												class="pp-sprite pp-sprite-edit center-block" ></a>
										</td>
										<td>												
			                         		<a id="latestActivity" href="#" ng-click="fetchUserActivity(users.userId, users.username, true)" 
			                         			class="pp-sprite pp-sprite-eyeball center-block"></a>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-12">
							<button class="btn button col-sm-2" id="emulateBtn"
								type="button" style="float: right" ng-click="saveManageUsers()">Save</button>
						</div>
					</div>
				</form:form>			
			</div>
		</div>
	</div>
	
	<!-- Permission Modal -->
	<div class="modal fade" id="permissionModal" tabindex="-1" role="dialog" aria-labelledby="myPermissionModalLabel" aria-hidden="true">
		<div class="modal-dialog">
	    	<div class="modal-content" style="margin-left: -100px; width: 700px; height: auto">
	      		<div class="modal-header">
	      			<button type="button" class="pp-sprite-close-window" style="float: right" data-dismiss="modal" aria-hidden="true"></button>
	        		<h3 class="modal-title" id="myPermissionModalLabel">Permissions</h3><span class="pp-ajax-loader {{userPermissionWaitMsg == '' ? 'hide' : 'show'}}">{{userPermissionWaitMsg}}</span>
	        		<h4>Editing Permissions for: <strong>{{userPermission.userName}}</strong></h4>
	      		</div>
	      	<div class="modal-body" >
	      		<div id="permissionsErrorBox" class="alert alert-danger {{permissionsError ? 'show' : 'hidden'}}">{{permissionsError}}</div>
	        		<form:form id="saveUserPermissionsForm" method="POST" modelAttribute="userPermission" role="form" class="form-horizontal" action="saveUserPermissions">
	        			<div class="form-group">
	        				<div class="col-sm-10">
		        				<label class="control-label">Base Role Set&nbsp;&nbsp;&nbsp;</label>
								<label ng-repeat="role in userPermission.roles">
	       							<input type="radio" name="baseRole" ng-model="userPermission.baseRoleId" ng-change="onRoleChange(userPermission.baseRoleId)"
	       								<%if (!SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)) { %>
	       									ng-disabled="true" 
	       								<%} //if condition closed for hasPermission(Permission.NAME.ADD_ROLE)%>				 
	       									ng-value="role.id" />&nbsp;{{role.label}}&nbsp;&nbsp;
	         					</label>
	         					<label>
	         						<input type="radio" name="baseRole" ng-model="userPermission.baseRoleId"
	         						<%if (!SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)) { %>
	       									ng-disabled="true" 
	       							<%} //if condition closed for hasPermission(Permission.NAME.ADD_ROLE)%> 
	         						ng-value="" ng-change="onRoleChange('')"/>None&nbsp;
	         					</label>
		        			</div>
	        				<button ng-click="savePermissions()" type="button" class="btn button pull-right">Save</button>
	        			</div>
	        			<%if (SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)) { %>
		        			<div class="form-group">
		        				<div class="pull-right">
									<label>
										<input type="checkbox" id="saveNewRoleCheck" name="saveNewRoleCheck" ng-model="newRoleFlag" />
										Save As new Role&nbsp;
									</label>
									<label>
										<input class="form-control input-sm" id="newRole" name="newRole" type="text" ng-model="userPermission.newRoleName" ng-disabled="!newRoleFlag">
									</label>
		        				</div>
							</div>
						<%} //if condition closed for hasPermission(Permission.NAME.ADD_ROLE)%>
						<div class="form-group">
							<label class="control-label">Select permissions you want to apply to the selected users&nbsp;&nbsp;</label>
							<input type="checkbox" id="selectAllPermission" name="selectAllPermission" ng-model="selectAllPermissions" ng-change="onSelectAllPermissionChange()" />							
							<label for="selectAllPermission" class="control-label">Select All</label>
						</div>
						<div id="items" class="form-group">
							<label for="items" class="label-large">Items</label>				
							<hr>
							<c:forEach var="item" items="${itemsList}">
								<label class="col-md-4">
									<input type="checkbox" ng-model="userPermission.permissions[${item.id}]" ng-init="availablePermissionIds.push(${item.id})" >&nbsp;${item.label}
								</label>
							</c:forEach>
						</div>								
						<div id="manualEntry" class="form-group">
							<br>
							<label for="items" class="label-large">Manual Entry</label>					
							<hr>
							<c:forEach var="manualEntry" items="${manualEntryList}">
								<label class="col-md-4">
									<input type="checkbox" ng-model="userPermission.permissions[${manualEntry.id}]" ng-init="availablePermissionIds.push(${manualEntry.id})" >&nbsp;${manualEntry.label}
								</label>
							</c:forEach>
						</div>	
						<div id="userRoleManagement" class="form-group">
							<br>
							<label for="items" class="label-large">User/Role Management</label>				
							<hr>
							<c:forEach var="userRoleManagement" items="${userRoleManagementList}">
								<label class="col-md-4">
									<input type="checkbox" ng-model="userPermission.permissions[${userRoleManagement.id}]" ng-init="availablePermissionIds.push(${userRoleManagement.id})" >&nbsp;${userRoleManagement.label}
								</label>
							</c:forEach>
						</div>
						<div id="payments" class="form-group">
							<br>
							<label for="items" class="label-large">Payments</label>				
							<hr>
							<c:forEach var="payments" items="${paymentsList}">
								<label class="col-md-4">
									<input type="checkbox" ng-model="userPermission.permissions[${payments.id}]" ng-init="availablePermissionIds.push(${payments.id})" >&nbsp;${payments.label}
								</label>
							</c:forEach>
						</div>
						<div id="otherPermissions" class="form-group">
							<br>
							<label for="items" class="label-large">Other Permissions</label>				
							<hr>
							<c:forEach var="otherPermissions" items="${otherPermissionsList}">
								<label class="col-md-4">
									<input type="checkbox" ng-model="userPermission.permissions[${otherPermissions.id}]" ng-init="availablePermissionIds.push(${otherPermissions.id})" >&nbsp;${otherPermissions.label}
								</label>
							</c:forEach>
						</div>
						<div style="float: right">
							<%if (SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)) { %>
							<label>
								<input type="checkbox" id="saveNewRoleCheck" name="saveNewRoleCheck" ng-model="newRoleFlag" />
								Save As new Role
							</label>
							<label>
								<input class="form-control input-sm" id="newRole" name="newRole" type="text" ng-model="userPermission.newRoleName" ng-disabled="!newRoleFlag">
							</label>
							<%} //if condition closed for hasPermission(Permission.NAME.ADD_ROLE)%>
							<button ng-click="savePermissions()" type="button" class="btn button">Save</button>
						</div>
					</form:form>
	      		</div>
	      		<div class="modal-footer">
	        		<!-- <button type="button" class="btn btn-primary">Save changes</button> -->
	      		</div>
	    	</div>
	  	</div>
	</div>
	
	<!-- Code for modal -->		
	<div class="modal fade bs-modal-xs" id="latestActivityModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	  	<div class="modal-dialog modal-xs" style="width:600px;">
		    <div class="modal-content">
				<div class="modal-header">
			        <button type="button" class="pp-sprite-close-window" style="float: right" data-dismiss="modal" aria-hidden="true"></button>
		        	<h4 class="modal-title" id="myModalLabel">Latest Activity	&nbsp;&nbsp;&nbsp;
		        	<span>
                   		<a id="print" href="#" ng-click="print()">
                   			<span class="add-on">
                   				<i class="icon-small icon-print"></i>Print
                   			</span>
                   		</a>
                  	</span>
					</h4>
				</div>
			    <div class="modal-body">
			      	<div id="info" class="alert alert-success alert-dismissable" hidden="true"></div>
					<div id="error" class="alert alert-danger alert-dismissable" hidden="true">
						<button type="button" class="close" data-dismiss="alert">&times;</button>				 	
					</div>	
					<div id="auditTrail">
						<table class="table table-striped table-bordered">					
							<thead>
								<tr>
							    	<th>Time</th>
							    	<th>Date</th>
							    	<th>Session</th>
							    	<th>Comments</th>
								</tr>
							</thead>
							<tbody id="activity">
							</tbody>
						</table>
					</div>
					<div id="noMoreDataToShow" style="display: none;">No more data to show</div>
											
					<div style="margin-top: 12px;" id="latestActivityBtnContainer">
						<button type="button" id="latestActivityBtn" class="btn button" ng-click="fetchUserActivity()">More Activity</button>
					</div>
				</div>
			    <div class="modal-footer"></div>
		    </div>
	  	</div>
	</div>
</div>
