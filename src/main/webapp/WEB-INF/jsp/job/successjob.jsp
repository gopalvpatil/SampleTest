<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script	src="<%=request.getContextPath()%>/static/positivepay/js/job.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/controllers/JobController.js"></script>    

<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Create A Job</div>
			</div>
		</div>
		
		<div class="panel panel-primary">
			<div align="center" class="panel-body">
				Success! Your Job has been added.<br />
				<a href="<%=request.getContextPath()%>/job/viewjob">
					<i class="icon-fixed-width"></i>				
					Access all jobs from the jobs tab
				</a>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-body">
				<div align="center" class="panel-body">			
					<a href="<%=request.getContextPath()%>/job/continuejob">
						<i class="icon-fixed-width icon-plus"></i>				
						Add another job
					</a>
				</div>			
			</div>	
		</div>
	</div>		
</div>

