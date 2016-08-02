<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery.blockUI.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/manualEntry.js" type="text/javascript"></script>	
<script	src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Manual Entry</div>
			</div>
			<div class="panel-body">
				<div id="ajax-loader-manualEntry"></div>
				<div id="error-saving-manualentries" class="alert alert-danger alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="error loading recent files"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
						Error while saving record. Please try again.
				</div>
				<div id="errorBox" class="alert alert-danger hidden">
		  			<strong>Warning!</strong> Please fix the following errors before you proceed:
		  			<ul id="errors"></ul>
				</div>
				<div id="successBox" class="alert alert-success hidden">
				</div>
				<div id="duplicateChecksErrorBox" class="alert alert-danger hidden">
				</div>
				<form id="checkEntryForm" class="form-horizontal" role="form">
					<div class="table-responsive">
						<table id="manualEntryTbl" class="table table-striped table-bordered">
							<thead>
								<tr>
									<th>Item Type</th>
									<th class="col-md-3">Company</th>
									<th>Account</th>
									<th>Check Number</th>
									<th>Amount</th>
									<th>Date</th>
									<th>Payee</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
							<tr id="row1" class="tblRow" islastRow="true">
								<td>
									<select id="issueCode1" name="issueCode">
										<option value="">Item Type</option>
									</select>
								</td>
								<td>
									<select class="pp-width-full" id="company1" name="company" onchange="loadAccounts(this.value, $(this).closest('tr').attr('id'));">
										<option value="">Name</option>
									</select>
								</td>
								<td>
									<select id="accountNumber1" name="accountNumber">
										<option value="">Select</option>
									</select>
								</td>
								<td>
									<input type="text" id="checkNumber1" name="checkNumber"
										size="12" placeholder="Check Number">
								</td>
								<td>
									<input type="text" id="checkAmount1" name="issuedAmount"
										size="8" placeholder="Amount" onblur="this.value = toDecimal(this.value);">
								</td>
								<td class="pp-width-ie-10">
									<div id="calendarBtn1" class="date-input">
										<input type="text" id="issueDate1" name="issueDate" size="10" placeholder="Item Date">
									</div>
								</td>
								<td>
									<input type="text" id="payee1" name="payee"	size="12" placeholder="Payee Name">
								</td>
								<td class="pp-width-ie-10">
									<div class="pp-width-full" style="width: 75px; float: left;">
										<div style="float: left; margin-right: 4px;">
											<a href="#" id="copy-button1" onclick="copyRow(1);"><img alt="Copy" src="<%=request.getContextPath()%>/static/positivepay/images/icons/copy-sprite.png" /></a>
										</div>
										<div style="float: left; margin-right: 4px;">
											<a href="#" id="delete-button1" data-id='1' style="display:none;" data-toggle="modal" onclick="showDeleteConfirmationDialogue(1);"><img alt="Delete" src="<%=request.getContextPath()%>/static/positivepay/images/icons/delete-sprite.png" /></a>
										</div>
										<div style="float: left;">
											<a href="#" id="add-button1" onclick="addRow(1);"><img alt="Add" src="<%=request.getContextPath()%>/static/positivepay/images/icons/add-sprite.png" /></a>
										</div>
									</div>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
				</form>
				<div>
					<div class="pull-right">
						<button id="saveManualEntry" type="button" class="btn button">SAVE</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade bs-modal-xs" id="deleteConfirmModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-xs">
		    	<div class="modal-content">
		      		<div class="modal-header">
		        		<label id="myModalLabel" class="modal-title">Confirm Delete?</label>
						<div style="float: right">
							<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#deleteConfirmModal" aria-hidden="true"></button>
						</div>
		      		</div>
		      		<div class="modal-body">
						Do you want to delete this entry?
		      		</div>
		      		<div class="modal-footer">
		        		<button type="button" class="btn button" data-dismiss="modal">No</button>
		        		<button type="button" id="btnYes" onclick="onConfirmDelete();" class="btn button">Yes</button>
		      		</div>
		    	</div>
		  	</div>
		</div>
	</div>
</div>