<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="com.westernalliancebancorp.positivepay.utility.SecurityUtility"%>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/dashboard.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>
<div>
    <div class="positivepay-column-spacer">
        <c:if test="${not empty param.emulation}">
            <div class="alert alert-info alert-dismissable">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                <img alt="Emulation Mode Ended"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" />
                You have successfully exited emulation mode and switched back to <strong><%=SecurityUtility.getPrincipal()%></strong> user.
            </div>
        </c:if>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">Dashboard</div>
                <div id="ajax-loader-errorRecords" align="center"></div>
            </div>
            <div class="panel-body">
                <form:form method="post" enctype="form-data" modelAttribute="dashboardDto" class="form-horizontal" action="searchfiles" role="form" id="viewdashboardform">
                    <div id="info" class="alert alert-success alert-dismissable" hidden="true"></div>

                    <div id="error" class="alert alert-danger alert-dismissable" hidden="true">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                    </div>
                    <input type="hidden" id="companyName" name="companyName" value="${companyNameSearchCriteria}"/>
                    <input type="hidden" id="status" name="status" value="${statusSearchCriteria}"/>
                    <input type="hidden" id="dateRange" name="dateRange" value="${dateRangeSearchCriteria}"/>
                    <!-- Search-->
                    <div class="form-group">
                        <div class="col-sm-4">
                        	<label for="companyNameSearchCriteria">Company</label>
                            <select id="companyNameSearchCriteria" name="companyNameSearchCriteria" class="pp-width-full">
                                <option value="All">All</option>
                            </select>
                        </div>
                        <div class="col-sm-2">
                            <label for="statusSearchCriteria">Status</label>
                            <select id="statusSearchCriteria" name="statusSearchCriteria" class="pp-width-full">
                                <option value="All">All</option>
                            </select>
                        </div>
                        <div class="col-sm-2">
                        	<label for="dateRangeSearchCriteria">Date Range</label>
                            <select id="dateRangeSearchCriteria" name="dateRangeSearchCriteria" class="pp-width-full">
                                <option value="All">All</option>
                            </select>
                        </div>
                        <div style="margin-left: 20px; margin-top: 13px; float:left">
                            <button id="search" name="search" value="search" type="submit" class="btn button">
                                Search
                            </button>
                        </div>
                    </div>

                    <div class="form-group" style="margin-top: 20px;">
	                    <div class="col-sm-12">
		                    <table class="table table-striped table-bordered">
		                        <thead>
		                        <tr>
		                            <th class="col-sm-1" style="width:2.5%">#</th>
		                            <th class="col-sm-4">Company</th>
		                            <th class="col-sm-1">Username</th>
		                            <th class="col-sm-2">File</th>
		                            <%--<th class="col-sm-1">File Type</th>--%>
		                            <th class="col-sm-2">Uploaded</th>
		                            <th class="col-sm-1">Status</th>
		                            <th class="col-sm-1" style="text-align: center">Received</th>
		                            <th class="col-sm-1" style="text-align: center">Loaded</th>
		                            <th class="col-sm-1" style="text-align: center">Errors</th>
		                        </tr>
		                        </thead>
		                        <tbody>
			                        <c:set var="count" value="0" scope="page" />
			                        <c:choose>
										<c:when test="${not empty dashboardDtoList}">
					                        <c:forEach var='dashboardDto' items="${dashboardDtoList}">
					                            <tr>
					                                <c:set var="count" value="${count + 1}" scope="page"/>
					                                <td class="col-sm-offset-0"><c:out value="${count}" /></td>
					                                <td style="width:33.3333%">${dashboardDto.companyName}</td>
					                                <td style="width:8.33333%">${dashboardDto.userName}</td>
					                                <td style="widht:16.6667%">${dashboardDto.originalFileName}</td>
					                                <!-- <td class="col-sm-1">${dashboardDto.fileType}</td> -->
					                                <td style="width:8.33333%">${dashboardDto.uploadedDate}</td>
					                                <td style="width:8.33333%">${dashboardDto.status}</td>
					                                <td style="text-align: center;width:8.33333%">${dashboardDto.itemsReceived}</td>
					                                <td style="text-align: center;width:8.33333%">${dashboardDto.itemsLoaded}</td>
					                                <td style="text-align: center;width:8.33333%">
					                                	<a class="errorRecordsLink" 
					                                		href="#"  
					                                		fileName="${dashboardDto.originalFileName}"
					                                		uploadedDate="${dashboardDto.uploadedDate}" 
					                                		companyName="${dashboardDto.companyName}"					                                		
					                                		href1="<%=request.getContextPath()%>/user/errorrecords/get/${dashboardDto.fileMetaDataId}">
					                                		${dashboardDto.errorRecordsLoaded}
					                                	</a>
					                                </td>
					                            </tr>
					                        </c:forEach>
				                        </c:when>
										<c:otherwise>
											 <tr><td colspan="9">No Records Found</td></tr>
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

<div class="modal" id="errorRecordsModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	<div class="modal-dialog" style="width:1200px;">
		<div class="modal-content">
			<div class="modal-header">
		        <div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal"></button>
				</div>							
		        <h3>Item Error Records:</h3>
		        <h4 class="modal-title" id="myModalLabel">Displaying results for filename <label id="fileName"> </label> uploaded on <label id="uploadedDate"> </label>
		        for the company <label id="companyName1"></label></h4>
			</div>
	      	<div class="modal-body">
				<div id="info1" class="alert alert-success alert-dismissable" hidden="true"></div>					
				<div id="error1" class="alert alert-danger alert-dismissable" hidden="true">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal"></button>				 	
				</div>	
				<div class="col-sm-12">
					<table class="table table-striped table-bordered">					
						<thead>
							<tr>
						    	<th style="width: 1.5%;" align="center">File Line Number</th>
						    	<th class="col-sm-1">Account Number</th>
						    	<th class="col-sm-1">Routing Number</th>
						    	<th class="col-sm-1" align="right">Check Number</th>
						    	<th class="col-sm-1" align="center" style="width: 1.5%;">Issue Code</th>
						    	<th class="col-sm-1" align="right">Issue Amount</th>
						    	<th class="col-sm-1">Issue Date</th>
						    	<th class="col-sm-1">Payee</th>
						    	<th class="col-sm-2">Error Type</th>
							</tr>
						</thead>
						<tbody id="itemErrorRecords">
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="modal-footer">
		</div>
	</div>
</div>
