<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
		<div id="userCreateError" style="display: none"></div>
		<div id="userCreateInfo" style="display: none"></div>
			<div class="panel-heading">
				<div class="panel-title">Create A Job</div>
			</div>
			<div align="center" class="panel-body">
				<a href="<%=request.getContextPath()%>/job/continuejob"> 
					<i class="icon-fixed-width icon-plus"></i>Add a new job
				</a>
			</div>
		</div>
	</div>
</div>
<script>
	var message = window.localStorage.getItem('jobresponse');
	var iDiv = document.createElement('div');
	iDiv.style.display = 'block';

	if (message == "error") {
		iDiv.className = 'alert alert-danger alert-dismissable';
		iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
		iDiv.innerHTML += "Job Creation Failed";
		document.all.userCreateError.insertBefore(iDiv);
		var div = document.getElementById('userCreateError');
		div.style.display = 'block';
	} else if (message == "success") {
		iDiv.className = 'alert alert-success alert-dismissable';
		iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
		iDiv.innerHTML += "Job Created Successfully";
		document.all.userCreateInfo.insertBefore(iDiv);
		var div = document.getElementById('userCreateInfo');
		div.style.display = 'block';
	} else if (message == "editsuccess") {
		iDiv.className = 'alert alert-success alert-dismissable';
		iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
		iDiv.innerHTML += "Job Edit Successful";
		document.all.userCreateInfo.insertBefore(iDiv);
		var div = document.getElementById('userCreateInfo');
		div.style.display = 'block';
	} else if (message == "editerror") {
		iDiv.className = 'alert alert-danger alert-dismissable';
		iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
		iDiv.innerHTML += "Job Edit Failed";
		document.all.userCreateError.insertBefore(iDiv);
		var div = document.getElementById('userCreateError');
		div.style.display = 'block';
	}
	
	window.localStorage.setItem('jobresponse', "");
</script>