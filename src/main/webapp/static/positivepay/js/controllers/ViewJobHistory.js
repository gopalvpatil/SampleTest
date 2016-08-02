/**
 * JOBController is responsible for modifying all the business logic on the
 * Create Job Page.
 * 
 * @Author Sameer Shukla
 * @constructor
 * 
 */
var ViewJobHistory = function($scope, $http) {
	$scope.editMode = true;
	/**
	 * All the global variable declarations
	 */
	$scope.error = "";
	$scope.baseUrl = globalBaseURL;
	$scope.jobNumOfItemsProcessed = '';
	$scope.jobNumOfErrors = '';
	$scope.jobNumOfFilesProccessed = '';
	$scope.jobNumOfFilesFailed = '';
	$scope.jobActualStartTime = '';
	$scope.jobActualEndTime = '';
	$scope.jobType = '';
	$scope.respmessage = '';
	$scope.waitStepHistory = false;
	
	/**
	 * Needed for radio's to behave properly
	 */
	$scope.options = {
		value : '-'
	};
	
	/**
	 * 
	 * This method submit the JSON Array to the controller.
	 */
	$scope.getJobStepHistory = function(id) {		
	    	$scope.waitStepHistory = true;
			var url = $scope.baseUrl+"/job/jobstephistory/"+id;
			$http({
				url : url,
				params: { 'reqtime': $.now()},
				dataType : "json",
				method : "GET",
				headers : {
					"Content-Type" : "application/json"
				}
			}).success(function(response) {
				$scope.jobStepHistoryArray = [];
				$scope.toggle = [];
				if(response != null && response.length > 0) {
					var errorFlag = false;
					var sumNumOfItemsProcessed = 0;
					var sumNumOfErrors = 0;					
					var sumNumOfFilesProcessed = 0;
					var sumNumOfFilesFailed = 0										
					var summary = '';
					$scope.jobActualStartTime = response[0].jobActualStartTime;
					$scope.jobActualEndTime = response[0].jobActualEndTime;
					$scope.jobType =  response[0].jobType;
					$scope.jobTimezone =  response[0].jobTimezone;
					var count = 1;			
					for(var i=0; i < response.length ; i++) {									
						if (!isNaN(response[i].jobStepNumOfItemsProcessed)) sumNumOfItemsProcessed += parseInt(response[i].jobStepNumOfItemsProcessed);
						if (!isNaN(response[i].jobStepNumOfErrors)) sumNumOfErrors += parseInt(response[i].jobStepNumOfErrors);	
						
						if (!isNaN(response[i].jobStepNumOfFilesProcessed)) sumNumOfFilesProcessed += parseInt(response[i].jobStepNumOfFilesProcessed);
						if (!isNaN(response[i].jobStepNumOfFilesFailed)) sumNumOfFilesFailed += parseInt(response[i].jobStepNumOfFilesFailed);
						
						summary += count+'. ' 
						summary += response[i].comments;
						summary += '\n';
						$scope.toggle[i] = false;
						if(response[i] != null) {
							$scope.jobStepHistoryArray.push(response[i]);
						}						
						count++;
					}
					
					$scope.jobNumOfItemsProcessed = sumNumOfItemsProcessed;
					$scope.jobNumOfErrors = sumNumOfErrors;						
					$scope.jobNumOfFilesProcessed = sumNumOfFilesProcessed;
					$scope.jobNumOfFilesFailed = sumNumOfFilesFailed;	
					
			        $("#jobStepSummaryText").empty();					
                    var jobStepSummaryVar = '<div class="col-sm-10">' +
							                    '<textarea class="col-sm-5 form-control" name="jobStepSummary">' +
							                    	summary +
							                    '</textarea>' +
						                    '</div>';

                    $('#jobStepSummaryText').append(jobStepSummaryVar);
        	    	
        	    	if(response[0].showErrorLink) {
        	    		document.getElementById('errorLink').style.pointerEvents = 'auto';
        	    	} else {
        	    		document.getElementById('errorLink').style.pointerEvents = 'none';
        	    	}
        	    	$scope.waitStepHistory = false;
					$('#jobStepHistory').modal('show');
				} else {
					$scope.waitStepHistory = false;
					$('#NoJobStepHistoryModal').modal('show');		
				}
			}).error(function(error) {
				$scope.respmessage = error;
				var iDiv = document.getElementById('userCreateError');
				if(!iDiv) {
					var iDiv = document.createElement('div');
					iDiv.style.display = 'block';
					iDiv.className = 'alert alert-danger alert-dismissable';
					iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
					iDiv.innerHTML += error.message;
					$( "#pb" ).prepend(iDiv);
				} else {
					iDiv.style.display = 'block';
					iDiv.className = 'alert alert-danger alert-dismissable';
					iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
					iDiv.innerHTML += error.message;
					iDiv.style.display = 'block';
				}				
				$scope.waitStepHistory = false;
			});
	};
	
	
	$scope.showStepComments = function(rowId) {
		if($scope.toggle[rowId] == true) {
			$scope.toggle[rowId] = false;
		} else {
			$scope.toggle[rowId] = true;
		}		
	};
	
};