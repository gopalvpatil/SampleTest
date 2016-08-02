<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!-- Start Show Report Report Modal -->
<div class="modal" id="reportShowModal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog" style="width:1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<label class="modal-title">Run Report/Extract</label>
				<div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#reportShowModal"></button>
				</div>
			</div>
			<div class="modal-body">	
				<form:form id="reportShowForm" method="POST" role="form" class="form-horizontal" 
					action="${pageContext.request.contextPath}/report/save" target="_blank">			
					<div class="form-group">
						<label for="reportName" class="col-sm-1 control-label">Name</label>
						<div class="col-sm-4">
							<input type="text" class="form-control" id="reportName" name="reportName" required/>
							<input type="hidden" id="reportId" name="reportId"/> 
							<input type="hidden" id="templateId" name="templateId"/>
							<!-- <input type="hidden" class="form-control" id="templateNameText"/> -->
						</div>
						<label for="reportTemplateName" class="col-sm-2 control-label">Template</label>
						<div class="col-sm-4">
							<input type="text" class="form-control" id="templateName" name="templateName" disabled/>
						</div>
					</div>	
					<div class="form-group">	
						<label for="asOfDate" class="col-sm-1 control-label">As of Date</label>
						<div id="asOfDateDiv" class="col-sm-2 date-input">
							<input class="pp-width-full" type="text" id="asOfDate" name="asOfDate" required placeholder="As of Date"/>								
							<select class="pp-width-full" style="visibility: hidden;" id="asOfDateSymbolicValue" name="asOfDateSymbolicValue">
 							</select>
						</div>	
						<div class="col-sm-2">
							<label id="labelAsOfDateIsSymbolic" style="margin-top: 5px;">
								<input type="checkbox" name="asOfDateIsSymbolic" id="asOfDateIsSymbolic" style="margin-right: 4px;">&nbsp;Symbolic Date
							</label>
						</div>
						<label for="outputFormat" class="col-sm-2 control-label">Output Format</label>
						<div class="col-sm-2">
							<select id="outputFormat" name="outputFormat" required>
 								<option>Output Format</option>
 								<option value="CSV">CSV</option>
 								<option value="PDF">PDF</option>
 								<option value="XLS">XLS</option>
 								<option value="TIFF">TIFF</option>
							</select>
						</div>	
					</div>
					<c:if test="${displayBankSelection}">									
						<div class="form-group" style="height: 188px; overflow-y: auto;">
							<div class="col-sm-12">
								<table id="tableBanks" class="table table-striped table-bordered">
									<thead>
										<tr>
											<th class="col-sm-1"><input type="checkbox" id="selectAllBanks" value="false"/></th>
											<th>Bank Name</th>
										</tr>
									</thead>
									<tbody>
				   						<c:choose>
											<c:when test="${fn:length(banks) gt 0}">
												<c:forEach var='bank' items="${banks}">
										  			<tr>
														<td>
															<input type="checkbox" name="bankIds" value="${bank.id}"/>
														</td>
														<td>${bank.name}</td>
													</tr>
						   						</c:forEach>
						   					</c:when>
						   					<c:otherwise>
						   						<tr>
						   							<td></td>
													<td>
														There are no banks to display.
													</td>
												</tr>
						   					</c:otherwise>
						   				</c:choose>
									</tbody>
								</table>
							</div>
						</div>
					</c:if>
					<div class="form-group">
						<label for="companyIds" class="error"></label>
					</div>
					<div class="form-group" style="max-height: 188px; overflow-y: auto;">
						<div class="col-sm-12">
							<table id="tableCompanies" class="table table-striped table-bordered">
								<thead>
									<tr>
										<th class="col-sm-1"><input type="checkbox" id="selectAllCompanies" value="false"/></th>
										<th>Company Name</th>
									</tr>
								</thead>
								<tbody>
									<c:choose>
										<c:when test="${displayBankSelection}">
											<tr>
												<td>
												</td>
												<td>Please select a bank to display companies.</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:choose>
 													<c:when test="${fn:length(companies) gt 0}">
													<c:forEach var='company' items="${companies}">
											  			<tr>
															<td>
																<input type="checkbox" name="companyIds" value="${company.id}"/>
															</td>
															<td>${company.name}</td>
														</tr>
							   						</c:forEach>
							   					</c:when>
							   					<c:otherwise>
							   						<tr>
							   							<td></td>
														<td>
															There are no companies to display.
														</td>
													</tr>
							   					</c:otherwise>
							   				</c:choose>
							   			</c:otherwise>
							   		</c:choose>
								</tbody>
							</table>
						</div>
					</div>
					<div class="form-group">
						<label for="accountIds" class="error"></label>
					</div>
					<div class="form-group" style="max-height: 188px; overflow-y: auto;">
						<div class="col-sm-12">
							<table id="tableAccounts" class="table table-striped table-bordered">
								<thead>
									<tr>
										<th class="col-sm-1"><input type="checkbox" id="selectAllAccounts" value="false"/></th>
										<th>Account Number</th>
										<th>Account Name</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>	
					<div id="optionalParameters">
					</div>
					<div class="form-actions" align="right">		
						<button type="submit" class="btn button">
							Run Report
						</button>
					</div>
				</form:form>						
			</div>				
		</div>
	</div>
</div>
<!-- End Show Report Modal Dialog -->

<!-- Start Delete Report Report Modal  s-->
<div class="modal" id="reportDeleteModal" tabindex="-1" role="dialog">
	<div class="modal-dialog" style="width:700px;">
		<div class="modal-content">
			<div class="modal-header">
				<label class="modal-title">Delete Report</label>
				<div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal" data-target="#reportDeleteModal"></button>
				</div>
			</div>
			<div class="modal-body">	
				<input type="hidden" id="deleteReportId" name="deleteReportId" /> 
				<div class="form-group">
					<label>Would you like to delete </label>
					<label id="deleteReportName"></label>
				</div>	
				<div class="form-actions" align="right">		
					<button id="reportButtonDelete" class="btn button">
					 	Yes
					</button>
					<button type="button" class="btn button" data-dismiss="modal" data-target="#reportDeleteModal">
						No
					</button>
				</div>		
			</div>				
		</div>
	</div>
</div>
<!-- End Delete Report Modal Dialog -->

