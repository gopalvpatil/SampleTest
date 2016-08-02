<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery.mask.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/job.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/company/companysetup.js?v=2" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js" type="text/javascript"></script>

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
	
	.cust-col-no-padding {
		padding: 0px !important;
	}
	
	.acc-control-label {
		text-align: left !important;
	}
	
	.cust-col-sm-42 {
		width: 42%;
	}
	
	.cust-col-width-10 {
		width: 10%;
	}
	
	.cust-col-width-21 {
		width: 21%;
	}
	
	.cust-col-width-14 {
		width: 14%;
	}
	
	.cust-col-padd {
		padding-left: 15px !important;
	}
	
	.cust-col-width-27 {
		width: 27%;
	}
	
	.cust-col-width-33 {
		width: 31%;
	}
	
	.cust-control-lable {
		padding-left: 10px !important;
	}
	
	.col-sm-1,.col-sm-2,.col-sm-3,.col-sm-4,.col-sm-5,.col-sm-6,.col-sm-7,.col-sm-10 {
		padding-left: 5px;
		padding-right: 5px;
		float: left;
	}
	
	input[type="checkbox"] {
		margin: 0px;
	}
	
	.icon-large {
		margin-left: 20px;
	}
	
	#address>input[type="text"],address>select {
		float: left;
		width: 33%;
	}
	
	#address>select {
		width: 32%;
		margin-left: 1%;
		margin-right: 1%;
		float: left;
		height: 34px;
		padding-right: 2px;
	}
	
	#timeZone {
		width: 100%;
	}
	
	#bankName {
		width: 40%;
	}
	
	.text-align-left1 {
		text-align: left !important;
	}
</style>

