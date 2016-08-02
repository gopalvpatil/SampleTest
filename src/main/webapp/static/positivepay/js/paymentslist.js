$(document).ready(function() {

});

function populatePaymentListForExceptionsPage() {
    if (paymentsList.length == 0) {
        $.ajax({
            type: "GET",
            url: globalBaseURL + "/user/exceptions/payments",
            dataType: "json",
            beforeSend: function() {
                $("#ajax-loader-paymentList").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading payment List...');
            },
            complete: function() {
                $("#ajax-loader-paymentList").empty();
            },
            success: function(paymentsJson) {
                paymentsList = paymentsJson;
				buildPaymentsListTable();
            },
            error: function() {
                $('#error-loading-payments').removeClass("hidden");
            }
        });
    } else {
		buildPaymentsListTable();
    }
}

function populateItemListForPaymentsAndItemsPage() {
	var paymentsFilter = window.localStorage.getItem("paymentsFilter");
	if(paymentsFilter == null || paymentsFilter == "") {
		paymentsFilter = "{\"searchParametersMap\":{}, \"searchCriteria\" : \"\"}";
		$("#paymentList").notify("Fetching all records as no filter was specified!", {className: "warn", arrowShow: false, autoHideDelay: 3000});
	}
    if (itemList.length == 0) {
        $.ajax({
            type: "POST",
            url: globalBaseURL + "/user/itemsList",
            data: paymentsFilter,
            dataType: "json",
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
                $("#ajax-loader-paymentList").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading item List...');
            },
            complete: function() {
                $("#ajax-loader-paymentList").empty();
            },
            success: function(itemsJson) {
            	itemList = itemsJson;
            	buildItemsListTable();
            },
            error: function(jqXHR, textStatus, errorThrown) {
            	$('#error-loading-items').removeClass("hidden");
            }
        });
    } else {
    	buildItemsListTable();
    }
}

function populatePaymentListForPaymentsAndItemsPage() {
	var paymentsFilter = window.localStorage.getItem("paymentsFilter");
	if(paymentsFilter == null || paymentsFilter == "") {
		paymentsFilter = "{\"searchParametersMap\":{}, \"searchCriteria\" : \"\"}";
		$("#paymentList").notify("Fetching all records as no filter was specified!", {className: "warn", arrowShow: false, autoHideDelay: 3000});
	}
    if (paymentsList.length == 0) {
        $.ajax({
            type: "POST",
            url: globalBaseURL + "/user/paymentsList",
            data: paymentsFilter,
            dataType: "json",
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
                $("#ajax-loader-paymentList").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading payment list...');
            },
            complete: function() {
                $("#ajax-loader-paymentList").empty();
            },
            success: function(paymentsJson) {
                paymentsList = paymentsJson;
                buildPaymentsListTable();
            },
            error: function(jqXHR, textStatus, errorThrown) {
            	$('#error-loading-payments').removeClass("hidden");
            }
        });
    } else {
        buildPaymentsListTable();
    }
}

function buildItemsListTable() {
	//check if the payment list is empty
    if (itemList.length > 0) {
        //Set Badge
        $("#paymentListBadge").html(itemList.length);
        $("#itemList").removeClass('hidden').addClass('show');
        $("#listPageHeader").text('Item List');

        var itemsListTable = $('#itemListTbl').dataTable( {
    		"lengthMenu": [[25, 50, 100, 500, -1], [25, 50, 100, 500, "All"]],
    		"pagingType": "full_numbers",
    		"autoWidth": false,
    		"order": [[ 1, "asc" ]],
    		"data": itemList,
    		"columns": [
    		            { "data": "accountNumber", className: "right"},
						{ "data": "checkNumber", className: "right"},
    		            { "data": "accountName", className: "center"},
    		            { "data": "bankName" },
    		            { "data": "itemType" , className: "center"},
    		            { "data": "itemAmount", className: "right" },
    		            { "data": "itemDate", className: "center"},
    		            { "data": "createdBy", className: "center"},
    		            { "data": "createdMethod"}
    		        ],
    		"dom": '<"top"if><t><"bottom"lp>',
    		"language": {
    			"emptyTable":     "No data available in table",
    			"info":           "Showing _START_ to _END_ of _TOTAL_ records",
    			"infoEmpty":      "Showing 0 to 0 of 0 records",
    			"infoFiltered":   "(filtered from _MAX_ total records)",
    			"thousands":      ",",
    			"decimal":		  ".",
    			"lengthMenu":     "Records per page _MENU_",
    			"loadingRecords": "Loading...",
    			"processing":     "Processing...",
    			"search":         "Search:",
    			"zeroRecords":    "No matching records found"
    		},
			"deferRender": true,
			"columnDefs": [
				{
					"targets": 1,
					"data": "checkNumber",
					"render": function ( data, type, full, meta ) {
						return "<a href='#' onclick='showItemDetailPopUp(" + full.checkId + ", " + data + ", " + full.accountNumber + ", &quot;" + full.itemType + "&quot;" + ", &quot;" + full.traceNumber + "&quot;);'>" + data + " <i class='icon-fixed-width icon-camera'></i></a>";
					}
				},
			    {
					"targets": 5,
					"data": "itemAmount",
					"render": function ( data, type, full, meta ) {
					  return formatDollarAmount(data);
					}
			    },
			    {
					"targets": 6,
					"data": "itemDate",
					"render": function ( data, type, full, meta ) {
					  return getFormattedDateFromDateString(data);
					}
			    }
			]
    	});
		$('#itemListTbl').removeClass("dataTable");
		//Fixed header
		//new $.fn.dataTable.FixedHeader( paymentListTable );
		//$('div').removeClass("FixedHeader_Cloned");
		//scroll to top of the page on click of pagination.
		$("#itemListTbl_paginate").click(function() {
			$('html, body').animate({scrollTop:0}, 'slow');
		});
    } else {
        //No data to display
        $("#noPayments").removeClass('hidden');
    }
}

function populatePaymentListForPaymentsInfoPage() {
	var dataCriteriaDto = window.localStorage.getItem("dataCriteriaDto");
	if(dataCriteriaDto == null || dataCriteriaDto == "") {
		dataCriteriaDto = "{}";
		$("#paymentList").notify("Fetching all records as no search parameters were specified.", {className: "warn", arrowShow: false, autoHideDelay: 3000});
	}
    if (paymentsList.length == 0) {
        $.ajax({
            type: "POST",
            url: globalBaseURL + "/user/paymentsinfo/search",
            data: dataCriteriaDto,
            dataType: "json",
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
                $("#ajax-loader-paymentList").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading payment list...');
            },
            complete: function() {
                $("#ajax-loader-paymentList").empty();
            },
            success: function(paymentsJson) {
                paymentsList = paymentsJson;
                buildPaymentsListTable();
            },
            error: function(jqXHR, textStatus, errorThrown) {
            	$('#error-loading-payments').removeClass("hidden");
            }
        });
    } else {
        buildPaymentsListTable();
    }
}

