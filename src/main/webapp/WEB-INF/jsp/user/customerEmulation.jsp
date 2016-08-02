<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/customerEmulation.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Customer Emulation</div>
			</div>
			
			<div class="panel-body">
				<form id="customerEmulationForm" class="form-inline" role="form" method="GET" action="emulation/create">
				  	<div class="form-group">
					    <label for="bankName">Bank Name</label>
					    <select id="bankName" name="bankName" onchange="loadCompaniesByBankId(this.value);">
							<option value="">Select Bank...</option>
							<c:forEach var="bank" items="${banks}">
								<option value="${bank.id}">${bank.name}</option>
							</c:forEach>
						</select>
				  	</div>
				  	<div class="form-group">
					    <label for="companyName">Company Name</label>
					    <select id="companyName" name="companyName" onchange="loadUsersByCompanyId(this.value);">
							<option value="">Select Bank First...</option>
						</select>
				  	</div>
				  	<div class="form-group">
					    <label for="userName">User Name</label>
					    <select id="userName" name="userName">
							<option value="">Select Company First...</option>
						</select>
				  	</div>
				  	<button type="button" id="emulateBtn" class="btn button">EMULATE</button>
				</form>
			</div>
		</div>
	</div>
</div>