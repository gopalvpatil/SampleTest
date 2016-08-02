<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page
	import="com.westernalliancebancorp.positivepay.utility.SecurityUtility"%>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/dashboard.js"
	type="text/javascript"></script>
<script>

</script>	
<div>
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Error Page</div>
				<div id="ajax-loader-errorRecords" align="center"></div>
			</div>
			<div class="panel-body">
				<form:form method="post" enctype="form-data"
					modelAttribute="dashboardDto" class="form-horizontal"
					action="searchfiles" role="form" id="jobError">
					<div id="info" class="alert alert-success alert-dismissable"
						hidden="true"></div>

					<div id="error" class="alert alert-danger alert-dismissable"
						hidden="true">
						<button type="button" class="close" data-dismiss="alert">&times;</button>
					</div>
					<input type="hidden" id="companyName" name="companyName"
						value="${companyNameSearchCriteria}" />
					<input type="hidden" id="status" name="status"
						value="${statusSearchCriteria}" />
					<input type="hidden" id="dateRange" name="dateRange"
						value="${dateRangeSearchCriteria}" />
					<!-- Search-->
					<div class="form-group" style="margin-top: 20px;">
						<div class="col-sm-12">
							<table class="table table-striped table-bordered">
								<thead>
									<tr>
										<th style="width: 1.5%;" align="center">File Line Number</th>
										<th class="col-sm-1">Account Number</th>
										<th class="col-sm-1">Trace Number</th>
										<th class="col-sm-1" align="right">Check Number</th>
										<th class="col-sm-1" align="center" style="width: 1.5%;">Item Type</th>
										<th class="col-sm-1" align="center">Amount</th>
										<th class="col-sm-1">Paid Date</th>
										<th class="col-sm-1">Stop Date</th>
										<th class="col-sm-1">Stop Presented Date</th>
										<th class="col-sm-2">Error Type</th>
									</tr>
								</thead>
								<tbody>
									<c:set var="count" value="0" scope="page" />
									<c:choose>
										<c:when test="${not empty itemErrorList}">
											<c:forEach var='dto' items="${itemErrorList}">
												<tr>
													<td style="width: 1.5%;" align="center">${dto.fileLineNumber}</td>
													<td style="width: 8.33333%">${dto.accountNumber}</td>
													<td style="widht: 16.6667%">${dto.traceNumber}</td>
													<td style="width: 8.33333%">${dto.checkNumber}</td>
													<td style="text-align: center; width: 8.33333%">${dto.itemType}</td>
													<td style="text-align: right; width: 8.33333%">${dto.amount}</td>
													<td style="text-align: center; width: 8.33333%">${dto.paidDate}</td>
													<td style="text-align: center; width: 8.33333%">${dto.stopDate}</td>
													<td style="text-align: center; width: 8.33333%">${dto.stopPresentedDate}</td>
													<td style="text-align: left; width: 8.33333%">${dto.exceptionTypeName}</td>
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<tr>
												<td colspan="9">No Records Found</td>
											</tr>
										</c:otherwise>
									</c:choose>
								</tbody>
							</table>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>

