<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<!DOCTYPE html>
<html ng-app>
	<head>
		<title><tiles:getAsString name="title" /></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<!-- WAL favicon -->
		<link rel="shortcut icon" href="<%=request.getContextPath()%>/static/positivepay/images/wal_favicon.ico" type="image/x-icon"/>
		<!-- Include CSS here -->
		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/jquery/css/jquery-ui.css" type="text/css" />
	    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/bootstrap/css/bootstrap.min.css" type="text/css" />	
		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/bootstrap/css/bootstrap-theme.min.css" type="text/css" />
		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" type="text/css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/font-awesome/css/font-awesome.min.css" type="text/css" />
		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/jReject/css/jquery.reject.css" type="text/css" />
		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/positivepay/css/positivepay.css" type="text/css" />
		<!--[if IE 9]>
     		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/positivepay/css/ie9.css">
			<script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/placeholder.js" type="text/javascript"></script>

			<style type="text/css" media="screen">
				.button {
					filter: none;
				}
			</style>
		<![endif]-->
		<link rel="stylesheet" href="<%=request.getContextPath()%>/static/positivepay/css/ie10.css">
		
		<!-- Include JS here -->
	    <script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery-1.10.2.min.js" type="text/javascript"></script>
	    <script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery-ui.js" type="text/javascript"></script>
	    <script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery-validate.js" type="text/javascript"></script>
	    <script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery-dateFormat.js" type="text/javascript"></script>
	    <script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery-date.js" type="text/javascript"></script>
		<script	src="<%=request.getContextPath()%>/static/thirdparty/jquery/js/jquery.blockUI.js" type="text/javascript"></script>
	    <script	src="<%=request.getContextPath()%>/static/thirdparty/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
	    <script	src="<%=request.getContextPath()%>/static/thirdparty/jReject/js/jquery.reject.min.js" type="text/javascript"></script>
		<script	src="<%=request.getContextPath()%>/static/positivepay/js/positivepay.js" type="text/javascript"></script>
		<script type="text/javascript">
			var globalBaseURL='<%=request.getContextPath()%>';
			var globalRequestUrl='<%=(String)request.getAttribute("javax.servlet.forward.request_uri")%>';
		</script>

	</head>
	<body>
		<div id="wrap">
			<div id="header">
				<tiles:insertAttribute name="header"/>
			</div>
			<div id="menu">
				<tiles:insertAttribute name="menu"/>
			</div>
			<div id="globalMessage">
				<tiles:insertAttribute name="globalMessage"/>
			</div>
			<div id="body" style="overflow: hidden;">
      			<tiles:insertAttribute name="body"/>
			</div>
		</div>
		<div id="footer" class="footer">
			<tiles:insertAttribute name="footer"/>
		</div>
	</body>
</html>