//function buildPaymentsListTable(start, end) {
function buildPaymentsListTable() {
    //check if the payment list is empty
    if (paymentsList.length > 0) {
        //Set Badge
        $("#paymentListBadge").html(paymentsList.length);
        $("#paymentList").removeClass('hidden').addClass('show');
        var header = window.localStorage.getItem("lookfor");
        $("#listPageHeader").text('Payment List');
       
        var paymentListTable = $('#paymentListTbl').dataTable( {
    		"lengthMenu": [[25, 50, 100, 500, -1], [25, 50, 100, 500, "All"]],
    		"pagingType": "full_numbers",
    		"autoWidth": false,
    		"order": [[ 1, "asc" ]],
    		"data": paymentsList,
    		"columns": [
    		            { "data": "accountNumber", className: "right"},
						{ "data": "checkNumber", className: "right"},
    		            { "data": "paymentStatus", className: "center"},
    		            { "data": "matchStatus" },
    		            { "data": "exceptionType" },
    		            { "data": "exceptionStatus" },
    		            { "data": "issuedAmount", className: "right"},
    		            { "data": "issuedDate", className: "center" },
    		            { "data": "paidAmount", className: "right" },
    		            { "data": "paidDate", className: "center" },
    		            { "data": "stopDate", className: "center" },
    		            { "data": "voidDate", className: "center" },
    		            { "data": "accountName"}
    		        ],
    		"dom": '<"top"if><t><"bottom"lp>',
    		"language": {
    			"emptyTable":     "No data available in table",
    			"info":           "Showing _START_ to _END_ of _TOTAL_ records",
    			"infoEmpty":      "Showing 0 to 0 of 0 records",
    			"infoFiltered":   "(filtered from _MAX_ total records)",
    			"thousands":      ",",
    			"decimal":		  ".",
    			"lengthMenu":     "Records per page _MENU_",
    			"loadingRecords": "Loading...",
    			"processing":     "Processing...",
    			"search":         "Search:",
    			"zeroRecords":    "No matching records found"
    		},
			"deferRender": true,
			"columnDefs": [ 
				{
					"targets": 1,
					"data": "checkNumber",
					"render": function ( data, type, full, meta ) {
						if(full.exceptionType != null && (full.exceptionType.toLowerCase() == 'duplicate paid item exception' || full.exceptionType.toLowerCase() == 'duplicate stop item exception')) {
							return "<a href='#' onclick='showCameraPopUpForDuplicatePaidException(" + full.checkId + ", " + full.accountNumber + ", &quot;" + full.paymentStatus + "&quot;, " + full.workflowId + ", " + data + ");'>" + data + " <i class='icon-fixed-width icon-camera'></i></a>";
						} else {
							return "<a href='#' onclick='showCameraPopUp(" + full.checkId + ", " + data + ", " + full.accountNumber + ", &quot;" + full.paymentStatus + "&quot;, " + full.workflowId + ", &quot;" + full.traceNumber + "&quot;);'>" + data + " <i class='icon-fixed-width icon-camera'></i></a>";
						}
					}
				},
			    {
					"targets": 6,
					"data": "issuedAmount",
					"render": function ( data, type, full, meta ) {
						return formatDollarAmount(data);
					}
			    },
			    {
					"targets": 7,
					"data": "issuedDate",
					"render": function ( data, type, full, meta ) {
						return getFormattedDateFromDateString(data);
					}
			    },
			    {
					"targets": 8,
					"data": "paidAmount",
					"render": function ( data, type, full, meta ) {
						return formatDollarAmount(data);
					}
			    },
			    {
					"targets": 9,
					"data": "paidDate",
					"render": function ( data, type, full, meta ) {
						return getFormattedDateFromDateString(data);
					}
			    },
			    {
					"targets": 10,
					"data": "stopDate",
					"render": function ( data, type, full, meta ) {
						return getFormattedDateFromDateString(data);
					}
			    },
			    {
					"targets": 11,
					"data": "voidDate",
					"render": function ( data, type, full, meta ) {
						return getFormattedDateFromDateString(data);
					}
			    }
			]
    	});
		$('#paymentListTbl').removeClass("dataTable");
		//Fixed header
		//new $.fn.dataTable.FixedHeader( paymentListTable );
		//$('div').removeClass("FixedHeader_Cloned");
		//scroll to top of the page on click of pagination.
		$("#paymentListTbl_paginate").click(function() {
			$('html, body').animate({scrollTop:0}, 'slow');
		});
    } else {
        //No data to display
        $("#noPayments").removeClass('hidden');
    }
}

function showCheckImages(checkId, showBoth) {
    //remove the existing images if any
    $('#checkImageTbl > tbody > tr > td').remove();
    $('#origcheckImageTbl > tbody > tr > td').remove();
    //Get front check details
    showCheckImage(checkId, "f");
    if(showBoth) {
	    //Get back check details after 1 second
	    setTimeout(function() {
	        showCheckImage(checkId, "b");
	    }, 200);
    }
}
function showCheckImage(checkId, side) {
    //Show check images
	var alt = "";
	if (side == 'f') {
		alt = "Front of the Check";
	} else {
		alt = "Back of the check";
	}
	var url = globalBaseURL + "/check/image?checkId=" + checkId + "&side=" + side;
	var tableRow = '<td><img class="zoom" style="border:1px solid" src="' + url + '" width="500" height="250" alt="' + alt + '"></td>';
	$('#checkImageTbl > tbody > tr:last').append(tableRow);
    $('#origcheckImageTbl > tbody > tr:last').append(tableRow);
    $('.zoom').loupe();
}

function validateField(fieldName, fieldValue, displayErrorFor) {
	var errors = new Array();
	 
	if(fieldValue == "" || !isNumber(fieldValue)) {
		var errorMessage = fieldName + " can not be empty and should be numeric";
        errors.push(errorMessage);
	}
	
	var errorDiv;
	var erroBoxDiv;
	
	if(displayErrorFor == 'duplicate') {
		errorDiv = $("#duplicateerrors");
		erroBoxDiv = $("#duplicateerrorBox");
	} else {
		errorDiv = $("#errors");
		erroBoxDiv = $("#errorBox");
	}
	
	var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
	return isValid;
}

function addErrorToDiv(errors,errorDiv,erroBoxDiv) {
	 //check if errors
    if (errors.length > 0) {
        //empty previous errors if any
    	errorDiv.empty();
        
        //errors found, show the error box
        if (erroBoxDiv.hasClass('hidden')) {
        	erroBoxDiv.removeClass('hidden').addClass('show');
        }
        //Add all errors to the errors div
        for (var i = 0; i < errors.length; i++) {
        	errorDiv.append($("<li style='display: list-item;'></li>").html(errors[i]));
        }
        return false;
    } else {
        //No error, validation pass, so hide error box
    	erroBoxDiv.addClass('hidden');
        //Empty error Message
    	errorDiv.empty();
        return true;
    }
}

