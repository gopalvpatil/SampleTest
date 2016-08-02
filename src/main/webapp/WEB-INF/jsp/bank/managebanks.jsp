<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script src="<%=request.getContextPath()%>/static/positivepay/js/bank/managebanks.js" type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">
					Manage Banks
				</div>
			</div>
			<div class="panel-body">
				<div class="panel-group">
					<a href="<%=request.getContextPath()%>/user/banksetup" type="button" class="btn button">Add Bank</a>
				</div>
				
				<div class="panel-group" id="accordion">
					<c:forEach var="currentbank" items="${banks}" varStatus="bankstatus">
						<div class="panel panel-default" >
						    <div class="panel-heading">
						      <h4 class="panel-title">
						        <a data-toggle="collapse" data-parent="#accordion" href="#collapse_bank_${currentbank.id}" bankId="${currentbank.id}" companiesFetched="false">
						         	${currentbank.name}
						        </a>
						        <a href="<%=request.getContextPath()%>/user/banksetup?bankId=${currentbank.id}" style="float: right">Edit Bank</a>
						      </h4>
						    </div>
						    <div id="collapse_bank_${currentbank.id}" class="panel-collapse collapse ">
						      <div class="panel-body">
						      	<div class="panel-group" id="accordion_bank_${currentbank.id}" >
							      	<!-- <div id="companyListTemplatePlaceHolder"></div>  -->
						      	</div>
							</div>
						  </div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>
</div>
