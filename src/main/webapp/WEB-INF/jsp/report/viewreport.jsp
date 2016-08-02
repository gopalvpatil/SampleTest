<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/report.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/datepicker.js" type="text/javascript"></script>

<%@include file="reportdialogs.jsp"%>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Reporting / Extracts</div>
			</div>
			<div class="panel-body">
				<table id="reportListTable" class="table table-striped table-bordered">
					<thead>
						<tr>
							<th>Favorite</th>
							<th>Name</th>
							<th>Type</th>
							<th>Template</th>
							<th>Date Created</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var='reportDto' items="${reportDtoList}">
							<tr <c:if test="${reportDto.reportId ne -1}">id="reportListTableRow${reportDto.reportId}"</c:if>>						
								<td>
									<c:if test="${reportDto.isFavorite}">
										<div class="pp-sprite pp-sprite-gold-star"></div>
									</c:if>
									<c:if test="${not reportDto.isFavorite}">
										<div class="pp-sprite pp-sprite-silver-star"></div>
									</c:if>
								</td>
								<td>${reportDto.reportName}</td>
								<td>${reportDto.reportType}</td>
								<td>${reportDto.templateName}</td>
								<td>${reportDto.dateCreated}</td>
								<td>
									<div class="pp-width-full" style="width: 75px; float: left;">
								    	<div style="width: 26px; height: 24px; float: left; margin-right: 4px;">
											<c:if test="${reportDto.isFavorite}">
					                        	<button data-toggle="modal" 
					                        		data-report-name="${reportDto.reportName}" 
					                        		data-report-id="${reportDto.reportId}"
					                        		class="delete-reportDialog pp-sprite-delete btn">
					                            </button>	         
					                    	</c:if>
				                    	</div>
								    	<div style="float: left; margin-right: 4px;">
				                        	<button data-toggle="modal"
				                        		data-report-id="${reportDto.reportId}"
					                        	data-report-name="${reportDto.reportName}" 
				                        		data-template-name="${reportDto.templateName}" 
				                        		data-template-id="${reportDto.templateId}"
				                        		data-output-format="${reportDto.outputFormat}"
				                        		data-as-of-date="${reportDto.asOfDate}"
				                        		data-as-of-date-is-symbolic="${reportDto.asOfDateIsSymbolic}"
				                        		data-as-of-date-symbolic-value="${reportDto.asOfDateSymbolicValue}"
				                        		class="open-reportDialog pp-sprite-run btn">
				                            </button>	                             
				                        </div>
				                    </div>
		                        </td>                       
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

