<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/datatable/css/jquery.dataTables.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/datatable/css/dataTables.fixedHeader.css" type="text/css" />
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/paymentsinfo.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/thirdparty/loupe/js/jquery.loupe.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/paymentslist.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>	
<script	src="<%=request.getContextPath()%>/static/thirdparty/datatable/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/datatable/js/dataTables.fixedHeader.min.js" type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
                <div class="panel-title">Payments Info</div>
            </div>
			<div class="panel-body">
				<div>
					<strong>Displaying search results for payments:</strong>
				</div>
				<h4 class="clearfix">Payment List <span id="paymentListBadge" class="badge"></span></h4>
				<div id="ajax-loader-paymentList"></div>
				<div id="error-loading-payments" class="alert alert-danger alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<img alt="error loading payment list"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
							Error while loading payment list. Please try again later.
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

<!-- Pop up to show -->
<div class="modal fade bs-example-modal-lg" id="checkDetailPopUp" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg positivepay-popup">
    <div class="modal-content">
		<div class="modal-header">
			<div style="float: right">
				<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#checkDetailPopUp" aria-hidden="true"></button>
			</div>
			<h4 style="display:inline;" id="checkNumberValue"></h4>&nbsp;&nbsp;
				<span>
					<a id="print" href="#">
						<span class="add-on">
							<i class="icon-small icon-print"></i>
						</span>
					</a>
				</span>
		</div>
		<div class="modal-body">
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
		</div>
		<div class="modal-footer">
		</div>
    </div>
  </div>
</div>