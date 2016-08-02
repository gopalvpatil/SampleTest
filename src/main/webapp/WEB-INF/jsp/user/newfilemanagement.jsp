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
				<div class="table-responsive" style="margin-top: 10px;">
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
				</div>
				<form id="multiUploadForm" class="form-inline" role="form">
					<div class="form-group" style="margin-top: 10px;">
						<input type="file" name="file" id="file" />
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
						</div>	
						<button id="uploadFilesBtn" style="display:none; clear:both;" class="btn button">Upload</button>
					</div>
				</form>
				<div style="display:none; margin-top:10px;" id="uploadAgainLink"><a href="#">Upload Again?</a></div>
			</div>
		</div>
	</div>
</div>