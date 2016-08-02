<%@ page import="com.westernalliancebancorp.positivepay.utility.SecurityUtility"%>
<%@ page import="com.westernalliancebancorp.positivepay.model.Permission" %>

<script src="<%=request.getContextPath()%>/static/positivepay/js/controllers/UserController.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js" type="text/javascript"></script>

<style>

.form-inline .form-group input {
	width:140px;
}

.form-inline .form-group select {
	width:140px;
}

</style>

<script>
	var banks = ${banks};
	var companies = ${companies};
	var roles = ${roles};
</script>

<div class="row" ng-controller="AddUserController">
	<div class="positivepay-column-spacer">
	
		<div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">Add Users</div>
            </div>
            <div class="panel-body">
            	<div id="addUserWait" class="pp-ajax-loader" style="display: none;">Please wait...</div>
            	<div id="addUserError" class="alert alert-danger alert-dismissable" style="display: none;">
				</div>
				<div id="addUserInfo" class="alert alert-success alert-dismissable" style="display: none;">
				</div>
                <form class="form-inline" role="form">
                	<input type="hidden" id="userActive" ng-model="user.active" ng-init="user.active=true">
                	<input type="hidden" id="userLocked" ng-model="user.locked" ng-init="user.locked=false">
					  <div class="form-group">
					    <label class="sr-only" for="username">UserName</label>
					    <input type="text" class="form-control" id="username" placeholder="UserName" ng-model="user.userName">
					  </div>
					  <div class="form-group">
					    <label class="sr-only" for="email">Email</label>
					    <input type="text" class="form-control" id="email" placeholder="Email" ng-model="user.email">
					  </div>
					  <div class="form-group">
					    <label class="sr-only" for="firstName">First Name</label>
					    <input type="text" class="form-control" id="firstName" placeholder="First Name" ng-model="user.firstName">
					  </div>
					  <div class="form-group">
					    <label class="sr-only" for="lastName">Last Name</label>
					    <input type="text" class="form-control" id="lastName" placeholder="Last Name" ng-model="user.lastName">
					  </div>
					  <input type="hidden" id="hasAddRolePermission" ng-model="hasAddRolePermission"  
					  	ng-init="hasAddRolePermission=<%=SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)%>">
					  <%if (SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)) { %>
						  <div class="form-group">
						    <label class="sr-only" for="bank">Role</label>
						    <select id="role" name="role" class="pp-width-full" ng-model="user.roleId"
										ng-options="role.roleId as role.roleLabel for role in roles" >
										<option value="">Select Role</option>
							</select>
						  </div>
					  <%} else {%>
					  	<input type="hidden" id="hasAddRolePermission" ng-model="hasAddRolePermission"  ng-init="hasAddRolePermission=false">
					  <% } %>
					  <div class="form-group">
					    <label class="sr-only" for="bank">Bank</label>
					    <select id="bank" name="bank" class="pp-width-full" ng-model="user.bankId"
									ng-options="bank.id as bank.bankName for bank in banks | orderBy:'bankName'"
									ng-change="user.companyId=''">
									<option value="">Select Bank</option>
						</select>
					  </div>
					  <div class="form-group">
					    <label class="sr-only" for="comp">Company</label>
					    <select id="comp" name="comp" class="pp-width-full" ng-model="user.companyId"
									ng-options="company.id as company.name for company in companies | orderBy:'name' | filter:{bankId:user.bankId}" 
									ng-disabled="user.bankId == null">
									<option value="">Select Company</option>
						</select>
					  </div>
					  <button type="button" class="btn button" ng-click="addUser()">Add</button>
				</form>
            </div>
        </div>
	</div>
</div>
