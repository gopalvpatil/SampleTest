<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/filemapping.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer">
		<c:if test="${not empty FILE_MAPPING_NOT_SET}">
		   <div id="fileMappingNotSet" class="alert alert-warning" style="display:inline-block;"><strong>Warning!</strong> You have not yet created a file mapping, Please create one before uploading a file.</div>  
		</c:if>
		<div id="mappingCreated" class="hidden"><a href="filemanagement">Upload Files</a></div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">File Mapping</div>
			</div>
			<div class="panel-body">
				<form id="fileMappingForm">
					<div>
						<div id="messageBox" style="margin-bottom:10px;"></div>
						<div class="form-inline" id="createOrUpdate">
							<div class="form-group">
								<input id="newMapping" class="form-control" name="newMapping" type="text" placeholder="Mapping Name">
							</div>
							<div class="form-group">
								<button type="submit" id="createNewMappingBtn" class="button">Create</button>
							</div>
							<c:if test="${not empty fileMappings}">
								<div class="form-group">
									<label for="orText">Or </label>
								</div>
								<div class="form-group">
									<select id="fileMappingName" name="fileMappingName" onchange="displayFileMapping(this.value);">
										<option value="">Select to Update:</option>
										<c:forEach var="fileMapping" items="${fileMappings}">
											<option value="${fileMapping.id}">${fileMapping.fileMappingName}</option>
										</c:forEach>
									</select>
								</div>
							</c:if>
						</div>
						<div class="form-inline hidden" id="fileTypeSelection" style="margin-top:20px;">
							<div class="form-group">
								<label for="newMapping" class="col-sm-4 control-label">File Type:</label>
								<div class="col-sm-8">
									<select id="fileType" name="fileType">
										<option value="">Select File Type:</option>
										<option value="CSV">CSV</option>
										<option value="TXT_FIXED_WIDTH">Text Fixed Width</option>
										<option value="TXT_DELIMITER">Text With Delimiter</option>
									</select>
								</div>
							</div>
						</div>
						<div class="form-inline hidden" id="delimiterSetting" style="margin-top:20px;">
							<div class="form-group">
								<label for="delimiterSetting" class="col-sm-4 control-label">Select Delimiter:</label>
								<div class="col-sm-8">
									<select id="delimiterType" name="delimiterType">
										<option value="">Select Delimiter:</option>
										<c:forEach var="delimiter" items="${delimiters}">
											<option value="${delimiter.id}">${delimiter.name}&nbsp;&nbsp;&nbsp;&nbsp;'${delimiter.symbol}'</option>
										</c:forEach>
									</select>
								</div>
							</div>
						</div>
						<div style="margin:20px;">
							<table id="csvSetting" class="hidden">
								<c:forEach var="i" begin="1" end="7">
					    			<tr>
										<td>
											<c:out value="${i}"/>
										</td>
										<td>
											<select id="position<c:out value='${i}'/>" name="position<c:out value='${i}'/>" onchange="isDuplicate(this.id);">
												<option value="">Please Select...</option>
												<option value="ACCOUNT_NUMBER">Account Number</option>
												<option value="ROUTING_NUMBER">Routing Number</option>
												<option value="CHECK_NUMBER">Check Number</option>
												<option value="ISSUE_CODE">Issue Code</option>
												<option value="ISSUE_DATE">Issue Date</option>
												<option value="CHECK_AMOUNT">Check Amount</option>
												<option value="PAYEE">Payee</option>
											</select>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
						<div style="margin:20px;">
							<table id="txtSetting" class="hidden">
								<tr>
									<th>Field Name</th><th>Start Position</th><th>End Position</th>
								</tr>
								<tr>
									<td>Account Number</td>
									<td>
										<input type="text" class="form-control" id="accountNumberStartPos" name="accountNumberStartPos"
												size="4" placeholder="Start">
									</td>
									<td>
										<input type="text" class="form-control" id="accountNumberEndPos" name="accountNumberEndPos"
												size="4" placeholder="End">
									</td>
								</tr>
								<tr>
									<td>Routing Number</td>
									<td>
										<input type="text" class="form-control" id="routingNumberStartPos" name="routingNumberStartPos"
												size="4" placeholder="Start">
									</td>
									<td>
										<input type="text" class="form-control" id="routingNumberEndPos" name="routingNumberEndPos"
												size="4" placeholder="End">
									</td>
								</tr>
								<tr>
									<td>Check Number</td>
									<td>
										<input type="text" class="form-control" id="checkNumberStartPos" name="checkNumberStartPos"
												size="4" placeholder="Start">
									</td>
									<td>
										<input type="text" class="form-control" id="checkNumberEndPos" name="checkNumberEndPos"
												size="4" placeholder="End">
									</td>
								</tr>	
								<tr>
									<td>Issue Code</td>
									<td>
										<input type="text" class="form-control" id="issueCodeStartPos" name="issueCodeStartPos"
												size="4" placeholder="Start">
									</td>
									<td>
										<input type="text" class="form-control" id="issueCodeEndPos" name="issueCodeEndPos"
												size="4" placeholder="End">
									</td>
								</tr>	
								<tr>
									<td>Issue Date</td>
									<td>
										<input type="text" class="form-control" id="issueDateStartPos" name="issueDateStartPos"
												size="4" placeholder="Start">
									</td>
									<td>
										<input type="text" class="form-control" id="issueDateEndPos" name="issueDateEndPos"
												size="4" placeholder="End">
									</td>
								</tr>	
								<tr>
									<td>Check Amount</td>
									<td>
										<input type="text" class="form-control" id="checkAmountStartPos" name="checkAmountStartPos"
												size="4" placeholder="Start">
									</td>
									<td>
										<input type="text" class="form-control" id="checkAmountEndPos" name="checkAmountEndPos"
												size="4" placeholder="End">
									</td>
								</tr>
								<tr>
									<td>Payee</td>
									<td>
										<input type="text" class="form-control" id="payeeStartPos" name="payeeStartPos"
												size="4" placeholder="Start">
									</td>
									<td>
										<input type="text" class="form-control" id="payeeEndPos" name="payeeEndPos"
												size="4" placeholder="End">
									</td>
								</tr>
							</table>
						</div>
						<div id="buttons">
							<table>
								<tr>
									<td><button id="saveFileMappingBtn" type="button" class="hidden clearfix button"><i class="icon-fixed-width icon-save"></i> Save</button></td>
									<td><button id="deleteFileMappingBtn" type="button" class="hidden clearfix button"><i class="icon-fixed-width icon-remove"></i> Delete</button></td>
									<td><button id="cancelFileMappingBtn" type="button" class="hidden clearfix button"><i class="icon-fixed-width icon-undo"></i> Start Over</button></td>
								</tr>
							</table>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>