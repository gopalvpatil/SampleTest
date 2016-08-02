<script src="<%=request.getContextPath()%>/static/positivepay/js/job.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/controllers/UserController.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js"
	type="text/javascript"></script>

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
</style>	

<div class="row" ng-controller="UserController">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Find Users</div>
			</div>
			<div class="panel-body" id="pb">
				<span id="show">
				</span>
				<!-- Start of FORM -->
				<div id="userCreateInfo" style="display: none">
				</div>
				<div id="userCreateError" style="display: none">
				</div>
				<form name="findUserForm"
					style="border: 2px solid; border-radius: 0px; width: 1100px; height: 100px">
					<h4>Find A User</h4>
			
					<div class="form-actions" align="center">
						<input type="text" name="username" value="Username" id="username"
							maxlength="20" ng-model="user" required/>
			
						<button type="button" class="btn button" ng-click="findUser()" ng-disabled="findUserForm.$invalid">
							<i class="icon-fixed-width icon-ok"></i> Search
						</button>
					</div>
				</form>
				<br />
				<br />
				<label><h4> Add Users </h4></label>
				<form name="displayUserForm"
					style="border: 2px solid; border-radius: 0px; width: 1100px; height: 100px">
					<h4>Find A User</h4>
					
					<div class="form-actions" align="center">
						<input type="text" name="username" value="Username"
							ng-model="dispUser" /> <input type="text" name="email"
							value="User@email.com" ng-model="email" /> 
						<select name="comp" ng-model="comp" ng-options="company.name for company in companies">
							<option value="">Select Company</option>
						</select>
						<button type="button" class="btn button" ng-click="addUser()">
							<i class="icon-fixed-width icon-ok"></i> Add
						</button>
					</div>
				</form>
				<!-- End of FORM -->
			</div>
		</div>
	</div>
</div>
