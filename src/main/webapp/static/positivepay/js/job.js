$(function() {		
    $('#selectAll').click(function(e) {
        $(this).closest('table').find('td input:checkbox').prop('checked', this.checked);
    });

    $('#delete').click(function(event) {
        var selectedIds = [];
        $(':checkbox:checked').each(function(i) {
            selectedIds.push($(this).val());
        });

        var data = {jobId: jobId};

        $.ajax({
            url: "deleteJob",
            type: "POST",
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            datatype: "json",
            traditional: true,
            success: function(response) {
                $('#info').html('Job was deleted successfully.');
                $('#info').show();
            },
            error: function(response) {
                $('#error').html('A problem has occured, job was not deleted.');
                $('#error').show();
            },
            async: false
        });
        event.preventDefault();
    });

    $('#run').click(function(event) {
    	$('#error').hide();
    	$('#info').hide();
        var selectedIds = [];
        $('.runCheckbox:checked').each(function(i) {
            selectedIds.push($(this).val());
        });
        
        if (selectedIds.length > 0) {
	        var data = {selectedIds: selectedIds};	
	        $.ajax({
	            url: "runJob",
				params: { 'reqtime': $.now()},
	            type: "POST",
	            data: JSON.stringify(data),
	            contentType: 'application/json; charset=utf-8',
	            datatype: "json",
	            traditional: true,
	            success: function(response) {
	                $('#info').html('Job is running in the background.');
	                $('#info').show();
	            },
	            error: function(response) {
	                $('#error').html('A problem has occured, job did not run.');
	                $('#error').show();
	            },
	            async: false
	        });
        } else {
            $('#error').html('Please select at least one job to run.');
            $('#error').show();
        }
        event.preventDefault();
    }); 

    $('a.confirm-link').click(function(event) {
        var url = ($(this).attr('href1'));
        var id = getURLParameter(url, 'id');
        $('#deleteConfirmModal').data('id', id).modal('show');
    });

    $('a.del-link').click(function(event) {
        var id = $('#deleteConfirmModal').data('id');
        var data = {jobId: id};
        
        $.ajax({
            url: "deleteJobById",
            type: "POST",
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            datatype: "json",
            traditional: true,
            success: function(response) {
                $('#info').html('Job was deleted successfully.');
                $('#info').show();
                $("#row-" + id).hide('slow');
            },
            error: function(response) {
                $('#error').html('A pProblem has occured, job was not deleted.');
                $('#error').show();
            },
            async: false
        });
        event.preventDefault();

        $('#deleteConfirmModal').modal('hide');
    });

    function getURLParameter(url, name) {
        return (RegExp(name + '=' + '(.+?)(&|$)').exec(url) || [, null])[1];
    }    
    
    var isViewPage = $("#isViewPage").val();
    if (isViewPage == 'true') {  		
    		setInterval(function() {        
    			var dynamicJobList = [];    			
	            $.ajax({
	                url: "fetchJobStatus",
	    			params: { 'reqtime': $.now()},
	                type: "GET",
	                contentType: 'application/json; charset=utf-8',
	                datatype: "json",
	                traditional: true,
	                success: function(jobDtoList) {	
	                	 if (jobDtoList.length > 0) {
	                         for (var i = 0; i < jobDtoList.length; i++) {	                        	 
	                        	 var jobDto = jobDtoList[i];	
		                         $("#statusDynamicColumn-"+jobDto.jobId).empty(); 
		                         $("#nextRunDateDynamicColumn-"+jobDto.jobId).empty(); 
		                         $("#lastRunDateDynamicColumn-"+jobDto.jobId).empty(); 
	                        	 
		                         if (jobDto.active) {	                        	 	                        		 
	                        		 if (jobDto.jobStatusType != 'Running') {
	                        			 var statusDynamicColumn = '<td id="statusDynamicColumn-'+jobDto.jobId+'">' + jobDto.jobStatusType + '</td>';
	                        		 } else {
	                        			 var statusDynamicColumn = '<td id="statusDynamicColumn-'+jobDto.jobId+'">' +	                        			 								
	                        			 								jobDto.jobStatusType + 
	                        			 							'</td>';
	                        		 }	                        		 
	                        		 var nextRunDateDynamicColumn = '<td id="nextRunDateDynamicColumn-'+jobDto.jobId+'">' + jobDto.jobNextRunDate + '</td>';
	                        		 var lastRunDateDynamicColumn = '<td id="lastRunDateDynamicColumn-'+jobDto.jobId+'">' + jobDto.jobLastRunDate + '</td>';
	                        		
	                        		 $("#statusDynamicColumn-" + jobDto.jobId).append(statusDynamicColumn);
	                        		 $("#statusStaticColumn-" + jobDto.jobId).hide();	         	                    	                        	 
	        	                	 $("#statusDynamicColumn-" + jobDto.jobId).show();
	        	                	
	         	                     $("#nextRunDateDynamicColumn-" + jobDto.jobId).append(nextRunDateDynamicColumn);
	        	                	 $("#nextRunDateStaticColumn-" + jobDto.jobId).hide();	                        	 
	        	                	 $("#nextRunDateDynamicColumn-" + jobDto.jobId).show();
	        	                	
	         	                     $("#lastRunDateDynamicColumn-" + jobDto.jobId).append(lastRunDateDynamicColumn);	
	        	                	 $("#lastRunDateStaticColumn-" + jobDto.jobId).hide();                        	 
	        	                	 $("#lastRunDateDynamicColumn-" + jobDto.jobId).show();
	        	                	
	        	                	 $("#info").hide();
	        	        			 $("#error").hide();	                        		 
	                        	 }
	                         }
	                	 }
	                },
	                error: function(response) {
	                },
	                async: true
	            });
            }, 5000);    		
    }
    
    var frequencySelectedVar = $("#frequencySelected").val();
    var intervalTimeSelectedVar = $("#intervalTimeSelected").val();
    var timezoneSelectedVar = $("#timezoneSelected").val();
    var jobStartHourSelected = $("#jobStartHourSelected").val();
    var jobStartMinuteSelected = $("#jobStartMinuteSelected").val();
    var jobStartMeridiemSelected = $("#jobStartMeridiemSelected").val();
    var jobEndHourSelected = $("#jobEndHourSelected").val();
    var jobEndMinuteSelected = $("#jobEndMinuteSelected").val();
    var jobEndMeridiemSelected = $("#jobEndMeridiemSelected").val();
    var indefinitelyVar = $("#indefinitely").val();
    var savePageVar = $("#savePage").val();
    
    if(indefinitelyVar && savePageVar != 'true') {
    	toggleJobEndDate(true);
    }
    
    if(frequencySelectedVar == 'One-time' && savePageVar != 'true') {
    	disableFieldsForOneTime();
    }

    var frequency = {"Recurring": "Recurring", "One-time": "One-time"};
    for (var text in frequency) {
        var val = frequency[text];
        if(val == frequencySelectedVar) {
        	$('<option selected="true"/>').val(val).text(text).appendTo($('#jobFrequency'));
        } else {
        	$('<option/>').val(val).text(text).appendTo($('#jobFrequency'));
        }
    };

    var intervalTime = {"5 minutes": "5", "10 minutes": "10", "15 minutes": "15", "30 minutes": "30", "every hour": "60", "every 2 hours": "120"};
    for (var text in intervalTime) {
        var val = intervalTime[text];
        if(val == intervalTimeSelectedVar) {
        	$('<option selected="true"/>').val(val).text(text).appendTo($('#intervalTime'));
        } else {
        	$('<option/>').val(val).text(text).appendTo($('#intervalTime'));
        }        
    };

    var hour = {
            "00": "00", "01": "01", "02": "02", "03": "03", "04": "04", "05": "05", "06": "06",
            "07": "07", "08": "08", "09": "09", "10": "10", "11": "11", "12": "12"
        };
        
        var hourKeys = [];
        for (k in hour) {
            if (hour.hasOwnProperty(k)) {
            	hourKeys.push(k);
            }
        }
        
        hourKeys.sort();	
        for (i = 0; i < hourKeys.length; i++){
        	var text = hourKeys[i];
            var val = hour[text];        
            if(val == jobStartHourSelected) {
            	$('<option selected="true"/>').val(val).text(text).appendTo($('#jobStartHour'));
            } else {
            	$('<option/>').val(val).text(text).appendTo($('#jobStartHour'));
            }         
            
            if(val == jobEndHourSelected) {
            	$('<option selected="true"/>').val(val).text(text).appendTo($('#jobEndHour'));
            } else {
            	$('<option/>').val(val).text(text).appendTo($('#jobEndHour'));
            } 
        };
        
        var minute = {
            "00": "00", "01": "01", "02": "02", "03": "03", "04": "04", "05": "05",
            "06": "06", "07": "07", "08": "08", "09": "09", "10": "10",
            "11": "11", "12": "12", "13": "13", "14": "14", "15": "15",
            "16": "16", "17": "17", "18": "18", "19": "19", "20": "20",
            "21": "21", "22": "22", "23": "23", "24": "24", "25": "25",
            "26": "26", "27": "27", "28": "28", "29": "29", "30": "30",
            "31": "31", "32": "32", "33": "33", "34": "34", "35": "35",
            "36": "36", "37": "37", "38": "38", "39": "39", "40": "40",
            "41": "41", "42": "42", "43": "43", "44": "44", "45": "45",
            "46": "46", "47": "47", "48": "48", "49": "49", "50": "50",
            "51": "51", "52": "52", "53": "53", "54": "54", "55": "55",
            "56": "56", "57": "57", "58": "58", "59": "59"
        };    
        var minuteKeys = [];
        for (k in minute)
        {
            if (minute.hasOwnProperty(k))
            {
                minuteKeys.push(k);
            }
        }
        minuteKeys.sort();	
        for (i = 0; i < minuteKeys.length; i++){
        	var text = minuteKeys[i];
            var val = minute[text];        
            if(val == jobStartMinuteSelected) {
            	$('<option selected="true"/>').val(val).text(text).appendTo($('#jobStartMinute'));
            } else {
            	$('<option/>').val(val).text(text).appendTo($('#jobStartMinute'));
            }         
            
            if(val == jobEndMinuteSelected) {
            	$('<option selected="true"/>').val(val).text(text).appendTo($('#jobEndMinute'));
            } else {
            	$('<option/>').val(val).text(text).appendTo($('#jobEndMinute'));
            } 
        }
    
    var meridiem = {"AM": "AM", "PM": "PM"};
    for (var text in meridiem) {
        var val = meridiem[text];
        if(val == jobStartMeridiemSelected) {
        	$('<option selected="true"/>').val(val).text(text).appendTo($('#jobStartMeridiem'));
        } else {
        	$('<option/>').val(val).text(text).appendTo($('#jobStartMeridiem'));
        }         
        
        if(val == jobEndMeridiemSelected) {
        	$('<option selected="true"/>').val(val).text(text).appendTo($('#jobEndMeridiem'));
        } else {
        	$('<option/>').val(val).text(text).appendTo($('#jobEndMeridiem'));
        } 
    };

    var timezone = {"US/Pacific": "US/Pacific", "US/Mountain": "US/Mountain", "US/Arizona": "US/Arizona", "US/Central": "US/Central", "US/Eastern": "US/Eastern"};
    for (var text in timezone) {
        var val = timezone[text];
        if(val == timezoneSelectedVar) {
        	$('<option selected="true"/>').val(val).text(text).appendTo($('#timezone'));
        } else {
        	$('<option/>').val(val).text(text).appendTo($('#timezone'));
        }  
    };

    $("#jobStartDate").datepicker();

    $("#imageStartDate").click(function() {
    	if($('#jobStartDate').is(':enabled')) {
    		$("#jobStartDate").datepicker("show");
    	}
    });

    $("#jobEndDate").datepicker();

    $("#imageEndDate").click(function() {
    	if($('#jobEndDate').is(':enabled')) {
    		$("#jobEndDate").datepicker("show");
    	}
    });

    jQuery.validator.addMethod("usaDate", function(value, element) {
        var date = getDateFromFormat(value, 'MM/dd/yyyy');
        if (date == 0) {
            return false;
        }
        
    	return true;
	},
    "Please enter a job start date in the format mm/dd/yyyy");
    
    $('#indefinitely').click(function(e) {
    	toggleJobEndDate(this.checked);
    });    
    
    $('#jobFrequency').on('change', function() {
        if (this.value == "One-time") {
        	disableFieldsForOneTime();
        }

        if (this.value != "One-time") {
            var nodes = document.getElementById("run-on").getElementsByTagName('*');
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].disabled = false;
            }

            nodes = document.getElementById("endDate").getElementsByTagName('*');
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].disabled = false;
            }

            nodes = document.getElementById("indifinite").getElementsByTagName('*');
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].disabled = false;
            }

            nodes = document.getElementById("interval").getElementsByTagName('*');
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].disabled = false;
            }
            nodes = document.getElementById("endTime").getElementsByTagName('*');
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].disabled = false;
            }
        }
    }); 
    
    //This method should be remove from here and include it in another js   
  //This method should be remove from here and include it in another js   
    /*$('#contactPhone,#contactFax').keydown(function (e) {
		var key = e.charCode || e.which || 0;
		$(this).focus();
		$phone.val = $(this).val;
		// Auto-format- do not expose the mask as the user begins to type
		if (key !== 8 && key !== 9) {
			if($phone.val().indexOf('(') < 0) {
				$phone.val('('+$phone.val());
			}
			
			if ($phone.val().length === 4) {
				$phone.val($phone.val() + ')');
			}
			if ($phone.val().length === 5) {
				$phone.val($phone.val() + ' ');
			}			
			if ($phone.val().length === 9) {
				$phone.val($phone.val() + '-');
			}
		}
		// Allow numeric (and tab, backspace, delete) keys only
		return (key == 8 || 
				key == 9 ||
				key == 46 ||
				(key >= 48 && key <= 57) ||
				(key >= 96 && key <= 105));	
	}).bind('focus click', function () {
		$phone = $(this);		
		if ($phone.val().length === 0) {
			$phone.val('(');
		}
		else {
			var val = $phone.val();
			$phone.val('').val(val); // Ensure cursor remains at the end
		}
	}).blur(function () {
		$phone = $(this);		
		if ($phone.val() === '(') {
			$phone.val('');
		}
	});*/
    
    
    
  //Below should be removed from here and include in another account related js  
    $('#open-account').click(function(event) {
        var url = ($(this).attr('href1'));
        if (window.localStorage.getItem('compId') == 0) {
            var iDiv = document.getElementById('userCreateInfo');
            if (!iDiv) {
                var iDiv = document.createElement('div');
                iDiv.style.display = 'block';
                iDiv.className = 'alert alert-danger alert-dismissable';
                iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
                iDiv.innerHTML += "Error: Cannot create account without company";
                $("#pb").prepend(iDiv);
            }
            else {
                iDiv.style.display = 'block';
                iDiv.className = 'alert alert-danger alert-dismissable';
                iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
                iDiv.innerHTML += "Error: Cannot create account without company";
                iDiv.style.display = 'block';
            }
            return false;
        }
        var id = getURLParameter(url, 'id');
        $('#accountSetup').data('id', id).modal('show');
    });

    $('.account-table').on('click','.icon-pencil',function(event) {
        var url = ($(this).attr('href1'));
        if (window.localStorage.getItem('compId') == 0) {
            var iDiv = document.createElement('div');
            iDiv.style.display = 'block';
            iDiv.className = 'alert alert-danger alert-dismissable';
            iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
            iDiv.innerHTML += "Error: Cannot create Account without Company";
            document.all.userCreateError.insertBefore(iDiv);
            var div = document.getElementById('userCreateError');
            div.style.display = 'block';
            return false;
        }
        var id = getURLParameter(url, 'id');
        $('#accountSetup').data('id', id).modal('show');
    });
});


