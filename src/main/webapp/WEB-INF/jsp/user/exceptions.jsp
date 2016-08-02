<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/datatable/css/jquery.dataTables.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/datatable/css/dataTables.fixedHeader.css" type="text/css" />
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/thirdparty/loupe/js/jquery.loupe.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/paymentslist.js" type="text/javascript"></script>	
<script src="<%=request.getContextPath()%>/static/positivepay/js/exceptions.js" type="text/javascript"></script>	
<script src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>	
<script	src="<%=request.getContextPath()%>/static/thirdparty/datatable/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/datatable/js/dataTables.fixedHeader.min.js" type="text/javascript"></script>
<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Exceptions</div>
			</div>
			
			<div class="panel-body">
				<select id="showAccountNumber" name="showAccountNumber" onchange="filterExceptionDecisioning(this.value);">
					<option value="">Showing All</option>
					<c:forEach var="account" items="${userAccounts}">
						<option value="${account.number}">${account.number}</option>
					</c:forEach>
				</select>
				<h4>Exception Resolution</h4>
				<div id="exceptionResolutionMessage"></div>
				<c:choose>
					<c:when test="${not empty outsideResolutionWindow}">
						<div class="alert alert-info alert-dismissable">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<img alt="No recent files"
								src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
								Currently you are outside the window of exception resolution. Your exception resolution window is 
								<strong>${decisionWindow.start}-${decisionWindow.end} ${decisionWindow.timezone}</strong>. 
								Please visit during that period.
						</div>
					</c:when>
					<c:when test="${not empty checksInException}">
						<div class="row" id="noOfExceptionsMessage">
							<div class="col-md-9"><p class="text-danger">
							<strong>You have <span id="exceptionDecisionBadge" class="badge">${fn:length(checksInException)}</span>
							 items that need to be resolved. You have from ${decisionWindow.start} ${decisionWindow.timezone} to ${decisionWindow.end} ${decisionWindow.timezone}.</strong></p></div>
							<div class="col-md-2 nopadding"><label class="control-label col-sm-7">Pay All <input type="checkbox" id="selectAll" name="checkbox" value="true"/></label></div>
						</div>
						<div id="messageUponExceptionResolution" class="alert alert-info alert-dismissable hidden">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<img alt="Exceptions Resolved"
								src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
								You have no exceptions to resolve.
						</div>
						<div id="exceptionDecisioningErrors" class="alert alert-danger" style="display:none;">
							<strong>Error!</strong> Please select the highlighted decision/reason value(s).
						</div>
						<table id="exceptionDecisioningTbl" class="table table-striped table-bordered">
							<thead>
								<tr>
									<th>Account Number</th><th>Check Number</th><th>Paid Amount</th><th>Payee</th><th>Exception Type</th><th>Decision</th><th>Reason</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="checkInException" items="${checksInException}" varStatus="theCount">						
								<tr id="exception${theCount.count}">
									<td class="hidden">${checkInException.id}</td>
									<td>${checkInException.accountNumber}</td>
									<td align="right" id="exceptionalCheckNumber${theCount.count}" onclick="showCameraPopUp(${checkInException.id}, ${checkInException.checkNumber});">${checkInException.checkNumber} <a href="#"><i class="icon-fixed-width icon-camera"></i></a></td>
									<td align="right">
										<c:if test="${not empty checkInException.paidAmount}">
											$${checkInException.paidAmount}
										</c:if>
									</td>
									<td>${checkInException.payee}</td>
									<td>${checkInException.exceptionType}</td>
									<td>
										<select id="decision${theCount.count}" name="decision" style="width:150px;" onchange="populateReasonsDropdown(this.value, $(this).closest('tr').attr('id'));">
											<option value="0">Select Decision</option>
											<c:forEach var="entry" items="${availableActionsForChecksMap}">
												<c:if test="${entry.key == checkInException.id}">
													<c:forEach var="action" items="${entry.value}">
											  			<option value="${action.key}">${action.value}</option>
													</c:forEach>
												</c:if>
											</c:forEach>
										</select>
									</td>
									<td>
										<select id="reason${theCount.count}" name="reason" style="width:200px;">
											<option value="0">Select decision first</option>
										</select>
									</td>
								</tr>
								</c:forEach>
								<c:if test="${not empty availableActionsForChecksMap}">
  									<input type="hidden" id="mapsize" value="<c:out value="${fn:length(availableActionsForChecksMap)}" />">
								</c:if>
							</tbody>
						</table>
						<div class="pull-right">
							<button id="resolveExceptionsBtn" type="button" class="btn button">Resolve Exceptions</button>
						</div>
					</c:when>
					<c:otherwise>
						<div class="alert alert-info alert-dismissable">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<img alt="No recent files"
								src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
								You have no exceptions to resolve.
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>

<!-- Pop up to show -->
<div class="modal fade bs-example-modal-lg" id="checkDetailPopUp" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg positivepay-popup">
    <div class="modal-content">
		<div class="modal-header">
			<button type="button" class="pp-sprite-close-window" style="float: right" data-dismiss="modal" aria-hidden="true"></button>
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