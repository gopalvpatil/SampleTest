<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.westernalliancebancorp.positivepay.utility.SecurityUtility" %>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/filemanagement.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/static/positivepay/js/filemanagementdialogue.js"
	type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Upload Files</div>
			</div>
			<div class="panel-body">
				<form:form id="uploadFile" method="post"
					enctype="multipart/form-data" modelAttribute="uploadedFile"
					class="form-inline" action="filemanagement" role="form">
					<c:if test="${not empty validationError}">
						<div class="alert alert-danger alert-dismissable">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<img alt="Copy"
								src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" />
							<form:errors path="file" />
						</div>
					</c:if>
					<c:if test="${not empty duplicateUpload}">
						<div class="alert alert-warning alert-dismissable">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<img alt="Copy"
								src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" />
							<c:out value="${duplicateUpload}" />
						</div>
					</c:if>
					<c:if test="${not empty uploadSuccess}">
						<div class="alert alert-success alert-dismissable">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<img alt="Copy"
								src="<%=request.getContextPath()%>/static/positivepay/images/icons/success.png" />
							<c:out value="${uploadSuccess}" /><br/>
							<c:if test="${not empty warningMessages}">
								It also appears that: <br/>
								<ul>
								<c:forEach var="warning" items="${warningMessages}">
									<li>${warning}</li>
								</c:forEach>
								</ul>
							</c:if>
						</div>
					</c:if>
					<c:if test="${not empty errorWhileUpload}">
						<div class="alert alert-danger alert-dismissable">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<img alt="Copy"
								src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" />
							<strong>File Upload Failed!</strong><br/>
							The file contains data that does not match your file mapping. Please correct the data or change the file mapping and try to upload again.
						</div>
					</c:if>
					<div class="col-sm-offset-1 col-sm-12 form-group">
						<input type="file" name="file" id="file" />
						<div class="dummyfile">
							<input
								id="filename" type="text" name="filename" class="form-control"
								placeholder="Select a file..." readonly="readonly" /> <a id="fileselectbutton" class="btn button">Browse</a>
							<c:if test="${not empty fileMappings}">
								<div class="form-group">
									<select id="fileMappingId" name="fileMappingId"
										onchange="displayFileMapping(this.value);">
										<option value="">Select Template:</option>
										<c:forEach var="fileMapping" items="${fileMappings}">
											<option value="${fileMapping.id}">${fileMapping.fileMappingName}</option>
										</c:forEach>
									</select>
								</div>
							</c:if>
							<button class="btn button" onclick="submitForm()"> Upload</button>
						</div>
						<p class="text-muted">
							<i class="icon-fixed-width icon-info-sign"></i> Accepted file
							formats: CSV, TXT
						</p>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Recent File Uploads</div>
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
					<div class="table-responsive">
						<table id="fileSelectionCriteriaTbl">
							<tr>
								<td>
									<input type="text" class="form-control" id="uploadDate"
									name="uploadDate" size="8" placeholder="Upload Date">
								</td>
								<td>
									<a href="#" id="calendarBtn"><img alt="Copy"
										src="<%=request.getContextPath()%>/static/positivepay/images/icons/calendar-sprite.png" /></a>
								</td>
								<td>
									<select id="accountNumber" name="accountNumber"
										onchange="filterRecentFiles();">
										<option value="">Select Account No.</option>
										<c:forEach var="account" items="${userAccounts}">
											<option value="${account.number}">${account.number}</option>
										</c:forEach>
									</select>
								</td>
								<td>
									<select id="dateRange" name="dateRange"
									onchange="filterRecentFiles();">
										<option value="">All</option>
										<option value="0">Today</option>
										<option value="1">Yesterday</option>
										<option value="7">Last 7 Days</option>
										<option value="14">Last 14 Days</option>
										<option value="30">Last 30 Days</option>
									</select>
								</td>
								<td>
									<button class="btn button" onclick="clearFilter();"> Clear</button>
								</td>
							</tr>
						</table>
					</div>
					<div class="table-responsive" style="margin-top: 10px;">
						<table class="table table-bordered" id="recentFilesTbl">
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
	</div>
</div>
<%@include file="filemanagementdialogue.jsp"%>

<script>

  function submitForm()
  {
	  if(isIE () == 9){
		  setTimeout(function(){document.forms[0].submit();},100);  
	  } else {
		  document.forms[0].submit();
	  }
  }
  
  function isIE () {
	  var myNav = navigator.userAgent.toLowerCase();
	  return (myNav.indexOf('msie') != -1) ? parseInt(myNav.split('msie')[1]) : false;
  }

</script>