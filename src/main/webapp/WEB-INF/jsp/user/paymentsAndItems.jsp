<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/datatable/css/jquery.dataTables.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/datatable/css/dataTables.fixedHeader.css" type="text/css" />
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/paymentsAndItems.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/thirdparty/loupe/js/jquery.loupe.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/paymentslist.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>	
<script	src="<%=request.getContextPath()%>/static/thirdparty/datatable/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/datatable/js/dataTables.fixedHeader.min.js" type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-body">
				<div>
					<button type="button" class="btn button" onclick="updateFilter();location.href='<%=request.getContextPath()%>/user/paymentsanditems'" >
						Update Filter
					</button>
					<button type="button" class="btn button" onclick="location.href='<%=request.getContextPath()%>/user/paymentsanditems'">
						Start Over
					</button>
				</div>
				<div id="accountinfoSaveMessage"></div>
				<h4 class="clearfix"><label id="listPageHeader"></label> <span id="paymentListBadge" class="badge"></span></h4>
				<div id="ajax-loader-paymentList"></div>
				<div id="error-loading-payments" class="alert alert-danger alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<img alt="error loading payment list"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
							Error while loading payment list. Please try again later.
				</div>
				<div id="error-loading-items" class="alert alert-danger alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<img alt="error loading item list"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
							Error while loading item list. Please try again later.
				</div>
				<div id="paymentList" class="hidden">
					<table id="paymentListTbl" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th>Account Number</th><th>Check Number</th><th>Payment Status</th><th>Match Status</th><th>Exception Type</th><th>Exception Status</th>
								<th>Issued Amount</th><th>Issued Date</th><th>Paid Amount</th><th>Paid Date</th><th>Stop Date</th><th>Void Date</th><th>Account Name</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<div id="itemList" class="hidden">
					<table id="itemListTbl" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th>Account Number</th><th>Check Number</th><th>Account Name</th><th>Bank</th><th>Item Type</th><th>Item Amount</th>
								<th>Item Date</th><th>Created By</th><th>Created Method</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<div id="noPayments" class="alert alert-info alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="No Payments"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
						No payments to display.
				</div>
			</div>
		</div>
	</div>
</div>



<!-- Pop up for item details -->
<div class="modal" id="itemDetailPopUp" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog positivepay-popup">
    <div class="modal-content">
		<div class="modal-header">
			<div style="float: right">
				<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#itemDetailPopUp" aria-hidden="true"></button>
			</div>
		</div>
		<div class="modal-body">
			<input type="text" id="itemCheckId" name="itemCheckId" class="hidden"/>
			
			<h4>Item Details</h4>
			<div id="ajax-loader-itemDetails"></div>
			<div id="error-loading-itemdetails" class="alert alert-danger alert-dismissable hidden">
				<button type="button" class="close" data-dismiss="alert"
					aria-hidden="true">&times;</button>
				<img alt="error loading item details"
					src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
					Error while loading item details. Please try again later.
			</div>
			<table id="itemDetailsTbl" class="table table-striped table-bordered hidden">
				<tr>
					<td>Check Number</td><td id="itemcheckNumberVal"></td><td>Payee</td><td id="itempayeeVal"></td>
				</tr>
				<tr>
					<td>Account No.</td><td id="itemaccountNumberVal"></td><td>Amount</td><td id="itemAmountVal"></td>
				</tr>
				<tr>
					<td>Customer</td><td id="itemcustomerVal"></td><td>Item Date</td><td id="itemDateVal"></td>
				</tr>
				<tr>
					<td>Bank</td><td id="itembankVal"></td><td>Item Code</td><td id="itemCodeVal"></td>
				</tr>
				<tr>
					<td>Payment Status</td><td id="itemPaymentStatusVal"></td><td>Match Status</td><td id="itemMatchStatusVal"></td>
				</tr>
				<tr>
					<td>Created Date</td><td id="itemCreatedDateVal"></td><td>Trace No.</td><td id="itemTraceNoVal"></td>
				</tr>
				<tr>
					<td>Created Method</td><td id="itemCreatedMethodVal"></td><td>Created By</td><td id="itemCreatedByVal"></td>
				</tr>
			</table>			
			</div>			
    	</div>
    	<div class="modal-footer">
		</div>
  	</div>
