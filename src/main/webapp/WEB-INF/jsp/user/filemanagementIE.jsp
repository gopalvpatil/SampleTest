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
	src="<%=request.getContextPath()%>/static/positivepay/js/newfilemanagement.js"
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
				<div id="validationErrorBox" class="alert alert-danger alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="Copy"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" />
					<span id="fileUploadErrorMessage"></span>
				</div>
				<!-- <div class="table-responsive" style="margin-top: 10px;">
					<table class="table table-striped table-bordered hidden" style="width: auto;" id="fileListTbl">
						<thead>
							<tr>
								<th>Remove</th>
								<th>File Name</th>
								<th>File Mapping</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div> -->
				<form:form id="uploadFile" method="post"
					enctype="multipart/form-data" modelAttribute="uploadForm"
					class="form-inline" action="newfilemanagementIE" role="form">
					
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
					
					<div class="form-group" style="margin-top: 10px;">
						<button type="button" id="addFileRow" class="btn button">Add File</button>
						<table id="fileTable">
				        <tr>
				            <td><input name="files[0]" type="file" /></td>
				        </tr>
				    </table>
						
						
						<%-- <input type="file" name="file" id="file" />
						<div class="dummyfile">
							<input
								id="filename" type="text" name="filename" class="form-control"
								placeholder="File Name" readonly="readonly" /> <a id="fileselectbutton" class="btn button">Browse</a>
							<c:if test="${not empty fileMappings}">
								<div class="form-group">
									<select id="fileMappingId" name="fileMappingId">
										<option value="">Select Template:</option>
										<c:forEach var="fileMapping" items="${fileMappings}">
											<option value="${fileMapping.id}">${fileMapping.fileMappingName}</option>
										</c:forEach>
									</select>
								</div>
							</c:if>
							<button id="addFileBtn" class="btn button">Add File</button>
						</div>	 --%>
						<input type="submit" value="Upload" />
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>