<div class="row" ng-controller="CompanyController">
	<div class="positivepay-column-spacer" >
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Company Setup</div>
			</div>
			<div class="panel-body">
				<div id="companyInfoBox" class="alert alert-success {{companyInfoMessage ? 'show' : 'hidden'}}">{{companyInfoMessage}}</div>
				<div id="companySetupBox" class="alert alert-danger {{companySetupError ? 'show' : 'hidden'}}">{{companySetupError}}</div>
				
				<form:form id="companysetup-form" method="post" enctype="form-data"
						modelAttribute="companyDTO" class="form-horizontal"
						commandName="companyDTO" action="saveCompanySetup" role="form">
					<input id="companyId" name="companyId" type="hidden" value="${companyId}" />
					<input id="bankId" name="bankId" type="hidden" value="${bankId}" />
					
					<div class="col-sm-10" style="margin-left: 3px;">
						<div class="form-group">
							<span class="pull-right pp-ajax-loader {{waitprocess ? 'show' : 'hide'}}">Please wait...</span>
							<label class="col-sm-2 control-label" for="bank">Bank </label>
							<div class="col-sm-8">
								<form:select id="bank" name="bank" path="bankId" ng-model="company.bankId">       
									<form:option value="" label="Select Bank"/>
									<form:options items="${banks}" var="bank" itemValue="id" itemLabel="name"/>
								</form:select>
							</div>
						</div>
					</div>

					<div class="col-sm-5">
						<div class="form-group">
							<label class="col-sm-4 control-label" for="mainContact">Main Contact</label>
							<div class="col-sm-8">
								<input id="mainContact" class="form-control" name="mainContact" type="text" ng-model="company.mainContact"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="address1">Address</label>
							<div class="col-sm-8">
								<input id="address1" name="address1" class="form-control" type="text" ng-model="company.address1"/>
							</div>

						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="address2">&nbsp;</label>
							<div class="col-sm-8">
								<input id="address2" class="form-control" name="address2" type="text" ng-model="company.address2"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="city">&nbsp;</label>
							<div class="col-sm-8" id="address">
								<input id="city" class="form-control" name="city" type="text" ng-model="company.city"/> 
								<select id="state" name="state" class="pp-width-full" ng-model="company.state"
									ng-options="k as v for (k,v) in us_states" >
									<option value="">Select State</option>
								</select>
								<input id="zip" class="form-control" name="zip" type="text" maxlength="10" ng-model="company.zip"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="email">Contact Email</label>
							<div class="col-sm-8">
								<input id="email" class="form-control" name="email" type="text" ng-model="company.email"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="phone">Contact Phone</label>
							<div class="col-sm-8">
								<input id="contactPhone" class="form-control" name="phone" type="text" maxlength="14" ng-model="company.phone"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="fax">Contact Fax</label>
							<div class="col-sm-8">
								<input id="contactFax" class="form-control" name="fax" type="text" maxlength="14" ng-model="company.fax"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="isActive" style="padding-top: 0px;">Is Active</label>
							<div class="col-sm-8">
								<input id="active" type="checkbox" class="checkbox" ng-model="company.active" />
							</div>
							
						</div>
					</div>
					<div class="col-sm-5">
						<div class="form-group">
							<label class="col-sm-4 control-label" for="companyName">Company Name</label>
							<div class="col-sm-8">
								<input id="companyName" class="form-control" name="companyName" type="text" ng-model="company.name"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="branchNameAndNumber">Branch Name & No</label>
							<div class="col-sm-8">
								<input id="branchNameAndNumber" class="form-control" name="branchNameAndNumber" type="text" ng-model="company.branchName"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="federalTaxId">Federal Tax ID</label>
							<div class="col-sm-8">
								<input id="federalTaxId" class="form-control" name="federalTaxId" type="text" ng-model="company.federalTaxId"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="accountForBilling">Account for analysis billing </label>
							<div class="col-sm-8">
								<input id="accountForBilling" class="form-control" name="accountForBilling" type="text" ng-model="company.accountForAnalysis"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="timeZone">Time Zone</label>
							<div class="col-sm-8">
								<select id="timeZone" name="timeZone" class="pp-width-full" ng-model="company.timeZone"
									ng-options="k as v for (k,v) in timezone" >
									<option value="">Select</option>
								</select>
							</div>
						</div>
					</div>
					<div class="col-sm-offset-2 col-sm-10">
						<div class="form-group" style="margin-left: 0; margin-right: 0px;">
							<button class="btn button" id="buttonSaveCompany" type="button"
								style="float: right" ng-click="saveCompany()">Save</button>
						</div>
					</div>
				</form:form>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Accounts</div>
			</div>
			<div class="panel-body">
				<div class="form-group">
						<label for="showArchievedAccounts">&nbsp;&nbsp;Show Inactive Accounts </label>
						<input  id="showArchievedAccounts" name="showArchievedAccounts" type="checkbox" ng-model="showArchievedAccounts">
					</div>
				<table class="table table-striped table-bordered">
					<thead>
						<tr class="row-bg-color">
							<th class="col-sm-3">Account Number</th>
							<th class="col-sm-7">Account Name</th>
							<th>Is Active</th>
							<th>Edit</th>
							<th>Delete</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="account in company.accounts" class="{{account.active || showArchievedAccounts ? '' : 'collapse' }}">
							<td>{{account.accountNumber}}</td>
							<td>{{account.accountName}}</td>
							<td>{{account.active ? 'Active' : 'Inactive'}}</td>
							<td><!-- Allow edit account only if company is active -->
								<a href="#"><div class="pp-sprite pp-sprite-edit {{isCompanyActive ? 'show' : 'hide' }}" ng-click="editAccount(account.id)"></div></a>
							</td>
							<td>
								<a href="#"><div class="pp-sprite pp-sprite-delete {{account.active ? 'show' : 'hide' }}" ng-click="openDeleteConfirmationModal(account.id)"></div></a>
							</td>
						</tr>
					</tbody>
				</table>
				<div class="form-group pp-margin-top-small">
					<button class="btn button" type="button" ng-disabled="!isCompanyActive" ng-click="addAccount(company ? company.id : null)" style="float: right">Add Account</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- -For Account Page  -->

	<div class="modal fade bs-modal-xs" id="accountSetupModal" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-xs">
			<div class="modal-content" style="border: none">
				<div class="col-sm-12 cust-col-no-padding">
					<div class="panel panel-primary">
						<div class="panel-body">
							<div class="form-group">
								<label class="modal-title col-sm-1 pp-width-full" style="font-weight: bolder;">Account
								<span class="pull-right pp-ajax-loader {{accountwaitprocess ? 'show' : 'hide'}}">Please wait...</span></label>
								<div id="accountSetupBox" class="alert alert-danger {{accountSetupError ? 'show' : 'hidden'}}">{{accountSetupError}}</div>
								<div style="float: right">
									<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#accountSetupModal" aria-hidden="true"></button>
								</div>
							</div>
							<form id="companysetup-form" method="post" enctype="form-data" class="form-horizontal">
								<div class="form-group">
									<div class="col-sm-5 cust-col-sm-42">
										<label class="col-sm-9  pp-width-full" for="accountNumber" style="font-weight: bolder;">Account Number</label>
										<div class="col-sm-9 pp-width-full">
											<input id="accountNumber" class="form-control" name="accountNumber" type="text" ng-model="account.accountNumber" required>
										</div>
									</div>
									<div class="col-sm-5">
										<label class="col-sm-5 pp-width-full" for="accountName" style="font-weight: bolder;">Account Name</label>
										<div class="col-sm-6 pp-width-full">
											<input id="accountName" class="form-control" name="accountName" type="text" ng-model="account.accountName" required>
										</div>
									</div>
								</div>
								<div class="form-group">
									<div class="row">
										<label class="col-sm-4 control-label" for="staleDate" style="font-weight: bolder;">Stale Date Checks After</label>
										<div class="col-sm-2">
											<input id="staleDate" class="form-control" name="staleDate" type="number" ng-model="account.staleDays" value="180">
										</div>
										<label class="col-sm-3 control-label">Days. (default 180)</label>
									</div>
								</div>
								</br>
								<div class="row">
									<div class="col-md-4">
										<strong>Service Option</strong>
										<label class="radio" >
										    <input id="serviceOption" name="serviceOption" type="radio" ng-model="account.accountServiceOption" value="1" />
										    Full
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="serviceOption" name="serviceOption" type="radio" ng-model="account.accountServiceOption" value="3" />
										    Partial
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="serviceOption" name="serviceOption" type="radio" ng-model="account.accountServiceOption" value="2" />
										    Positive Pay
										 </label>
									</div>
									<div class="col-md-4">
										<strong>Positive Pay Default Decision</strong>
										<label class="radio">
										    <input id="ppDefaultDecision" name="ppDefaultDecision" type="radio" ng-model="account.ppDecision" value="PAY" />
										    Pay
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="ppDefaultDecision" name="ppDefaultDecision" type="radio" ng-model="account.ppDecision" value="NOPAY" />
										    No Pay
										 </label>
									</div>
									<div class="col-md-4">
										<strong>Cycle Cut off</strong>
										<label class="radio">
										    <input id="cycleCutOff" name="cycleCutOff" type="radio" ng-model="account.accountCycleCutOff" value="1" />
										    Daily
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="cycleCutOff" name="cycleCutOff" type="radio" ng-model="account.accountCycleCutOff" value="2" />
										    Monthly
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="cycleCutOff" name="cycleCutOff" type="radio" ng-model="account.accountCycleCutOff" value="3" />
										    Special Calendar
										 </label>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-md-4">
										<strong>Issue File Input Method</strong>
										<label class="radio">
										    <input id="fileInputMethod" name="fileInputMethod" type="radio" ng-model="account.fileInputMethod" value="PPAY" />
										    Positive Pay Tool
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="fileInputMethod" name="fileInputMethod" type="radio" ng-model="account.fileInputMethod" value="FTP" />
										    FTP
										 </label>
									</div>
									<div class="col-md-4">
										<strong>Report/Data Output Method</strong>
										<label class="radio">
										    <input id="reportOutputMethod" name="reportOutputMethod" type="radio" ng-model="account.reportOutputMethod" value="PPAY" />
										    Positive Pay Download
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="reportOutputMethod" name="reportOutputMethod" type="radio" ng-model="account.reportOutputMethod" value="FTP" />
										    FTP
										 </label>
									</div>
									<div class="col-md-4">
										<strong>Data Output Format</strong>
										<label class="radio">
										    <input id="dataOutputMethod" name="dataOutputMethod" type="radio" ng-model="account.dataOutputMethod" value="TXT" />
										    .TXT
										 </label>
										 <label class="radio" style="padding-top:0px;">
										    <input id="dataOutputMethod" name="dataOutputMethod" type="radio" ng-model="account.dataOutputMethod" value="CSV" />
										    .CSV
										 </label>
									</div>
								</div>
								<br>
								<div class="col-sm-9 pp-width-full">
									<div class="form-group">
										<div class="col-sm-5 ">
											<label class="col-sm-3">Users</label>
											<div class="col-sm-1">
												<input id="selectAllUsersCheckBox" type="checkbox" class="checkbox" ng-model="selectAllUsers"
													ng-change="onSelectAllUserChange()" />
											</div>
											<label class="col-sm-7" for="selectAllUsers" style="padding-top: 0px" >Select All Users</label>
										</div>
									</div>
								</div>
								<div class="panel-body col-sm-9 pp-width-full">
									<div class="pre-scrollable" style="height: 100px;border:solid 1px black;">
										<div ng-repeat="user in userList">
											<label>&nbsp;&nbsp;	
        										<input type="checkbox" ng-checked="account.selectedUserIds.indexOf(user.userId) > -1" 
        											ng-click="onAccountUserCheckboxClicked(user.userId)"/>
        										{{user.userName}}
        									</label>
    									</div>
									</div>
								</div>
								<div class="col-sm-3" style="width:30%;clear:both;">
									<div class="form-group" style="margin:0px;">
										<div class="col-sm-2">
											<input id="active" type="checkbox" class="checkbox" ng-model="account.active" />
										</div>
										<label class="col-sm-10 " for="isActive" style="padding-top: 0px;">Is Active</label>
									</div>
								</div>
								<div class="form-group" style="padding-right: 20px;">
									<button class="btn button" id="emulateBtn" type="button"
										style="float: right" ng-click="saveAccount()">Save</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- Delete account confirmation -->
	<div class="modal fade" id="deleteAccountModal" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	            	<label class="modal-title">Delete Account</label>
	            	<div style="float: right">
						<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#deleteAccountModal" aria-hidden="true"></button>
					</div>
	            </div>
	            <div class="modal-body">
	                <h3>Are you sure you wish to delete the account?</h3>
	                <span class="pp-ajax-loader {{deleteAccountWaitprocess ? 'show' : 'hide'}}">Please wait...</span>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn button" data-dismiss="modal">No</button>
	                <button type="button" class="btn button" ng-click="deleteAccount(selectedAccountId)" >Yes</button>
	        	</div>
	    	</div>
	  	</div>
	</div>
</div>