</div>

<!-- Pop up to show -->
<div class="modal" id="checkDetailPopUp" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog positivepay-popup">
    <div class="modal-content">
		<div class="modal-header">
			<div style="float: right">
				<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#checkDetailPopUp" aria-hidden="true"></button>
			</div>
			<div id="errorBox" class="alert alert-danger hidden">
	  			<strong>Warning!</strong> Please fix the following errors before you proceed:
	  			<ul id="errors"></ul>
			</div>
			<div id="ajax-loader-accountNumbers"></div>
			<div id="error-loading-accountNumbers" class="alert alert-danger alert-dismissable hidden">
				<button type="button" class="close" data-dismiss="alert"
					aria-hidden="true">&times;</button>
				<img alt="error loading account Numbers"
					src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
					Error while loading account numbers. Please try again later.
			</div>
			<div class="table-responsive">
				<table id="chkDetailsTbl">
					<tr>
						<td>Check Number</td>
						<td>Amount</td>
						<td>Account Number</td>
						<td>Payee</td>
						<td>Check Date</td>
						<td>Comment</td>
						<td>Action</td>
						<td></td>
					</tr>
					<tr id="row1">
						<td>
							<input type="text" id="checkNumber" name="checkNumber"
								disabled="true" style="width:110px">
						</td>
						<td>
							<input type="text" id="amount" name="amout"
								size="12" disabled="true" style="width:90px">
						</td>
						<td>
							<select id="accountNumber" name="accountNumber" disabled="true">
							</select>
						</td>
						<td>
							<input type="text" id="payee" name="payee"
								disabled="true" style="width:150px">
						</td>
						<td>
							<input type="text" id="checkDate" name="checkDate"
								size="12" disabled="true" style="width:100px">
						</td>
						<td>
								<textarea id="comment"
								name="comment" rows="3" disabled="true" style="width:150px"></textarea>
						</td>
						<td>
							<select id="action" name="action" onchange="resolveAction(this.value);">
								<option value="">Select action</option>
							</select>
						</td>
						<td>
							<button type="button" class="btn button" onclick="saveAccountInfo()">
								Save
							</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div id="ajax-loader-saveAccountinfo"></div>
		<div class="modal-body">
			<input type="text" id="checkId" name="checkId" class="hidden"/>
			<input type="text" id="traceId" name="traceId" class="hidden"/>
			<div id="ajax-loader-checkImages"></div>
			<div id="error-loading-checkimages" class="alert alert-danger alert-dismissable hidden">
				<button type="button" class="close" data-dismiss="alert"
					aria-hidden="true">&times;</button>
				<img alt="error loading check images"
					src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
					Error while loading check images. Please try again later.
			</div>
			<table id="checkImageTbl">
				<tbody>
					<tr>
					</tr>
				</tbody>
			</table>
			<h4>Check Details</h4>
			<div id="ajax-loader-checkDetails"></div>
			<div id="error-loading-checkdetails" class="alert alert-danger alert-dismissable hidden">
				<button type="button" class="close" data-dismiss="alert"
					aria-hidden="true">&times;</button>
				<img alt="error loading check details"
					src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
					Error while loading check details. Please try again later.
			</div>
			<table id="checkDetailsTbl" class="table table-striped table-bordered hidden">
				<tr>
					<td>Check Number</td><td id="checkNumberVal"></td><td>Payee</td><td id="payeeVal"></td>
				</tr>
				<tr>
					<td>Account No.</td><td id="accountNumberVal"></td><td>Issued Amount</td><td id="issuedAmountVal"></td>
				</tr>
				<tr>
					<td>Customer</td><td id="customerVal"></td><td>Issued Date</td><td id="issuedDateVal"></td>
				</tr>
				<tr>
					<td>Bank</td><td id="bankVal"></td><td>Paid Amount</td><td id="paidAmountVal"></td>
				</tr>
				<tr>
					<td>Bank No.</td><td id="bankNumberVal"></td><td>Paid Date</td><td id="paidDateVal"></td>
				</tr>
				<tr>
					<td>Payment Status</td><td id="paymentStatusVal"></td><td>Stop Date</td><td id="stopDateVal"></td>
				</tr>
				<tr>
					<td>Match Status</td><td id="matchStatusVal"></td><td>Void Date</td><td id="voidDateVal"></td>
				</tr>
				<tr>
					<td>Exception Type</td><td id="exceptionTypeVal"></td><td>Trace No.</td><td id="traceNoVal"></td>
				</tr>
				<tr>
					<td>Exception Status</td><td id="exceptionStatusVal"></td><td>Reference Number</td><td id="referenceNumberVal"></td>
				</tr>
			</table>			
			<div id="ajax-loader-paymenthistory"></div>
				<div id="error-loading-paymenthistory" class="alert alert-danger alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<img alt="error loading recent files"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
							Error while loading payment history. Please try again later.
				</div>
				<div id="paymenthistory" class="hidden">
					<h4>Payment History</h4>
					<hr/>
					<div class="table-responsive" style="margin-top: 10px;">
						<table class="table table-striped table-bordered" id="paymenthistoryTbl">
							<thead>
								<tr>
									<th>Sequence</th>
									<th>Date-Time</th>
									<th>User</th>
									<th>Description</th>
									<th>Comment</th>
									<th>Resulting Payment Status</th>
									<th>Created Method</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
				</div>
				<div id="noPaymentHistory" class="alert alert-info alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="No payment history"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
						No payment history to show.
				</div>
			</div>			
    	</div>
    	<div class="modal-footer">
		</div>
  	</div>