function saveAccountInfo() {
	var checkId = $("#checkId").text();
	var traceNumber = $("#traceId").text();
	var accountNumber =  $('#accountNumber').val();
	var checkNumber=  $('#checkNumber').val();
	var payee =  $('#payee').val();
	var checkDate =  $('#checkDate').val();
	var userComment = $('#comment').val();
	var action = $("#action").val();
	var amount = $("#amount").val();
	var url = "";
	var querystring = "checkId=" + checkId + "&actionName=" + action;
	//validate all text areas
	
	if(action == "") {
		return;
	}
	var actionDescription = "Performing action...";
	if(action == 'changeAccountNumber') {
		url = globalBaseURL + "/user/workflow/changeAccountNumber?ACCOUNT_NUMBER_NEW=" + accountNumber;
		actionDescription="Changing account number...";
	} else if(action == 'changeCurrentAccountNumber') {
		url = globalBaseURL + "/user/workflow/changeAccountNumber?ACCOUNT_NUMBER_NEW=" + accountNumber;
		actionDescription="Changing current account number...";
	} else if(action == 'changeCurrentCheckNumber') {
		url = globalBaseURL + "/user/workflow/changeCheckNumber?CHECK_NUMBER_NEW=" + checkNumber;
		if(!validateField('Check Number', checkNumber, 'checkdetails')) {
			return;
		}
		actionDescription="Changing current check number...";
	}else if(action == 'changeCheckNumber') {
		if(checkId == "0") {
			url = globalBaseURL + "/user/nonWorkflow/changeZeroedCheckNumber";
			amount = amount.substring(1);
			amount = appendZeroesToAmount(amount);
			querystring = "traceNumber=" + traceNumber + "&changedCheckNumber=" + checkNumber +  "&accountNumber=" + accountNumber +  "&amount=" + amount;
		}
		else {
			url = globalBaseURL + "/user/workflow/changeCheckNumber?CHECK_NUMBER_NEW=" + checkNumber;
		}
		if(!validateField('Check Number', checkNumber, 'checkdetails')) {
			return;
		}
		actionDescription="Changing check number...";
	} else if(action == 'changePayee') {
		var errors = new Array();
		 
		if(payee == "") {
			var errorMessage = "Payee can not be empty";
	        errors.push(errorMessage);
		}
		
		var errorDiv;
		var erroBoxDiv;
		
		errorDiv = $("#errors");
		erroBoxDiv = $("#errorBox");
			
		var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
		if(!isValid) {
			return;
		}
		url = globalBaseURL + "/check/payee";
		querystring = "checkId=" + checkId + "&payeeName=" + payee;
		actionDescription = "Changing payee...";
	} else if(action == 'changeCheckDate') {
		var errors = new Array();
		 
		if(checkDate == "" || !isValidDate(checkDate)) {
			var errorMessage = "Check date is not valid.";
	        errors.push(errorMessage);
		}
		
		var errorDiv;
		var erroBoxDiv;
		
		errorDiv = $("#errors");
		erroBoxDiv = $("#errorBox");
			
		var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
		if(!isValid) {
			return;
		}
		url = globalBaseURL + "/check/date";
		querystring = "checkId=" + checkId + "&date=" + checkDate;
		actionDescription = "Changing check date...";
	}else if(action == 'addComment') {
		var errors = new Array();
		 
		if(userComment == "") {
			var errorMessage = "User comment can not be empty";
	        errors.push(errorMessage);
		}
		
		var errorDiv;
		var erroBoxDiv;
		
		errorDiv = $("#errors");
		erroBoxDiv = $("#errorBox");
			
		var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
		if(!isValid) {
			return;
		}
		url = globalBaseURL + "/add/comment";
		querystring = "checkId=" + checkId + "&comment=" + userComment;
		actionDescription = "Adding comment...";
	}else if(action == 'removeVoid' || action == 'removeStop') {
		url = globalBaseURL + "/remove/stoporvoid";
		querystring = "checkId=" + checkId;
		actionDescription = "Performing action...";
	} else {
		url = globalBaseURL + "/user/workflow/generic";
	}
	
	//save account info
	
	$.ajax({
        type: "POST",
        url: url,
        data: querystring,
        beforeSend: function(xhr) {
            $("#ajax-loader-saveAccountinfo").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> ' + actionDescription);
        },
        complete: function() {
            $("#ajax-loader-saveAccountinfo").empty();
        },
        success: function() {
        	$('#checkDetailPopUp').modal('hide');
        	$("#accountinfoSaveMessage").notify("Action performed successfully!", {className: "warn", arrowShow: false, autoHideDelay: 2000});
        	//reload the page to show changed information
        	setTimeout(function(){location.reload();}, 2000);
        },
        error: function(jqXHR, textStatus, errorThrown) {
        	var genericMessage = "Error " + actionDescription + " : ";
        	if(jqXHR.responseText.indexOf("Session Timed Out") != -1) {
        		window.location.replace(globalBaseURL + "/login?sessionExpired=true");
        	}
        	var response = jQuery.parseJSON(jqXHR.responseText);
        	if(response != null && typeof(response) !== "undefined") {
        		var message = response.message;
        		if(message != null && typeof(message) !== "undefined") {
        			var errorMessage = jQuery.parseJSON(message);
        			var error = errorMessage.error;
        			var transId = errorMessage.transactionId;
        			genericMessage = genericMessage + "error is : " + error + " , transaction Id : " + transId;
        		}
        	}
        	$('#checkDetailPopUp').modal('hide');
        	$("#accountinfoSaveMessage").notify(genericMessage, {className: "error", arrowShow: false, autoHideDelay: 3000});
        }
    });	
}

function appendZeroesToAmount(amount) {
	var currentLength = amount.substring(0,amount.indexOf(".")).length;
	var zeroesToAppend= 8-currentLength;
	for(i=0;i<zeroesToAppend;i++) {
		amount = "0"+amount;
	}
	return amount;
}

