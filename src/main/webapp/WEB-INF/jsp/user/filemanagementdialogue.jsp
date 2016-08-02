<div class="modal" id="errorRecordsModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	<div class="modal-dialog" style="width:1200px;">
		<div class="modal-content">
			<div class="modal-header">
		        <div style="float: right">
					<button type="button" class="pp-sprite-close-window" data-dismiss="modal"></button>
				</div>							
		        <h3>Item Error Records:</h3>
		        <h4 class="modal-title" id="myModalLabel">Displaying results for filename <label id="fileName"> </label> uploaded on <label id="uploadedDate"> </label>
		        for the company <label id="companyName1"></label></h4>
			</div>
	      	<div class="modal-body">
				<div id="ajax-loader-errorDetails"></div>
				<div id="error-loading-errorDetails" class="alert alert-danger alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="error loading check details"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png" /> 
						Error while loading check details. Please try again later.
				</div>
				<div id="errorDetails" class="col-sm-12 hidden">
					<table id="errorDetailsTbl" class="table table-striped table-bordered">					
						<thead>
							<tr>
						    	<th style="width: 1.5%;" align="center">File Line Number</th>
						    	<th class="col-sm-1">Account Number</th>
						    	<th class="col-sm-1">Routing Number</th>
						    	<th class="col-sm-1" align="right">Check Number</th>
						    	<th class="col-sm-1" align="center" style="width: 1.5%;">Issue Code</th>
						    	<th class="col-sm-1" align="right">Check Amount</th>
						    	<th class="col-sm-1">Check Date</th>
						    	<th class="col-sm-1">Payee</th>
						    	<th class="col-sm-2">Error Type</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<div id="noErrorDetails" class="alert alert-info alert-dismissable hidden">
					<button type="button" class="close" data-dismiss="alert"
						aria-hidden="true">&times;</button>
					<img alt="No Error Details"
						src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" /> 
						No records to show.
				</div>
			</div>
		</div>
		<div class="modal-footer">
		</div>
	</div>
</div>