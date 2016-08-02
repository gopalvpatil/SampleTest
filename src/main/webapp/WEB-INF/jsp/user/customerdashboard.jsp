<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.westernalliancebancorp.positivepay.utility.SecurityUtility"%>
<jsp:useBean id="now" class="java.util.Date" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/bootstrap/css/bootstrap-multiselect.css" type="text/css" />
<script src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/bootstrap/js/bootstrap-multiselect.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/datepicker.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/customerdashboard.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/dashboardpayments.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/report.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/filemanagementdialogue.js" type="text/javascript"></script>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>

<script type="text/javascript">
	//Load the Visualization API and the piechart and columnchart package.
	google.load('visualization', '1.0', {'packages':['corechart']});
	
	var accountList = new Array();
	<c:forEach var="userAccount" items="${userAccounts}" varStatus="accountStatus">
		accountList['${userAccount.id}'] = '${userAccount.number}';
	</c:forEach>
</script>

<%@include file="../report/reportdialogs.jsp"%>
<%@include file="filemanagementdialogue.jsp"%>

<div>
    <div class="positivepay-column-spacer">
    	<c:if test="${not empty param.emulation}">
            <div class="alert alert-info alert-dismissable">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                You have successfully exited emulation mode and switched back to <strong><%=SecurityUtility.getPrincipal()%></strong> user.
            </div>
        </c:if>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">Account Info | <span>Current as of <fmt:formatDate value="${now}" type="both" dateStyle="long" timeStyle="short"/></span></div>
            </div>
            <div class="panel-body">
                <div id="ajax-loader-accountInfo"></div>
				<div id="error-loading-accountInfo" class="alert alert-danger alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="error loading account info"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png"/> 
						Error while loading account information. Please try again later.
				</div>
				<div id="accountInfo" class="hidden">
					<p id="noOfExceptionsMessage" class="text-danger"><strong>You have <span id="exceptionDecisionBadge" class="badge"></span> items that need to be resolved. You have from <span id="decisionWindowStartTime"></span> to <span id="decisionWindowEndTime"></span>.</strong></p>
					<div class="table-responsive" style="margin-top: 10px;">
						<table class="table table-striped table-bordered" id="accountInfoTbl">
							<thead>
								<tr>
									<th>S.No.</th>
									<th>Account Name</th>
									<th>Account Number</th>
									<th>Bank Name</th>
									<th>Account Type</th>
									<th>Exceptions</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
					<div id="exceptionsBtn" class="pull-right hidden">
						<a style="float: right" class="btn button" href="<%=request.getContextPath()%>/user/exceptions">Exceptions</a>
					</div>
				</div>
				<div id="noAccountInfo" class="alert alert-info alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="No Account Info"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
						There are no accounts with exceptions.
				</div>
            </div>
        </div>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">Payment Information | <span>Current as of <span id="paymentRefreshData"></span></span>
                </div>
            </div>
            <div class="panel-body">
            	<div class="col-sm-3 pp-width-full">
            		<label for="forAccount" style="margin-top:8px;">Account</label>
		            <div>
		            	<select id="accountsChartSelect" name="accountsChartSelect" class="pull-left">
		                	<option value="">ALL</option>
		                	<c:forEach var="accountNumber" items="${accountNumbers}" varStatus="accountStatus">
		                		<option value="${accountNumber}"><c:out value="${accountNumber}" /></option>
		                	</c:forEach>
		                </select>
	               </div>
                </div>
                <div id="ajax-loader-payments"></div>
            	<div id="error-loading-paymentInfo" class="alert alert-danger alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
						<img alt="error loading account info"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
							Error while loading payment information. Please try again later.
				</div>
                <!--Div that will hold the pie chart-->
                <div class="row">
                	<div class="col-md-6 text-center"><h3>Amount</h3></div>
	    			<div class="col-md-6 text-center"><h3>Count</h3></div>
                </div>
                <div class="row" >
                	<div class="col-md-3" id="amountsPiechart_div"></div>
	    			<div class="col-md-3" id="amountsColumnchart_div"></div>
	    			<div class="col-md-3" id="countsPiechart_div"></div>
	    			<div class="col-md-3" id="countsColumnchart_div"></div>
                </div>
            </div>
        </div>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">File Management | <span>Current as of <fmt:formatDate value="${now}"  type="both" dateStyle="long" timeStyle="short"/></span></div>
            </div>
            <div class="panel-body">
                <div id="ajax-loader-recentFiles"></div>
				<div id="error-loading-recentFiles" class="alert alert-danger alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<img alt="error loading recent files"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
							Error while loading recent files. Please try again later.
				</div>
				<div id="recentFiles" class="hidden">
					<h4>Recent Uploads</h4>
					<hr/>
					<div class="table-responsive" style="margin-top: 10px;">
						<table class="table table-striped table-bordered" id="recentFilesTbl">
							<thead>
								<tr>
									<th>S. No.</th>
									<th>Upload Date</th>
									<th>File Name</th>
									<th>Received</th>
									<th>Loaded</th>
									<th>Errors</th>
									<c:if test="${not empty DOWNLOAD_FILES_PERMISSION}">
										<th id="actionHeader">Action</th>
									</c:if>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
					<div class="pull-right">
						<a style="float: right" class="btn button" href="<%=request.getContextPath()%>/user/filemanagement">File Management</a>
					</div>
				</div>
				<div id="noRecentFiles" class="alert alert-info alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="No recent files"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
						You have not uploaded any files recently.
				</div>
            </div>
        </div>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">Reports & Extracts | <span>Current as of <fmt:formatDate value="${now}" type="both" dateStyle="long" timeStyle="short"/></span></div>
            </div>
            <div class="panel-body">
                <div id="ajax-loader-reportsExtracts"></div>
				<div id="error-loading-reportsExtracts" class="alert alert-danger alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="error loading reports and extracts"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
						Error while loading reports and extracts. Please try again later.
				</div>
				<div id="reportsExtracts" class="hidden">
					<div class="table-responsive" style="margin-top: 10px;">
						<table class="table table-striped table-bordered">
							<thead>
								<tr>
									<th>Type</th>
									<th>Package</th>
									<th>Template</th>
									<th>Run</th>
								</tr>
							</thead>
							<tbody id="reportsExtractsTable">
							</tbody>
						</table>
					</div>
					<div class="pull-right">
						<a style="float: right" class="btn button" href="<%=request.getContextPath()%>/report/view">Reports/Extracts</a>
					</div>
					<div id="noReportsAndExtracts" class="alert alert-info alert-dismissable hidden">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						<img alt="No recent files"
							src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
							There are no reports or extracts to display.
					</div>
            	</div>
            </div>
        </div>
    </div>