function saveAccountInfoForDuplicateCheck() {
	var checkId = $("#dupcheckId").text();
	var accountNumber = $('#dupaccountNumber').val();
	var checkNumber= $('#dupcheckNumber').val();
	var payee = $('#duppayee').val();
	var checkDate = $('#dupcheckDate').val();
	var userComment = $('#dupcomment').val();
	var action = $("#dupaction").val();
	var url = "";
	var querystring = "checkId=" + checkId + "&actionName=" + action;
	var exceptionId = $("#exceptionId").val();
	//validate all text areas
	
	if(action == "") {
		return;
	}
	var actionDescription = "Performing action...";
	if(action == 'changeAccountNumber') {
		url = globalBaseURL + "/user/nonWorkflow/changeAccountNumber";
		querystring="exceptionalReferenceDataId=" + exceptionId + "&changedAccountNumber=" + accountNumber;
		actionDescription = "Changing account number...";
	} else if(action == 'changeCheckNumber') {
		url = globalBaseURL + "/user/nonWorkflow/changeCheckNumber";
		querystring = "exceptionalReferenceDataId=" + exceptionId + "&changedCheckNumber=" + checkNumber;
		if(!validateField('Check Number', checkNumber, 'duplicate')) {
			return;
		}
		actionDescription = "Changing check number...";
	} else if(action == 'deleteDuplicate') {
		url = globalBaseURL + "/user/nonWorkflow/delete";
		querystring="exceptionalReferencedataId=" + exceptionId;
		actionDescription = "Deleting duplicate...";
	} else if(action == 'pay') {
		url = globalBaseURL + "/user/nonWorkflow/pay";
		querystring="exceptionalReferenceDataId=" + exceptionId;
		actionDescription = "Performing action Pay...";
	} else if(action == 'addComment') {
		var errors = new Array();
		 
		if(userComment == "") {
			var errorMessage = "User comment can not be empty";
	        errors.push(errorMessage);
		}
		
		var errorDiv;
		var erroBoxDiv;
		
		errorDiv = $("#duplicateerrors");
		erroBoxDiv = $("#duplicateerrorBox");
			
		var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
		if(!isValid) {
			return;
		}
		url = globalBaseURL + "/add/comment";
		querystring = "checkId=" + checkId + "&comment=" + userComment;
		actionDescription = "Adding comment...";
	} 
	
	//save account info
	
	$.ajax({
        type: "POST",
        url: url,
        data: querystring,
        beforeSend: function(xhr) {
            $("#ajax-loader-orig-saveAccountinfo").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> ' + actionDescription);
        },
        complete: function() {
            $("#ajax-loader-orig-saveAccountinfo").empty();
        },
        success: function() {
        	$('#duplicatePaidPopUp').modal('hide');
        	$("#accountinfoSaveMessage").notify("Action performed successfully!", {className: "warn", arrowShow: false, autoHideDelay: 2000});
        	//reload the page to show changed information
        	setTimeout(function(){location.reload();}, 2000);
        },
        error: function(jqXHR, textStatus, errorThrown) {
        	var genericMessage = "Error " + actionDescription + " : ";
        	if(jqXHR.responseText.indexOf("Session Timed Out") != -1) {
        		window.location.replace(globalBaseURL + "/login?sessionExpired=true");
        	}
        	var response = jQuery.parseJSON(jqXHR.responseText);
        	if(response != null && typeof(response) !== "undefined") {
        		var message = response.message;
        		if(message != null && typeof(message) !== "undefined") {
        			var errorMessage = jQuery.parseJSON(message);
        			var error = errorMessage.error;
        			var transId = errorMessage.transactionId;
        			genericMessage = genericMessage + "error is : " + error + " , transaction Id : " + transId;
        		}
        	}
        	$('#duplicatePaidPopUp').modal('hide');
        	$("#accountinfoSaveMessage").notify(genericMessage, {className: "error", arrowShow: false, autoHideDelay: 3000});
        }
    });
}

function saveAccountInfoForDuplicatePaidPopUp() {
	var checkId = $("#dupcheckId").text();
	var accountNumber =  $('#origaccountNumber').val();
	var checkNumber=  $('#origcheckNumber').val();
	var payee =  $('#origpayee').val();
	var checkDate =  $('#origcheckDate').val();
	var userComment = $('#origcomment').val();
	var action = $("#origaction").val();
	var url = "";
	var querystring = "checkId=" + checkId + "&actionName=" + action;
	//validate all text areas
	
	if(action == "") {
		return;
	}
	var actionDescription = "Performing action...";
	if(action == 'changeAccountNumber') {
		url = globalBaseURL + "/user/workflow/changeAccountNumber?ACCOUNT_NUMBER_NEW=" + accountNumber;
		actionDescription = "Changing account number...";
	} else if(action == 'changeCurrentAccountNumber') {
		url = globalBaseURL + "/user/workflow/changeAccountNumber?ACCOUNT_NUMBER_NEW=" + accountNumber;
		actionDescription = "Changing current account number...";
	} else if(action == 'changeCurrentCheckNumber') {
		url = globalBaseURL + "/user/workflow/changeCheckNumber?CHECK_NUMBER_NEW=" + checkNumber;
		if(!validateField('Check Number', checkNumber, 'checkdetails')) {
			return;
		}
		actionDescription = "Changing current check number...";
	} else if(action == 'changeCheckNumber') {
		url = globalBaseURL + "/user/workflow/changeCheckNumber?CHECK_NUMBER_NEW=" + checkNumber;
		if(!validateField('Check Number', checkNumber, 'duplicate')) {
			return;
		}
		actionDescription = "Changing check number...";
	} else if(action == 'changePayee') {
		var errors = new Array();
		 
		if(payee == "") {
			var errorMessage = "Payee can not be empty";
	        errors.push(errorMessage);
		}
		
		var errorDiv;
		var erroBoxDiv;
		
		errorDiv = $("#duplicateerrors");
		erroBoxDiv = $("#duplicateerrorBox");
			
		var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
		if(!isValid) {
			return;
		}
		url = globalBaseURL + "/check/payee";
		querystring = "checkId=" + checkId + "&payeeName=" + payee;
		actionDescription = "Changing payee...";
	} else if(action == 'changeCheckDate') {
		var errors = new Array();
		 
		if(checkDate == "" || !isValidDate(checkDate)) {
			var errorMessage = "Check date is not valid.";
	        errors.push(errorMessage);
		}
		
		var errorDiv;
		var erroBoxDiv;
		
		errorDiv = $("#duplicateerrors");
		erroBoxDiv = $("#duplicateerrorBox");
			
		var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
		if(!isValid) {
			return;
		}
		url = globalBaseURL + "/check/date";
		querystring = "checkId=" + checkId + "&date=" + checkDate;
		actionDescription = "Changing check date...";
	} else if(action == 'addComment') {
		var errors = new Array();
		 
		if(userComment == "") {
			var errorMessage = "User comment can not be empty";
	        errors.push(errorMessage);
		}
		
		var errorDiv;
		var erroBoxDiv;
		
		errorDiv = $("#duplicateerrors");
		erroBoxDiv = $("#duplicateerrorBox");
			
		var isValid = addErrorToDiv(errors,errorDiv,erroBoxDiv);
		if(!isValid) {
			return;
		}
		url = globalBaseURL + "/add/comment";
		querystring = "checkId=" + checkId + "&comment=" + userComment;
		actionDescription = "Adding comment...";
	}else if(action == 'removeVoid' || action == 'removeStop') {
		url = globalBaseURL + "/remove/stoporvoid";
		querystring = "checkId=" + checkId;
		actionDescription = "Performing action...";
	} else {
		url = globalBaseURL + "/user/workflow/generic";
	}
	
	//save account info
	
	$.ajax({
        type: "POST",
        url: url,
        data: querystring,
        beforeSend: function(xhr) {
            $("#ajax-loader-orig-saveAccountinfo").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> ' + actionDescription);
        },
        complete: function() {
            $("#ajax-loader-orig-saveAccountinfo").empty();
        },
        success: function(paymentsJson) {
        	$('#duplicatePaidPopUp').modal('hide');
        	$("#accountinfoSaveMessage").notify("Action performed successfully!", {className: "warn", arrowShow: false, autoHideDelay: 2000});
        	//reload the page to show changed information
        	setTimeout(function(){location.reload();}, 2000);
        },
        error: function(jqXHR, textStatus, errorThrown) {
        	if(jqXHR.responseText.indexOf("Session Timed Out") != -1) {
        		window.location.replace(globalBaseURL + "/login?sessionExpired=true");
        	}
        	var genericMessage = "Error " + actionDescription + " : ";
        	var response = jQuery.parseJSON(jqXHR.responseText);
        	if(response != null && typeof(response) !== "undefined") {
        		var message = response.message;
        		if(message != null && typeof(message) !== "undefined") {
        			var errorMessage = jQuery.parseJSON(message);
        			var error = errorMessage.error;
        			var transId = errorMessage.transactionId;
        			genericMessage = genericMessage + "error is : " + error + " , transaction Id : " + transId;
        		}
        	}
        	$('#duplicatePaidPopUp').modal('hide');
        	$("#accountinfoSaveMessage").notify(genericMessage, {className: "error", arrowShow: false, autoHideDelay: 3000});
        }
    });	
}