</div>

<!-- Pop up for duplicate paid -->
<div class="modal" id="duplicatePaidPopUp" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog positivepay-popup">
    <div class="modal-content">
		<div class="modal-header">
			<div style="float: right">
				<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#duplicatePaidPopUp" aria-hidden="true"></button>
			</div>
			<div id="duplicateerrorBox" class="alert alert-danger hidden">
	  			<strong>Warning!</strong> Please fix the following errors before you proceed:
	  			<ul id="duplicateerrors"></ul>
			</div>
			<div id="ajax-loader-accountNumbers"></div>
			<div id="error-loading-accountNumbers" class="alert alert-danger alert-dismissable hidden">
				<button type="button" class="close" data-dismiss="alert"
					aria-hidden="true">&times;</button>
				<img alt="error loading account Numbers"
					src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
					Error while loading account numbers. Please try again later.
			</div>
			<div class="table-responsive">
				<table id="duplicatechkDetailsTbl">
					<tr>
						<td>Check Number</td>
						<td>Amount</td>
						<td>Account Number</td>
						<td>Payee</td>
						<td>Check Date</td>
						<td>Comment</td>
						<td>Action</td>
						<td></td>
					</tr>
					<tr id="row1">
						<td>
							<input type="text" id="origcheckNumber" name="origcheckNumber"
								disabled="true" style="width:110px">
						</td>
						<td>
							<input type="text" id="origamount" name="origamout"
								size="12" disabled="true" style="width:90px">
						</td>
						<td>
							<select id="origaccountNumber" name="origaccountNumber" disabled="true">
							</select>
						</td>
						<td>
							<input type="text" id="origpayee" name="origpayee"
								disabled="true" style="width:150px">
						</td>
						<td>
							<input type="text" id="origcheckDate" name="origcheckDate"
								size="12" disabled="true" style="width:100px">
						</td>
						<td>
 								<textarea id="origcomment"
								name="origcomment" rows="3" disabled="true" style="width:150px"></textarea>
						</td>
						<td>
							<select id="origaction" name="origaction" onchange="resolveAction(this.value);">
								<option value="">Select Action...</option>
							</select>
						</td>
						<td>
							<button type="button" class="btn button" onclick="saveAccountInfoForDuplicatePaidPopUp()">
								Save
							</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div id="ajax-loader-orig-saveAccountinfo"></div>
		<div class="modal-body">
			<input type="text" id="dupcheckId" name="dupcheckId" class="hidden"/>
			<input type="text" id="exceptionId" name="exceptionId" class="hidden"/>
			<div id="ajax-loader-orig-checkImages"></div>
			<div id="error-loading-orig-checkimages" class="alert alert-danger alert-dismissable hidden">
				<button type="button" class="close" data-dismiss="alert"
					aria-hidden="true">&times;</button>
				<img alt="error loading check images"
					src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
					Error while loading check images. Please try again later.
			</div>
			<table id="origcheckImageTbl">
				<tbody>
					<tr>
					</tr>
				</tbody>
			</table>
			
			<h4>Duplicate Payment Details</h4>
			<hr/>
			<select id="duplicatedetails" name="duplicatedetails" onchange="showDuplicateDetails(this.value);">
								<option value="">Select Duplicate Details..</option>
							</select>
			<div class="table-responsive">
				<table id="dupchkDetailsTbl">
				
				<tr>
						<td>Check Number</td>
						<td>Amount</td>
						<td>Account Number</td>
						<td>Payee</td>
						<td>Check Date</td>
						<td>Comment</td>
						<td>Action</td>
						<td></td>
					</tr>
					<tr id="row1">
						<td>
							<input type="text" id="dupcheckNumber" name="dupcheckNumber"
								disabled="true" style="width:110px">
						</td>
						<td>
							<input type="text" id="dupamount" name="dupamount"
								size="12" disabled="true" style="width:90px">
						</td>
						<td>
							<select id="dupaccountNumber" name="dupaccountNumber" disabled="true">
							</select>
						</td>
						<td>
							<input type="text" id="duppayee" name="duppayee"
								disabled="true" style="width:150px">
						</td>
						<td>
							<input type="text" id="dupcheckDate" name="dupcheckDate"
								size="12" disabled="true" style="width:100px">
						</td>
						<td>
 							<textarea id="dupcomment"
								name="dupcomment" rows="3" disabled="true" style="width:150px"></textarea>
						</td>
						<td>
							<select id="dupaction" name="dupaction" disabled="true" onchange="resolveDuplicatePaidAction(this.value);">
								<option value="">Select Action...</option>
							</select>
						</td>
						<td>
							<button id="dupSave" name="dupSave" type="button" class="btn button" disabled="true" onclick="saveAccountInfoForDuplicateCheck();">
								Save
							</button>
						</td>
					</tr>
				</table>
			</div>
			
			<%-- <div id="ajax-loader-dup-checkImages"></div>
			<div id="error-loading-dup-checkimages" class="alert alert-danger alert-dismissable hidden">
				<button type="button" class="close" data-dismiss="alert"
					aria-hidden="true">&times;</button>
				<img alt="error loading check images"
					src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
					Error while loading check images. Please try again later.
			</div> --%>
			<table id="dupcheckImageTbl">
				<tbody>
					<tr>
					</tr>
				</tbody>
			</table>
					
			<div id="ajax-loader-dup-paymenthistory"></div>
				<div id="error-loading-dup-paymenthistory" class="alert alert-danger alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<img alt="error loading recent files"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
							Error while loading payment history. Please try again later.
				</div>
				<div id="duppaymenthistory" class="hidden">
					<h4>Payment History</h4>
					<hr/>
					<div class="table-responsive" style="margin-top: 10px;">
						<table class="table table-striped table-bordered" id="duppaymenthistoryTbl">
							<thead>
								<tr>
									<th>Sequence</th>
									<th>Date-Time</th>
									<th>User</th>
									<th>Description</th>
									<th>Comment</th>
									<th>Resulting Payment Status</th>
									<th>Created Method</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
				</div>
				<div id="dupnoPaymentHistory" class="alert alert-info alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="No payment history"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
						No Payment history to show.
				</div>
			</div>
			<div class="modal-footer">
			</div>
    	</div>
  	</div>
</div>