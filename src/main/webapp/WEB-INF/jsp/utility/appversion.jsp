<%
   response.setHeader( "Pragma", "no-cache" );
   response.setHeader( "Cache-Control", "no-cache" );
   response.setDateHeader( "Expires", 0 );
%>
<div class="row">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Application Version</div>
			</div>
			<div class="panel-body">
				<table class="table table-bordered">
					<tbody>
						<tr><td>Application Name</td><td>${project.artifactId}</td></tr>
						<tr><td>Application POM Version</td><td>${project.version}</td></tr>
						<tr><td>Build Timestamp</td><td>${appBuildTimestamp}</td></tr>
						<tr><td>Build Revision</td><td>${buildNumber}</td></tr>
						<tr><td>Build from</td><td>${scmBranch}</td></tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