function resolveAction(action) {
	if(action == 'changeAccountNumber' || action == 'changeCurrentAccountNumber') {
		$("#accountNumber").prop('disabled', false);
		$("#checkNumber").prop('disabled', true);
		$("#amount").prop('disabled', true);
		$("#payee").prop('disabled', true);
		$("#checkDate").prop('disabled', true);
		$("#comment").prop('disabled', true);
		
		//disable original text areas
		
		$("#origaccountNumber").prop('disabled', false);
		$("#origcheckNumber").prop('disabled', true);
		$("#origamount").prop('disabled', true);
		$("#origpayee").prop('disabled', true);
		$("#origcheckDate").prop('disabled', true);
		$("#origcomment").prop('disabled', true);
	} else if(action == 'changeCheckNumber' || action == 'changeCurrentCheckNumber') {
		$("#checkNumber").prop('disabled', false);
		$("#accountNumber").prop('disabled', true);
		$("#amount").prop('disabled', true);
		$("#payee").prop('disabled', true);
		$("#checkDate").prop('disabled', true);
		$("#comment").prop('disabled', true);
		
		//disable original text areas
		
		$("#origaccountNumber").prop('disabled', true);
		$("#origcheckNumber").prop('disabled', false);
		$("#origamount").prop('disabled', true);
		$("#origpayee").prop('disabled', true);
		$("#origcheckDate").prop('disabled', true);
		$("#origcomment").prop('disabled', true);
	} else if(action == 'changePayee') {
		$("#payee").prop('disabled', false);
		$("#accountNumber").prop('disabled', true);
		$("#checkNumber").prop('disabled', true);
		$("#amount").prop('disabled', true);
		$("#checkDate").prop('disabled', true);
		$("#comment").prop('disabled', true);
		
		//disable original text areas
		
		$("#origaccountNumber").prop('disabled', true);
		$("#origcheckNumber").prop('disabled', true);
		$("#origamount").prop('disabled', true);
		$("#origpayee").prop('disabled', false);
		$("#origcheckDate").prop('disabled', true);
		$("#origcomment").prop('disabled', true);
		
	} else if(action == 'changeCheckDate') {
		$("#checkDate").prop('disabled', false);
		$("#accountNumber").prop('disabled', true);
		$("#checkNumber").prop('disabled', true);
		$("#amount").prop('disabled', true);
		$("#payee").prop('disabled', true);
		$("#comment").prop('disabled', true);
		
		//disable original text areas
		
		$("#origaccountNumber").prop('disabled', true);
		$("#origcheckNumber").prop('disabled', true);
		$("#origamount").prop('disabled', true);
		$("#origpayee").prop('disabled', true);
		$("#origcheckDate").prop('disabled', false);
		$("#origcomment").prop('disabled', true);
		
	}else if(action == 'addComment') {
		$("#checkDate").prop('disabled', true);
		$("#accountNumber").prop('disabled', true);
		$("#checkNumber").prop('disabled', true);
		$("#amount").prop('disabled', true);
		$("#payee").prop('disabled', true);
		$("#comment").prop('disabled', false);
		//disable original text areas
		
		$("#origaccountNumber").prop('disabled', true);
		$("#origcheckNumber").prop('disabled', true);
		$("#origamount").prop('disabled', true);
		$("#origpayee").prop('disabled', true);
		$("#origcheckDate").prop('disabled', true);
		$("#origcomment").prop('disabled', false);
		
	} else {
		disableTextArea();
	}
}

function resolveDuplicatePaidAction(action) {
	if(action == 'changeAccountNumber') {
		//disable duplicate text areas
		
		$("#dupaccountNumber").prop('disabled', false);
		$("#dupcheckNumber").prop('disabled', true);
		$("#dupamount").prop('disabled', true);
		$("#duppayee").prop('disabled', true);
		$("#dupcheckDate").prop('disabled', true);
		$("#dupcomment").prop('disabled', true);
	} else if(action == 'changeCheckNumber') {
		//disable duplicate text areas
		
		$("#dupaccountNumber").prop('disabled', true);
		$("#dupcheckNumber").prop('disabled', false);
		$("#dupamount").prop('disabled', true);
		$("#duppayee").prop('disabled', true);
		$("#dupcheckDate").prop('disabled', true);
		$("#dupcomment").prop('disabled', true);
	} else if(action == 'addComment') {
		//disable duplicate text areas
		
		$("#dupaccountNumber").prop('disabled', true);
		$("#dupcheckNumber").prop('disabled', true);
		$("#dupamount").prop('disabled', true);
		$("#duppayee").prop('disabled', true);
		$("#dupcheckDate").prop('disabled', true);	
		$("#dupcomment").prop('disabled', false);
	} else {
		disableTextArea();
	}
}

function disableTextArea() {
	$("#accountNumber").prop('disabled', true);
	$("#checkNumber").prop('disabled', true);
	$("#amount").prop('disabled', true);
	$("#payee").prop('disabled', true);
	$("#checkDate").prop('disabled', true);
	$("#comment").prop('disabled', true);
	
	//disable original text areas
	
	$("#origaccountNumber").prop('disabled', true);
	$("#origcheckNumber").prop('disabled', true);
	$("#origamount").prop('disabled', true);
	$("#origpayee").prop('disabled', true);
	$("#origcheckDate").prop('disabled', true);
	$("#origcomment").prop('disabled', true);
	
	//disable duplicate text areas
	
	$("#dupaccountNumber").prop('disabled', true);
	$("#dupcheckNumber").prop('disabled', true);
	$("#dupamount").prop('disabled', true);
	$("#duppayee").prop('disabled', true);
	$("#dupcheckDate").prop('disabled', true);
	$("#dupcomment").prop('disabled', true);
}

