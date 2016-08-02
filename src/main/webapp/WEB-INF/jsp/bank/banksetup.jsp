<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script src="<%=request.getContextPath()%>/static/positivepay/js/bank/banksetup.js" type="text/javascript"></script>

<style>
	.icon-large {
		padding-left: 20px;
	}
	
	#address > input[type="text"], address > select {
		float: left;
		width: 33%;
	}
	
	#address > select {
		width: 32%;
		margin-left: 1%;
		margin-right: 1%;
		float: left;
		height: 34px;
		padding-right: 2px;
	}
	
	.text-align-left {
		text-align: left !important;
	}
</style>

<div class="row">
	<div class="positivepay-column-spacer">
		<form:form id="banksetup-form" method="post" enctype="multipart/form-data" 
					modelAttribute="bankDto" class="form-horizontal" commandName="bankDto" action="banksetup"
					role="form">
			
			<div class="panel panel-primary">
				<div class="panel-heading">
					<div class="panel-title">Bank Setup</div>
				</div>
				
				<div class="panel-body">
					<c:if test="${not empty successMessage}">
						<div id="serverSuccessBox" class="alert alert-success show" >
							<span><c:out value="${successMessage}"/></span>
						</div>
					</c:if>
					<div id="errorBox" class="alert alert-danger hidden">
			  			<ul id="errors"></ul>
					</div>
					
					<div id="successBox" class="alert alert-success hidden"></div>
					
					<form:errors path="*"  cssClass="alert alert-danger alert-dismissable" element="div"/>
					<input type="hidden" id="id" name="id" value="${bankDto.id}"/>
					<input type="hidden" id="redirectUrl" name="redirectUrl" value=""/>
					<input type="hidden" id="formDataChanged" name="formDataChanged" value=""/>
				  	
					<div class="col-sm-5">
						<div class="form-group">
							<label class="col-sm-4 control-label" for="bankName">Bank Name</label>
							<div class="col-sm-8">
								<input id="bankName" class="form-control" name="bankName" type="text" maxlength="50" value="${bankDto.bankName}"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="streetAddress">Address</label>
							<div class="col-sm-8">
								<input id="streetAddress" class="form-control" name="streetAddress" type="text" maxlength="100" value="${bankDto.streetAddress}"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="streetAddress2">&nbsp;</label>
							<div class="col-sm-8">
								<input id="streetAddress2" class="form-control" name="streetAddress2" type="text" maxlength="50" value="${bankDto.streetAddress2}"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="state">&nbsp;</label>
							<div class="col-sm-8" id="address">
								<input id="city" class="form-control" name="city" type="text" maxlength="50" value="${bankDto.city}"/>
								<select id="state" name="state" value="${bankDto.state}">
									<option value="">Select</option>
								</select>
								<input id="zipCode" class="form-control" name="zipCode" type="text" maxlength="10" value="${bankDto.zipCode}"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="logoImage">&nbsp;</label>
							<div class="col-sm-8">
								<img id="banklogoImage" style="max-height:150px;" src="<%=request.getContextPath()%>/user/bank/logo/${bankDto.logoPathFilename}" class="img-responsive" alt="Bank Logo">
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="logoImage">Logo</label>
							<div class="col-sm-8">
								<div class="input-group">
									<input name="logoPathFilename" id="logoPathFilename" type="hidden" value="${bankDto.logoPathFilename}"/>
									<input name="bankLogo" id="bankLogo" type="file" onchange="displayBankLogo(this)" accept="image/*" style="display: none;"/>
								  	<input id="logoUploadInput" type="text" onfocus="$('#bankLogo').click();" onclick="$('#bankLogo').click();" class="form-control"  placeholder="Logo Upload"/>
							      	<span class="input-group-btn">
							        	<button onclick="$('#bankLogo').click();" class="btn button" type="button">Browse</button>
							      	</span>
							    </div>
							</div>
						</div>
					</div>
					<div class="col-sm-5">
						<div class="form-group">
							<label class="col-sm-4 control-label" for="bankId">Bank ID</label>
							<div class="col-sm-8">
								<input id="bankId" class="form-control" name="bankId" type="number" max="32767" value="${bankDto.bankId}"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="bankNumber">Bank Number</label>
							<div class="col-sm-8">
								<input id="bankNumber" class="form-control" name="bankNumber" maxlength="20" type="number" value="${bankDto.bankNumber}"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-4 control-label" for="websiteUrl">Website URL</label>
							<div class="col-sm-8">
								<input id="websiteUrl" class="form-control" name="websiteUrl" type="text" value="${bankDto.websiteUrl}"/>
							</div>
						</div>
					</div>
					<div class="col-sm-offset-2 col-sm-10">
						<div class="form-group" style="margin-left: 0; margin-right: 0px;">
							<button class="btn button" id="savebank" type="submit" style="float:right">Save</button>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-primary">
				<div class="panel-heading">
					<div class="panel-title">Companies</div>
				</div>
				 
				<div class="panel-body">
					<div class="form-group">
						<label for="showArchievedCompanies">&nbsp;&nbsp;Show Inactive Companies </label>
						<input  id="showArchievedCompanies" name="showArchievedCompanies" type="checkbox">
					</div>	
					<table class="table table-striped table-bordered">
						<thead>
							<tr class="row-bg-color">
								<th class="col-sm-10">Company</th>
								<th>Is Active</th>
								<th>Edit</th>
								<th>Delete</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="company" items="${bankDto.companies}" varStatus="status">
								<tr id="tr_row_${company.id}" style="display: ${company.active ? 'table-row;' : 'none;'}" active="${company.active}">
									<td>${company.name}</td>
									<td id="td_active_${company.id}">${company.active ? 'Active' : 'Inactive'}</td>
									<td>
										<a id="editCompany" href="#" href1="companysetup?companyId=${company.id}&bankId=${bankDto.id}">
											<div class="pp-sprite pp-sprite-edit"></div>
										</a>
									</td>
									<td>
										<a id="deleteCompanyAnchor" href="#" companyId="${company.id}" bankId="${bankDto.id}"
												deleteData='{"id":"${company.id}", "bankId":"${bankDto.bankId}"}' style="display: ${company.active ? 'block;' : 'none;'}">
											<div class="pp-sprite pp-sprite-delete"></div>
										</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div class="form-actions pp-margin-top-small">
						<a class="btn button" id="addCompany" href="#" href1="companysetup?bankId=${bankDto.id}" style="float:right">Add Company</a>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>

<div class="modal fade" id="deleteCompanyModal" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
            	<label class="modal-title">Delete Company</label>
            	<div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#deleteCompanyModal" aria-hidden="true"></button>
				</div>
            </div>
            <div class="modal-body">
                <h3>Are you sure you wish to delete the company?</h3>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn button" data-dismiss="modal">No</button>
                <button id="confirmDeleteCompany" type="button" class="btn button">Yes</button>
        	</div>
    	</div>
  	</div>
</div>

<div class="modal fade" id="saveBankDataModal" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
            	<div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#saveBankDataModal" aria-hidden="true"></button>
				</div>
            	<h4 class="modal-title" id="myModalLabel">Save Bank Changes</h4>
            </div>
            <div class="modal-body">
                <h3>Do you want to save the bank changes?</h3>
            </div>
            <div class="modal-footer">
                <button id="discardBankChangesAndNavigate" type="button" class="btn button" >No</button>
                <button id="saveBankChangesAndNavigate" type="button" class="btn button">Yes</button>
        	</div>
    	</div>
  	</div>
</div>