</div>

<!-- Data Criteria modal Modal -->
<div class="modal" id="dataCriteria" tabindex="-1" role="dialog" aria-labelledby="dataCriteriaModalLabel" aria-hidden="true">
	<div class="modal-dialog" style="width: 900px; height: auto">
		<div class="modal-content">
	    	<div class="modal-header">
	        	<label id="dataCriteriaModalLabel" class="modal-title">Data Criteria</label>
	        	<div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#dataCriteria" aria-hidden="true"></button>
				</div>
			</div>
			
	      	<div class="modal-body">
	      		<form:form id="dateCriteriaForm" method="POST" class="form-horizontal">
			      	<div id="dataCriteriaError" class="alert alert-danger alert-dismissable hidden">
			      		<ul id="errors"></ul>
			      	</div>
		      		<div class="form-group">
			      		<select id="accountNumbers" name="accountNumbers" size="3" multiple class="form-control">
			      			<c:forEach var="accountNumber" items="${accountNumbers}" varStatus="accountStatus">
			                	<option value="${accountNumber}"><c:out value="${accountNumber}" /></option>
			                </c:forEach>
			      		</select>
		      		</div>
		      		<div class="form-group">
			      		<select id="paymentStatusTypes" name="paymentStatusTypes" size="3" multiple class="form-control">
			      			<c:forEach var="paymentStatus" items="${paymentStatusList}" varStatus="status">
			      				<option value="${paymentStatus}"><c:out value="${paymentStatus}" /></option>
			      			</c:forEach>
			      		</select>
		      		</div>
		      		<div class="form-group">
			      		<select id="paidExceptionStatus" name="paidExceptionStatus" multiple class="pp-width-full">
			      			<option value="OPEN">Open</option>
			      			<option value="CLOSED">Closed</option>
			      		</select>
		      		</div>
		      		<div class="form-group">
		      			<label for="fromCheckNumber" class="col-sm-2 control-label">Check No.</label>
						<div class="col-sm-3">
							<input type="text" id="fromCheckNumber" name="fromCheckNumber" class="form-control">
						</div>
						<label for="toCheckNumber" class="control-label" style="float: left">  To  </label>
						<div class="col-sm-3">
							<input type="text" id="toCheckNumber" name="toCheckNumber" class="form-control">
						</div>
		      		</div>
		      		<div class="form-group">
		      			<label for="amountRelationalOperator" class="col-sm-2 control-label">Amount Type</label>
						<div class="col-sm-3">
							<select id="amountType" name="amountType" class="pp-width-full">
								<option value="">Select</option>
				      			<c:forEach var="paymentStatus" items="${paymentStatusSubList}" varStatus="status">
				      				<option value="${paymentStatus}"><c:out value="${paymentStatus}" /></option>
				      			</c:forEach>
				      		</select>
				      	</div>
						<div class="col-sm-3" style="float: left">
						    <input type="text" id="fromAmount" name="fromAmount" class="form-control"> 
						</div>
						<label for="toAmount" class="control-label" style="float: left"> To </label>
						<div class="col-sm-3" style="float: left;">
						    <input type="text"  id="toAmount" name="toAmount" class="form-control">
						</div>
		      		</div>
		      		<div class="form-group">
	      				<label for="dateTypeCondition" class="col-sm-2 control-label">Date Type</label>
					    <div class="col-sm-3">
						    <select id="dateType" name="dateType" class="pp-width-full">
						    	<option value="">Select</option>
				      			<c:forEach var="paymentStatus" items="${paymentStatusSubList}" varStatus="status">
				      				<option value="${paymentStatus}"><c:out value="${paymentStatus}"/></option>
				      			</c:forEach>
				      		</select>
			      		</div>
			      		<div class="col-sm-3">
			      			<div id="fromDateDiv" class="pp-width-full date-input">
					    		<input type="text" id="fromDate" name="fromDate" class="form-control" placeholder="mm/dd/yyyy"> 
					    	</div>
					    </div>
					    <label class="control-label" style="float: left"> To </label>
					    <div class="col-sm-3">
					    	<div id="toDateDiv" class="pp-width-full date-input">
					    		<input type="text" id="toDate" name="toDate" class="form-control" placeholder="mm/dd/yyyy">
					    	</div>
					    </div>
		      		</div>
		      		<div class="modal-footer">
		        		<button id="dataCriteriaBtn" class="btn button">Search</button>
		      		</div>
		      	</form:form>
	    	</div>
		</div>
	</div>
</div>