function showCameraPopUpForDuplicatePaidException(checkId, accountNumber, paymentStatus, workFlowId, checkNumber) {
	$('#duplicatePaidPopUp').modal();
	$('#duplicatePaidPopUp > div.modal-dialog').css('margin-top','100px');
	$("#dupcheckId").text(checkId);
	var erroBoxDiv = $("#duplicateerrorBox");
	erroBoxDiv.addClass('hidden');
	//show check images
	showCheckImages(checkId, false);
	
	loadAccountNumbers(accountNumber);
	
	//submit ajax request to load actions
	var paymentDetailDto = {paymentStatus:'', workflowId: '', checkId: ''};
	paymentDetailDto.paymentStatus = paymentStatus.toLowerCase();
	paymentDetailDto.workflowId = workFlowId;
	paymentDetailDto.checkId = checkId;
	
	$.ajax({
        type: "POST",
        url: globalBaseURL + "/user/alladminactions",
        data: JSON.stringify(paymentDetailDto),
        dataType: "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        },
        complete: function() {
        },
        success: function(actionsJson) {
        	 $('#origaction').find('option').remove();
            $.each(actionsJson, function(index, value) {
                $('#origaction').append($('<option>', {
                    value: index,
                    text: value
                }));
            });
            $('#origaction').append($("<option value='addComment'>Add Comment</option>"));
            var optionList = $('#origaction option');
            optionList.sort(function(a, b) {
            	var actionA = $(a).text().toUpperCase();
                var actionB = $(b).text().toUpperCase();
                return (actionA < actionB) ? -1 : (actionA > actionB) ? 1 : 0;
            });
            $.each(optionList, function(idx, itm) { 
                $(this).parent().append(itm); 
           });
            $('#origaction').prepend($("<option value=''>Select action...</option>"));
            $("#origaction option:contains('Select action...')").attr("selected", true);
            $('#origaction').notify("Actions populated.", "success");
            disableTextArea();
        },
        error: function(jqXHR, textStatus, errorThrown) {
        }
    });
	
	//load actions for duplicate check
	
	$.ajax({
        type: "GET",
        url: globalBaseURL + "/user/allnonworkflowactions",
        dataType: "json",
        contentType: 'application/json',
        success: function(actionsJson) {
        	$('#dupaction').find('option').remove();
            $.each(actionsJson, function(index, value) {
                $('#dupaction').append($('<option>', {
                    value: index,
                    text: value
                }));
            });
            $('#dupaction').append($("<option value='addComment'>Add Comment</option>"));
            if(paymentStatus == 'Paid') {
            	$('#dupaction').append($("<option value='pay'>Pay</option>"));
            }
            var optionList = $('#dupaction option');
            optionList.sort(function(a, b) {
            	var actionA = $(a).text().toUpperCase();
                var actionB = $(b).text().toUpperCase();
                return (actionA < actionB) ? -1 : (actionA > actionB) ? 1 : 0;
            });
            $.each(optionList, function(idx, itm) { 
                $(this).parent().append(itm); 
           });
            $('#dupaction').prepend($("<option value=''>Select action...</option>"));
            $("#dupaction option:contains('Select action...')").attr("selected", true);
            $('#dupaction').notify("Actions populated.", "success");
            disableTextArea();
        },
        error: function(jqXHR, textStatus, errorThrown) {
        }
    });
	
	
	//Submit ajax request to get check details
	var queryString = "checkId=" + checkId;
	$.ajax({
		type: "GET",
		url: globalBaseURL + "/user/exceptions/checkdetails?checkId=" + checkId,
		data: queryString,
		dataType: "json",
		contentType: 'application/json',
		success: function(checkDetailsJson) {
			$("#origcheckNumber").val(formatString(checkDetailsJson.checkNumber));
			$("#origpayee").val(formatString(checkDetailsJson.payee));
			$("#origamount").val(formatDollarAmount(checkDetailsJson.issuedAmount));
			$("#origcheckDate").val(getFormattedDateFromDateString(checkDetailsJson.issueDate));
		},
		error: function(jqXHR, textStatus, errorThrown) {
		}
	});
	
	var itemType;
	if(paymentStatus == 'Paid') {
		itemType = 'PAID';
	} else if(paymentStatus == 'Stop') {
		itemType = 'STOP';
	}
	//Submit ajax request to get duplicate check details
	queryString = "checkNumber=" + checkNumber + "&accountNumber=" + accountNumber + "&itemType=" + itemType;
	$.ajax({
		type: "GET",
		url: globalBaseURL + "/user/duplicatecheckdetails",
		data: queryString,
		dataType: "json",
		contentType: 'application/json',
		success: function(exceptionalReferenceJson) {
			exceptionDataList = exceptionalReferenceJson;
			
			$("#dupcheckNumber").val("");
			$("#exceptionId").val("");
			$("#dupamount").val("");
			$("#dupcheckDate").val("");
			
			var count = 1;
			if (exceptionDataList.length > 0) {
				$("#dupaction").prop('disabled', true);
				$("#dupSave").prop('disabled', true);
				
				$('#duplicatedetails').find('option').remove();
				$('#duplicatedetails').append($("<option value=''>Select Duplicate Details..</option>"));
				$.each(exceptionDataList, function(index, value) {
					 $('#duplicatedetails').append($('<option>', {
	                        value: value.expReferenceDataId,
	                        text: 'Duplicate Details '+ count.toString()
	                    }));
					 count++;
                });
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
		}
	});
	
	loadPaymentHistory(checkId);
}

function showDuplicateCheckImage(exceptionId) {
	//Submit ajax request to get duplicate check image
	$('#dupcheckImageTbl > tbody > tr > td').remove();
	var alt = "Front of the check";
	var url = globalBaseURL + "/exception/image?exceptionId=" + exceptionId + "&side=f";
	var tableRow = '<td><img class="zoom" style="border:1px solid" src="' + url + '" width="500" height="250" alt="' + alt + '"></td>';
	$('#dupcheckImageTbl > tbody > tr:last').append(tableRow);
    $('.zoom').loupe();
}

function showDuplicateDetails(exceptionId) {
	if(exceptionId == "") {
		disbaleDuplicateTextAreas();
		
		$("#dupaction").prop('disabled', true);
		$("#dupSave").prop('disabled', true);
	}else {
		$.each(exceptionDataList, function(index, value) {
			if(value.expReferenceDataId == exceptionId) {
				$("#dupaction").prop('disabled', false);
				$("#dupSave").prop('disabled', false);
				disbaleDuplicateTextAreas();
				
				$("#dupaction").val("");
				
				$("#dupcheckNumber").val(formatString(value.checkNumber));
				$("#exceptionId").val(formatString(value.expReferenceDataId));
				$("#dupamount").val(formatDollarAmount(value.amount));
				$("#dupcheckDate").val(getFormattedDate(value.date));
				
				//show duplicate image for selected check detail
				
				showDuplicateCheckImage(value.expReferenceDataId);
			}
       });
	}
}

function disbaleDuplicateTextAreas() {
	$("#dupaccountNumber").prop('disabled', true);
	$("#dupcheckNumber").prop('disabled', true);
	$("#dupamount").prop('disabled', true);
	$("#duppayee").prop('disabled', true);
	$("#dupcheckDate").prop('disabled', true);
	$("#dupcomment").prop('disabled', true);
}

function loadAccountNumbers(accountNumber) {
	//submit ajax request to load account numbers
	$.ajax({
		type: "GET",
		url: globalBaseURL + "/user/accountinfo/" + accountNumber,
		dataType: "json",
		contentType: 'application/json',
		beforeSend: function() {
			$("#ajax-loader-accountNumbers").html('<div class="infobox"><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading account numbers, please wait...</div>');
		},
		complete: function() {
			$("#ajax-loader-accountNumbers").empty();
		},
		success: function(accountNumbersJson) {
			if (accountNumbersJson.length > 0) {
                $('#accountNumber').find('option').remove();
                $('#origaccountNumber').find('option').remove();
                $('#dupaccountNumber').find('option').remove();
                $.each(accountNumbersJson, function(index, value) {
                    $('#accountNumber').append($('<option>', {
                        value: value,
                        text: value
                    }));
                    $('#origaccountNumber').append($('<option>', {
                        value: value,
                        text: 'Account No. ' + value
                    }));
                    $('#dupaccountNumber').append($('<option>', {
                        value: value,
                        text: 'Account No. ' + value
                    }));
                });
                $('#accountNumber').val(accountNumber);
                $('#origaccountNumber').val(accountNumber);
                $('#dupaccountNumber').val(accountNumber);
                $('#accountNumber').notify("Account numbers populated.", "success");
            }
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#error-loading-accountNumbers").removeClass('hidden');
		}
	});
}

function showItemDetailPopUp(checkId, checkNumber, accountNumber, itemType, traceNumber) {

	$('#itemDetailPopUp').modal();
	$('#itemDetailPopUp > div.modal-dialog').css('margin-top','100px');
	$("#itemCheckId").text(checkId);
	//hide the previous table momentarily.
	$("#itemDetailsTbl").addClass('hidden');
	$("#error-loading-itemdetails").addClass('hidden');
	
	var queryString = "";
	var url = "";
	if(checkId == "0") {
		url = globalBaseURL + "/user/zeroitemdetails";
		queryString = "traceNumber=" + traceNumber;
	}else {
		url = globalBaseURL + "/user/itemdetails";
		queryString = "checkNumber=" + checkNumber + "&accountNumber=" + accountNumber + "&itemType=" + itemType;
	}
	
	//Submit ajax request to get item details
	$.ajax({
		type: "GET",
		url: url,
		data: queryString,
		dataType: "json",
		contentType: 'application/json',
		beforeSend: function() {
			$("#ajax-loader-itemDetails").html('<div class="infobox"><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading item details, please wait...</div>');
		},
		complete: function() {
			$("#ajax-loader-itemDetails").empty();
		},
		success: function(paymentDetailsJson) {
			$("#itemDetailsTbl").removeClass('hidden');
			$("#itemcheckNumberVal").text(formatString(paymentDetailsJson.checkNumber));
			$("#itempayeeVal").text(formatString(paymentDetailsJson.payee));
			$("#itemaccountNumberVal").text(formatString(paymentDetailsJson.accountNumber));
			$("#itemAmountVal").text(formatDollarAmount(paymentDetailsJson.itemAmount));
			$("#itemcustomerVal").text(formatString(paymentDetailsJson.company));
			$("#itemDateVal").text(getFormattedDateFromDateString(paymentDetailsJson.itemDate));
			$("#itembankVal").text(formatString(paymentDetailsJson.bankName));
			$("#itemCodeVal").text(formatString(paymentDetailsJson.itemCode));
			$("#itemPaymentStatusVal").text(formatString(paymentDetailsJson.paymentStatus));
			$("#itemMatchStatusVal").text(formatString(paymentDetailsJson.matchStatus));
			$("#itemCreatedDateVal").text(formatString(paymentDetailsJson.createdDate));
			$("#itemTraceNoVal").text(formatString(paymentDetailsJson.traceNumber));
			$("#itemCreatedMethodVal").text(formatString(paymentDetailsJson.createdMethod));
			$("#itemCreatedByVal").text(formatString(paymentDetailsJson.createdBy));
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#error-loading-itemdetails").removeClass('hidden');
			$("#itemDetailsTbl").addClass('hidden');
		}
	});
}

function showCameraPopUp(checkId, checkNumber, accountNumber, paymentStatus, workFlowId, traceNumber) {
	$('#checkDetailPopUp').modal();
	$('#checkDetailPopUp > div.modal-dialog').css('margin-top','100px');
	$("#checkNumberValue").text("Check Number : " + checkNumber);
	$("#checkId").text(checkId);
	$("#traceId").text(traceNumber);
	//hide the previous table momentarily.
	$("#checkDetailsTbl").addClass('hidden');
	var erroBoxDiv = $("#errorBox");
	erroBoxDiv.addClass('hidden');
	//show check images
	showCheckImages(checkId, true);
	
	if(checkId == "0") {
		//load Actions
		$('#action').find('option').remove();
		$('#action').append($("<option value='changeCheckNumber'>Change Check Number</option>"));
		$('#action').prepend($("<option value=''>Select action...</option>"));
        $("#action option:contains('Select action...')").attr("selected", true);
        $('#action').notify("Actions populated.", "success");
        disableTextArea();
	} 
	
	else {
		if(typeof(paymentStatus)!=="undefined") {
			var paymentDetailDto = {paymentStatus:'', workflowId: '', checkId: ''};
			paymentDetailDto.paymentStatus = paymentStatus.toLowerCase();
			paymentDetailDto.workflowId = workFlowId;
			paymentDetailDto.checkId = checkId;
			//submit ajax request to load actions
			
			$.ajax({
	            type: "POST",
	            url: globalBaseURL + "/user/alladminactions",
	            data: JSON.stringify(paymentDetailDto),
	            dataType: "json",
	            beforeSend: function(xhr) {
	                xhr.setRequestHeader("Accept", "application/json");
	                xhr.setRequestHeader("Content-Type", "application/json");
	            },
	            complete: function() {
	            },
	            success: function(actionsJson) {
	            	$('#action').find('option').remove();
	                $.each(actionsJson, function(index, value) {
	                    $('#action').append($('<option>', {
	                        value: index,
	                        text: value
	                    }));
	                });
	                $('#action').append($("<option value='addComment'>Add Comment</option>"));
	                var optionList = $('#action option');
	                optionList.sort(function(a, b) {
	                	var actionA = $(a).text().toUpperCase();
	                    var actionB = $(b).text().toUpperCase();
	                    return (actionA < actionB) ? -1 : (actionA > actionB) ? 1 : 0;
	                });
	                $.each(optionList, function(idx, itm) { 
	                    $(this).parent().append(itm); 
	               });
	               $('#action').prepend($("<option value=''>Select action...</option>"));
	               $("#action option:contains('Select action...')").attr("selected", true);
	               $('#action').notify("Actions populated.", "success");
	               disableTextArea();
	            },
	            error: function(jqXHR, textStatus, errorThrown) {
	            }
	        });
		}
	}
	
	if(typeof(accountNumber) !== "undefined") {
		loadAccountNumbers(accountNumber);
	}
	
	var url = "";
	var queryString = "";
	
	if(checkId == "0") {
		url = globalBaseURL + "/user/exceptions/zerocheckdetails";
		queryString = "traceNumber=" + traceNumber;
	}else {
		url = globalBaseURL + "/user/exceptions/checkdetails";
		queryString = "checkId=" + checkId;
	}
	
	//Submit ajax request to get check details
	
	$.ajax({
		type: "GET",
		url: url,
		data: queryString,
		dataType: "json",
		contentType: 'application/json',
		beforeSend: function() {
			$("#ajax-loader-checkDetails").html('<div class="infobox"><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading check details, please wait...</div>');
		},
		complete: function() {
			$("#ajax-loader-checkDetails").empty();
		},
		success: function(checkDetailsJson) {
			$("#checkDetailsTbl").removeClass('hidden');
			$("#checkNumberVal").text(formatString(checkDetailsJson.checkNumber));
			$("#checkNumber").val(formatString(checkDetailsJson.checkNumber));
			$("#payeeVal").text(formatString(checkDetailsJson.payee));
			$("#payee").val(formatString(checkDetailsJson.payee));
			$("#accountNumberVal").text(formatString(checkDetailsJson.accountNumber));
			$("#issuedAmountVal").text(formatDollarAmount(checkDetailsJson.issuedAmount));
			if(!checkDetailsJson.issuedAmount == "")
				$("#amount").val(formatDollarAmount(checkDetailsJson.issuedAmount));
			else
				$("#amount").val(formatDollarAmount(checkDetailsJson.paidAmount));
			$("#customerVal").text(formatString(checkDetailsJson.accountName));
			$("#issuedDateVal").text(getFormattedDateFromDateString(checkDetailsJson.issueDate));
			$("#checkDate").val(getFormattedDateFromDateString(checkDetailsJson.issueDate));
			$("#bankVal").text(formatString(checkDetailsJson.bankName));
			$("#paidAmountVal").text(formatDollarAmount(checkDetailsJson.paidAmount));
			$("#bankNumberVal").text(formatString(checkDetailsJson.bankNumber));
			$("#paidDateVal").text(getFormattedDateFromDateString(checkDetailsJson.paidDate));
			$("#paymentStatusVal").text(formatString(checkDetailsJson.paymentStatus));
			$("#stopDateVal").text(getFormattedDateFromDateString(checkDetailsJson.stopDate));
			$("#matchStatusVal").text(formatString(checkDetailsJson.matchStatus));
			$("#voidDateVal").text(getFormattedDateFromDateString(checkDetailsJson.voidDate));
			$("#exceptionTypeVal").text(formatString(checkDetailsJson.exceptionType));
			$("#traceNoVal").text(formatString(checkDetailsJson.traceNumber));
			$("#exceptionStatusVal").text(formatString(checkDetailsJson.exceptionStatus));
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#error-loading-checkdetails").removeClass('hidden');
			$("#checkDetailsTbl").addClass('hidden');
		}
	});
	loadPaymentHistory(checkId);
}

function loadPaymentHistory(checkId) {
	//submit ajax request to load payment history
	$.ajax({
		type: "GET",
		url: globalBaseURL + "/user/paymenthistory/" + checkId,
		dataType: "json",
		contentType: 'application/json',
		beforeSend: function() {
			$("#ajax-loader-paymenthistory").html('<div class="infobox"><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading payment history, please wait...</div>');
			$("#ajax-loader-dup-paymenthistory").html('<div class="infobox"><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading payment history, please wait...</div>');
		},
		complete: function() {
			$("#ajax-loader-paymenthistory").empty();
			$("#ajax-loader-dup-paymenthistory").empty();
		},
		success: function(paymentHistoryList) {
			populatePaymentHistoryTable(paymentHistoryList);
			populatePaymentHistoryTableForDuplicatePopup(paymentHistoryList);
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#error-loading-paymenthistory").removeClass('hidden');
			$("#error-loading-dup-paymenthistory").removeClass('hidden');
		}
	});
}

function populatePaymentHistoryTable(paymentHistoryList) {
	//check if the recentFilesList list is empty
	if(paymentHistoryList.length > 0) {
		//Empty table data if any
		$('#paymenthistoryTbl > tbody:last').empty();
		$("#noPaymentHistory").addClass('hidden');
		//Make table rows by iterating over the payment list from start to end
		for(var i = paymentHistoryList.length-1; i >= 0 ; i--) {
			$("#paymenthistory").removeClass('hidden').addClass('show');
			var historyData = "";
			if(paymentHistoryList[i].resultingStatus == null) {
				historyData = "<td> N/A </td>";
			}
			else {
				historyData = "<td>"+paymentHistoryList[i].resultingStatus+"</td>";
			}
				
			var tableRow = "<tr id='paymenthistory'+(i+1)+''>"+
							"<td>"+(i+1)+"</td>"+
							"<td>"+getFormattedDateWithTimeStamp(paymentHistoryList[i].dateTime)+"</td>"+
							"<td>"+paymentHistoryList[i].user+"</td>"+
							"<td>"+paymentHistoryList[i].description+"</td>"+
							"<td>"+paymentHistoryList[i].comment+"</td>"+
							historyData +
							"<td>"+paymentHistoryList[i].createdMethod+"</td>"+
							"</tr>";
			$('#paymenthistoryTbl > tbody:last').append(tableRow);
		}
	} else {
		//No data to display
		$('#paymenthistoryTbl > tbody:last').empty();
		$("#noPaymentHistory").removeClass('hidden');
	}	
}

function populatePaymentHistoryTableForDuplicatePopup(paymentHistoryList) {
	//check if the recentFilesList list is empty
	if(paymentHistoryList.length > 0) {
		//Empty table data if any
		$('#duppaymenthistoryTbl > tbody:last').empty();
		$("#dupnoPaymentHistory").addClass('hidden');
		//Make table rows by iterating over the payment list from start to end
		for(var i = paymentHistoryList.length-1; i >= 0 ; i--)
		{
			$("#duppaymenthistory").removeClass('hidden').addClass('show');
			var historyData = "";
			if(paymentHistoryList[i].resultingStatus == null) {
				historyData = "<td> N/A </td>";
			}
			else {
				historyData = "<td>"+paymentHistoryList[i].resultingStatus+"</td>";
			}
			var tableRow = "<tr id='paymenthistory'+(i+1)+''>"+
							"<td>"+(i+1)+"</td>"+
							"<td>"+getFormattedDateWithTimeStamp(paymentHistoryList[i].dateTime)+"</td>"+
							"<td>"+paymentHistoryList[i].user+"</td>"+
							"<td>"+paymentHistoryList[i].description+"</td>"+
							"<td>"+paymentHistoryList[i].comment+"</td>"+
							historyData +
							"<td>"+paymentHistoryList[i].createdMethod+"</td>"+
							"</tr>";
			$('#duppaymenthistoryTbl > tbody:last').append(tableRow);
		}
	} else {
		//No data to display
		$('#duppaymenthistoryTbl > tbody:last').empty();
		$("#dupnoPaymentHistory").removeClass('hidden');
	}	
}
