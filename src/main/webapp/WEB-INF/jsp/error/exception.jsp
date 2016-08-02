<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">An error has occurred</div>
			</div>
			
			<div class="panel-body">
				<div class="alert alert-danger alert-dismissable">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="No recent files"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
						The system failed to process your request with the message - <strong><c:out value="${exceptionMessage}" /></strong> 
						The transaction Id for this request was <strong><c:out value="${transactionId}" /></strong>. <a href="" onclick="window.history.back();">Go Back</a>
				</div>
			</div>
		</div>
	</div>
</div>
