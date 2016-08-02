<%@ page
	import="com.westernalliancebancorp.positivepay.utility.SecurityUtility"%>
<%@ page import="com.westernalliancebancorp.positivepay.model.Permission" %>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!--This is a comment. Comments are not displayed in the browser-->
<style type='text/css'>
	/*ul li {
		display: inline;
	}*/
	ul li.dropdown:hover ul.dropdown-menu {
		display: block;
	}
	
	@media ( min-width : 768px) {
		.navbar-nav {
			margin: 0 auto;
			display: table;
			table-layout: fixed;
			float: none;
		}
	}
</style>
<div>
	<!--  
	<div class="navbar-header">
		<button type="button" class="navbar-toggle" data-toggle="collapse"
			data-target=".navbar-ex1-collapse">
			<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span>
			<span class="icon-bar"></span> <span class="icon-bar"></span>
		</button>
		<a class="navbar-brand" href="<%=request.getContextPath()%>/dashboard/viewdashboard">
			<i class="icon-fixed-width icon-dashboard"></i>Dashboard
		</a>
	</div>
	-->
	<!-- Refer to positivepay.js jqueryReady function for functionality of highlighting menus  -->
	<div class="collapse navbar-collapse">
		<ul id="menuNavBar" class="nav navbar-nav">
			<security:authorize
				ifAnyGranted="ROLE_CORPORATE_ADMIN, ROLE_BANK_ADMIN, PPAY_USERS, ROLE_CORPORATE_USER">
				<li><a href="<%=request.getContextPath()%>/user/dashboard">Dashboard</a></li>
			</security:authorize>
			<security:authorize
				ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS, ROLE_CORPORATE_ADMIN">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown"> Users </a>
					<ul class="dropdown-menu">
						<li>
							<a href="<%=request.getContextPath()%>/user/manageusers">
								Manage Users </a>
						</li>
                        <%if (SecurityUtility.hasPermission(Permission.NAME.ADD_ROLE)) { %>
                        <li>
                        	<a href="<%=request.getContextPath()%>/role/userroles">
                            User Roles </a>
                        </li>
                        <%} //if condition closed for hasPermission(Permission.NAME.ADD_ROLE)%>
						<security:authorize ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS">
							<li><a href="<%=request.getContextPath()%>/user/managebanks">
								Manage Banks </a>
							</li>
						</security:authorize>
					</ul></li>
			</security:authorize>
			<security:authorize ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS">
				<li>
					<a href="<%=request.getContextPath()%>/admin/system">System</a>
				</li>
			</security:authorize>
			<security:authorize
				ifAnyGranted="ROLE_CORPORATE_ADMIN, ROLE_CORPORATE_USER">
				<li>
					<a href="<%=request.getContextPath()%>/user/filemanagement">File
						Management</a>
				</li>
			</security:authorize>
			<security:authorize
				ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS, ROLE_CORPORATE_ADMIN, ROLE_CORPORATE_USER">
				<li>
					<a href="<%=request.getContextPath()%>/user/manualentry">Manual
						Entry</a>
				</li>
			</security:authorize>
			<security:authorize
				ifAnyGranted=" ROLE_CORPORATE_USER, ROLE_CORPORATE_ADMIN">
				<li>
					<a href="<%=request.getContextPath()%>/user/exceptions">Exceptions</a>
				</li>
			</security:authorize>
			<security:authorize
				ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS, ROLE_CORPORATE_ADMIN, ROLE_CORPORATE_USER">
				<li>
					<a href="<%=request.getContextPath()%>/report/view">Reporting
						/ Extracts</a>
				</li>
			</security:authorize>
			<security:authorize
				ifAnyGranted="ROLE_CORPORATE_ADMIN, ROLE_CORPORATE_USER, PPAY_USERS">
				<li>
					<a href="<%=request.getContextPath()%>/user/filemapping">File
						Mapping
					</a>
				</li>
			</security:authorize>
			<security:authorize ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS">
				<li>
					<a href="<%=request.getContextPath()%>/user/paymentsanditems">Payments/Items</a>
				</li>
			</security:authorize>
			<security:authorize ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS">
				<li>
					<a href="<%=request.getContextPath()%>/user/emulation">Emulation</a>
				</li>
			</security:authorize>
			<security:authorize ifAnyGranted="ROLE_BANK_ADMIN, PPAY_USERS">
				<li class="dropdown"><a
					href="<%=request.getContextPath()%>/job/viewjob"
					class="dropdown-toggle"> Jobs </a>
					<ul class="dropdown-menu">
						<li>
							<a href="<%=request.getContextPath()%>/job/createjob">
								Create Job 
							</a>
						</li>
					</ul>
				</li>
			</security:authorize>
		</ul>
	</div>

	<div class="positivepay-column-spacer">
		<ul style="float: right; display: inline; list-style-type: none; padding-right: 20px;">
			<li style="float: left; display: inline;"><span
				style="padding-top: 4px; padding-bottom: 4px; display: block;">Logged
					in as </span></li>
			<li style="float: left; display: inline;"><strong
				style="padding-left: 10px; padding-top: 4px; padding-bottom: 4px; display: block;"><%=SecurityUtility.getPrincipal()%></strong>
			</li>
			<li style="float: left; display: inline;">
				<a style="padding-left: 10px; padding-top: 4px; padding-bottom: 4px; display: block;"
				href="<%=request.getContextPath()%>/j_spring_security_logout">
					Logout 
				</a>
			</li>
		</ul>
		<div style="margin-top:5px; margin-right: -35px; float: right; display: none;" id="dismissReadButton1">
			<button id="dismiss" name="dismiss" value="dismiss"	class="btn button" >Dismiss As Read</button>
		</div>
		<div style="margin-top:5px; margin-right: 5px; float: right; display: none;" id="dismissReadButton">
			<label id="labelMaintenanceMessage" class="maintenance-message-text" style="font-weight: bolder; font-size:14px;"></label> &nbsp;
		</div>
	</div>
</div>
