<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/login.js" type="text/javascript"></script>

<div class="row" id="systemMessage" style="display: none;">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">System Message</div>
			</div>
		
			<div class="panel-body">
				<label id="labelSystemMessage" class="system-message-text"></label>
			</div>
		</div>	
	</div>
</div>

<div class="row">
	<div class="positivepay-column-spacer">
		<div>
			<c:if test="${empty param}">
				<div style="margin-bottom: 10px;">Please sign in below to continue!</div>
			</c:if>
			<c:if test="${not empty param.error}">
				<div class="alert alert-danger alert-dismissable col-sm-6">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
					<img alt="Login not successful"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> Your login attempt was not successful, please try again.
				</div>
			</c:if>
			<c:if test="${not empty param.sessionExpired}">
				<div class="alert alert-danger alert-dismissable col-sm-8">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
					<img alt="Session expired!"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> Unfortunately your session has expired, please login below to continue.
				</div>
			</c:if>
			<c:if test="${not empty param.logout}">
				<div class="alert alert-success alert-dismissable col-sm-6">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
					<img alt="Logout success"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" />
						You have been successfully logged out. You may sign in again!</div>
			</c:if>
		</div>
	</div>
</div>

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title"><i class="icon-fixed-width icon-lock"></i>Login</div>
			</div>

			<div class="panel-body">
				<form class="form-horizontal" role="form" method="POST"
					action="j_spring_security_check">
					<div class="form-group">
						<label for="inputUserID" class="col-sm-3 control-label">Username</label>
						<div class="input-group col-sm-6">
							<span class="input-group-addon"><i class="icon-fixed-width icon-user"></i></span>
							<input type="text" class="form-control" name="j_username"
								id="inputUserID" placeholder="User ID">
						</div>
					</div>
					<div class="form-group">
						<label for="inputPassword" class="col-sm-3 control-label">Password</label>
						<div class="input-group col-sm-6">
							<span class="input-group-addon"><i class="icon-fixed-width icon-key"></i></span>
							<input type="password" class="form-control" name="j_password"
								id="inputPassword" placeholder="Password">
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
							<div class="checkbox">
								<label> 
									<input type="checkbox" style="margin-right: 4px;" name='_spring_security_remember_me'>Remember me
								</label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
							<button type="submit" class="btn button"> Sign in</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<script>
	var emulateDiv = document.getElementById('emulate');
	if(emulateDiv!=null)
	{
		emulateDiv.style.display = 'none';
	}
</script>