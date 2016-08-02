<%@page import="com.westernalliancebancorp.positivepay.utility.SecurityUtility"%>



<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Home</div>
			</div>
			<div class="panel-body">
				<div class="form-group">
					Welcome back <strong><%=SecurityUtility.getPrincipal()%></strong>
				</div>
				<div class="form-group col-sm-12">
					<div id="datepickerTest" style="width: 100%">
					</div>
					<a class="btn btn-success" id="jsonResponseBtn" href="#"> <i
						class="icon-repeat icon-large"></i> Get json response
					</a>
					<a class="btn btn-success" id="xmlResponseBtn" href="#"> <i
						class="icon-repeat icon-large"></i> Get xml response
					</a>
				</div>
			</div>
		</div>
	</div>
</div>