function toggleJobEndDate(checked) {	 
    $('#indefinitely').attr('checked', checked);
	$("#jobEndDate").val('');
	var nodes = document.getElementById("endDate").getElementsByTagName('*');
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].disabled = checked;
    }  
    if(this.checked) {
    	document.getElementById('imageEndDate').style.pointerEvents = 'none';
    } else {
    	document.getElementById('imageEndDate').style.pointerEvents = 'auto';
    } 
};

function disableFieldsForOneTime() {	 
    var nodes = document.getElementById("run-on").getElementsByTagName('*');
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].disabled = true;
    }

    nodes = document.getElementById("endDate").getElementsByTagName('*');
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].disabled = true;
    }

    nodes = document.getElementById("indifinite").getElementsByTagName('*');
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].disabled = true;
    }

    nodes = document.getElementById("interval").getElementsByTagName('*');
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].disabled = true;
    }

    nodes = document.getElementById("endTime").getElementsByTagName('*');
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].disabled = true;
    }
};

function validateContinueJob() {
	var checkDetail = {
	    "jobName": $("#jobName").val(),
	    "jobStartDate": $("#jobStartDate").val(),
	    "jobEndDate": $("#jobEndDate").val(),
	    "jobFrequency": $("#jobFrequency").val(),
	    "indefinitely": $("#indefinitely").val(),
	    "interval": $("#intervalTime").val(),
	    "jobStartHour": $("#jobStartHour").val(),
	    "jobStartMinute": $("#jobStartMinute").val(),
	    "timezone": $("#timezone").val(),
	};
        
    var errors = new Array();
    var isRunOnDays;
    var isIndifinite = $('#indefinitely').is(':checked');
    
    if($('#monday').is(':checked') || $('#tuesday').is(':checked') || $('#wednesday').is(':checked') || $('#thursday').is(':checked') || 
    		$('#friday').is(':checked') || $('#saturday').is(':checked') || $('#sunday').is(':checked') || $('#weekly').is(':checked')) {
    	isRunOnDays = true;
    } else {
    	isRunOnDays = false;
    }
    	
    if (!checkDetail.jobName) {
        var errorMessage = "Job name is required";
        $("#jobName").addClass('highlight');
        $("#jobName").focus();
        errors.push(errorMessage);
    } else {
        $("#jobName").removeClass('highlight');
    }

    if (!checkDetail.jobStartDate) {
        var errorMessage = "Please enter a job start date in the format mm/dd/yyyy";
        $("#jobStartDate").addClass('highlight');
        $("#jobStartDate").focus();
        errors.push(errorMessage);
    } else {
        $("#jobStartDate").removeClass('highlight');
    }

    if (checkDetail.jobFrequency != 'One-time') {
        if (!isIndifinite && !checkDetail.jobEndDate) {
            var errorMessage = "Please enter a job end date in the format mm/dd/yyyy";
            $("#jobEndDate").addClass('highlight');
            $("#jobEndDate").focus();
            errors.push(errorMessage);
        } else {
            $("#jobEndDate").removeClass('highlight');
        }

        if (checkDetail.jobEndDate && checkDetail.jobStartDate) {
            if (!compareDatesFormatMMDDYYYY($('#jobEndDate').val(), $('#jobStartDate').val())) {
                var errorMessage = "Please enter a valid range for job start and end date in the format mm/dd/yyyy";
                $("#jobStartDate").addClass('highlight');
                $("#jobEndDate").addClass('highlight');
                errors.push(errorMessage);
            } else {
                $("#jobEndDate").removeClass('highlight');
                $("#jobStartDate").removeClass('highlight');
            }
        }
        
        $("#setInterval").removeClass('highlight');
        $("#jobStartHour").removeClass('highlight');
        $("#jobStartMinute").removeClass('highlight');
        $("#timezone").removeClass('highlight');        
		if(checkDetail.jobStartHour == '00' && checkDetail.jobStartMinute == '00' && checkDetail.interval == '0') {
            var errorMessage = "Either interval or start time should be selected.";
            $("#setInterval").addClass('highlight');
            $("#jobStartHour").addClass('highlight');
            $("#jobStartMinute").addClass('highlight');
            $("#setInterval").focus();
            $("#jobStartHour").focus();
            $("#jobStartMinute").focus();
            errors.push(errorMessage);
        } else if((checkDetail.jobStartHour != '00' || checkDetail.jobStartMinute != '00') && checkDetail.interval != '0') {
            var errorMessage = "Interval and start time should not be selected at the same time.";
            $("#setInterval").addClass('highlight');
            $("#jobStartHour").addClass('highlight');
            $("#jobStartMinute").addClass('highlight');
            $("#setInterval").focus();
            $("#jobStartHour").focus();
            $("#jobStartMinute").focus();
            errors.push(errorMessage);
        } else if(checkDetail.jobStartHour == '00' && checkDetail.jobStartMinute != '00' && checkDetail.interval == '0') {
            var errorMessage = "Please select Hour time in Start time.";
            $("#jobStartHour").addClass('highlight');
            $("#jobStartHour").focus();
            errors.push(errorMessage);
        } else {
            $("#setInterval").removeClass('highlight');
            $("#jobStartHour").removeClass('highlight');
            $("#jobStartMinute").removeClass('highlight');
        }
    
	    if(checkDetail.timezone == '000') {
	        var errorMessage = "Please enter timezone.";
	        $("#timezone").addClass('highlight');
	        $("#timezone").focus();
	        errors.push(errorMessage);
	    } else {
	        $("#timezone").removeClass('highlight');
	    } 
		
		if(!isRunOnDays) {
            var errorMessage = "Please select at least a day to run job.";
            $("#run-on").addClass('highlight');            
            $("#run-on").focus();
            errors.push(errorMessage);
		} else {
            $("#run-on").removeClass('highlight');
		}        
    } else {
        $("#jobStartHour").removeClass('highlight');
        $("#jobStartMinute").removeClass('highlight');
        $("#timezone").removeClass('highlight'); 
        
        if(checkDetail.jobStartHour == '00' && checkDetail.jobStartMinute == '00') {
            var errorMessage = "Start time should be selected.";
            $("#jobStartHour").addClass('highlight');
            $("#jobStartMinute").addClass('highlight');
            $("#jobStartHour").focus();
            $("#jobStartMinute").focus();
            errors.push(errorMessage);
        }  else if(checkDetail.jobStartHour == '00' && checkDetail.jobStartMinute != '00') {
            var errorMessage = "Please select Hour time in Start time.";
            $("#jobStartHour").addClass('highlight');
            $("#jobStartHour").focus();
            errors.push(errorMessage);
        } else {
            $("#jobStartHour").removeClass('highlight');
            $("#jobStartMinute").removeClass('highlight');
        }        
        
        if(checkDetail.timezone == '000') {
            var errorMessage = "Please enter timezone.";
            $("#timezone").addClass('highlight');
            $("#timezone").focus();
            errors.push(errorMessage);
        } else {
            $("#timezone").removeClass('highlight');
        }        
        
    }

    //check if errors
    if (errors.length > 0) {
        //empty previous errors if any
        $("#errors").empty();
        //Hide successBox if present
        if ($("#successBox").hasClass("show")) {
            $("#successBox").removeClass('show').addClass('hidden');
        }
        //errors found, show the error box
        if ($("#errorBox").hasClass('hidden')) {
            $("#errorBox").removeClass('hidden').addClass('show');
        }
        //Add all errors to the errors div
        for (var i = 0; i < errors.length; i++) {
            $("#errors").append($("<li></li>").html(errors[i]));
        }
        return false;
    } else {
        //No error, validation pass, so hide error box
        $("#errorBox").addClass('hidden');
        //Empty error Message
        $("#errors").empty();
        return true;
    }
    return false;
}


function compareDatesFormatMMDDYYYY(bigDate, smallDate) {
    var ret = false;
    if (parseInt(bigDate.split('/')[2]) > parseInt(smallDate.split('/')[2])) {
        ret = true;
    }
    if (parseInt(bigDate.split('/')[2]) == parseInt(smallDate.split('/')[2])) {
        //for same yr , compare month
        if (parseInt(bigDate.split('/')[0]) > parseInt(smallDate.split('/')[0])) {
            ret = true;
        }
    }
    if (parseInt(bigDate.split('/')[2]) == parseInt(smallDate.split('/')[2]) && parseInt(bigDate.split('/')[0]) == parseInt(smallDate.split('/')[0])) {
        //both yr and month are same , then chk for date
        if (parseInt(bigDate.split('/')[1]) >= parseInt(smallDate.split('/')[1])) {
            ret = true;
        }
    }
    return ret;
}