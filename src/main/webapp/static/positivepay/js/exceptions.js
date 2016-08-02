$(document).ready(function() {
    payReasonsList = new Array();
    noPayReasonsList = new Array();
    
    var size = document.getElementById('mapsize').value;
	 $('[name^="checkbox"]').change(function(event) {
       if ($(this).is(":checked")) {
			for(var i=0; i<size; i++) {
				var j = Number(i) + 1;
				var decisionId = "decision" + j;
				//var text = $('#'+decisionId+' option:selected').text();
				if($('select[id=' + decisionId + ']').val() == 'noPay') {
					clearReasonsDD("", "exception" + j);
				}
				$('select[id=' + decisionId + ']').val("pay");
				var reasonId="reason"+j;
				var val = $('#' + decisionId + ' option[value=pay]').text();
				if($('#'+reasonId+' > option').size() == 1 && val == "Pay") {
					populateReasonsDropdown("pay", "exception" + j, true,reasonId);
				} else {
					var optionId = $('select[id=' + reasonId+']').find('option:contains("Pay")').val();
					$('select[id=' + reasonId+']').val(optionId);
				}
			}
       } else {
			for(var i = 0; i < size; i++) {
				var j = Number(i) + 1;
				var decisionId = "decision" + j;
				var reasonId = "reason" + j;
				var rowId = "exception" + j;
				clearReasonsDD("", rowId);
				var val = $('#' + decisionId + ' option[value=pay]').text();
				if(val == "Pay") {
					$('#' + decisionId).val(0);
					$('#' + reasonId).val('def');
				}
			}
		}
   });    
    
    //Click of resolve Exceptions Button
    $("#resolveExceptionsBtn").click(function() {
        var isExceptionDecisioningFormValid = true;
        var exceptionChecksArray = new Array();
        $('#exceptionDecisioningTbl > tbody > tr').each(function(i, row) {
            var decision = $(this).find('select[name="decision"]').val();
            var reason = $(this).find('select[name="reason"]').val();
            //Validate when the row is not hidden
            if (row.className != "hidden") {
                if (!decision || decision == 0) {
                    isExceptionDecisioningFormValid = false;
                    $(this).find('select[name="decision"]').addClass('highlight');
                } else {
                    $(this).find('select[name="decision"]').removeClass('highlight');
                }
                
                if (!reason || reason == 0) {
                    isExceptionDecisioningFormValid = false;
                    $(this).find('select[name="reason"]').addClass('highlight');
                } else {
                    $(this).find('select[name="reason"]').removeClass('highlight');
                }
                //make the check object and keep adding on to the check array
                var exceptionalCheck = {};
                exceptionalCheck.id = $(this).children('td:first').text();
                exceptionalCheck.decision = $(this).find('select[name="decision"]').val();
                exceptionalCheck.reason = $(this).find('select[name="reason"] option:selected').text();
                exceptionChecksArray.push(exceptionalCheck);
            }
        });
        if (!isExceptionDecisioningFormValid) {
            $("#exceptionDecisioningErrors").show();
            return;
        } else {
            $("#exceptionDecisioningErrors").hide();
        }
        //Submit ajax request to resolve exceptions
        $.ajax({
            type: "POST",
            url: "exceptions/resolve",
            data: JSON.stringify(exceptionChecksArray),
            dataType: "json",
            contentType: 'application/json',
            beforeSend: function() {
                $('#exceptionDecisioningTbl').block({
                    message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Resolving exceptions, please wait...</div>',
                    blockMsgClass: 'alert-warning',
                    css: {padding: '10px', color: '#000', border: '1px solid #006B87', 'border-radius': '5px', '-moz-border-radius': '5px', '-webkit-border-radius': '5px'}
                });
            },
            complete: function() {
                $('#exceptionDecisioningTbl').unblock();
            },
            success: function(checkJsonList) {
                //loop through the check list and remove those that were resolved with this call.
                for (var i = 0; i < checkJsonList.length; i++) {
                    var id = checkJsonList[i].id;
                    $('#exceptionDecisioningTbl > tbody > tr').each(function(i, row) {
                        var checkId = $(this).children('td:first').text();
                        if (checkId == id) {
                            //remove this row
                            row.remove();
                        }
                    });
                }
                //Show success message
                $("#exceptionResolutionMessage").notify("Exceptions resolved successfully!", {className: "warn", arrowShow: false, autoHideDelay: 3000});
                //Find number of rows in the table
                var rowCount = $('#exceptionDecisioningTbl > tbody > tr').length;
                //update the badge count
                $('#exceptionDecisionBadge').text(rowCount);
                if (rowCount == 0) {
                    //hide noOfExceptionsMessage
                    $('#noOfExceptionsMessage').addClass('hidden');
                    //show the appropriate message
                    $('#messageUponExceptionResolution').removeClass('hidden');
                    //hide table
                    $('#exceptionDecisioningTbl').hide('slow');
                    //hide the resolve exceptions button
                    $('#resolveExceptionsBtn').hide('slow');
                }
            },
            error: function(req, error, status) {
				if (req.status == '999') {
					notificationOptions = {
						arrowShow: false,
						className: 'success',
						style: 'bootstrap'
					};
                    $(".panel-title").notify("Operation Successful. However data was not saved because the user is in emulation mode.", notificationOptions);
                } else {
                	$("#exceptionResolutionMessage").notify("Error resolving exceptions!", {className: "error", arrowShow: false, autoHideDelay: 3000});
                }
			}
        });
    });
});

function populateReasonsDropdown(reasonType, rowId, showReasonSelected, reasonId) {
	var decisionId="decision"+rowId.slice(-1);
    //first clear
    clearReasonsDD(reasonType, rowId);
    if (reasonType == "") {
        return;
    }
    var isPay = true;
    if (reasonType.toUpperCase() == "NOPAY") {
        isPay = false;
    }
    if (isPay && payReasonsList.length > 0) {
        //Do not make ajax request, but fetch it from locally stored value
        for (var i = 0; i < payReasonsList.length; i++)
        {
            $('#' + rowId + '').find("select[name='reason']").append($("<option value='" + payReasonsList[i].id + "'>" + payReasonsList[i].name + "</option>"));
        }
    } else if (!isPay && noPayReasonsList.length) {
        //Do not make ajax request, but fetch it from locally stored value
        for (var i = 0; i < noPayReasonsList.length; i++)
        {
            $('#' + rowId + '').find("select[name='reason']").append($("<option value='" + noPayReasonsList[i].id + "'>" + noPayReasonsList[i].name + "</option>"));
        }
    } else {
        $.ajax({
            type: "GET",
            url: "exceptions/reasons?isPay=" + isPay,
            dataType: "json",
            success: function(reasonsJson) {
                for (var i = 0; i < reasonsJson.length; i++) {
                    var id = reasonsJson[i].id;
                    var name = reasonsJson[i].name;
                    var reason = {
                        id: id,
                        name: name,
                    };
                    if (isPay) {
                        payReasonsList.push(reason);
                    } else {
                        noPayReasonsList.push(reason);
                    }

                    $('#' + rowId + '').find("select[name='reason']").append($("<option value='" + id + "'>" + name + "</option>"));
                }
                if(showReasonSelected) {
                	var optionId = $('select[id=' + reasonId+']').find('option:contains("Pay")').val();
                	$('select[id=' + reasonId+']').val(optionId);
            	}
            },
        	async: false
        });
    }
    if(showReasonSelected) {
    	var optionId = $('select[id=' + reasonId+']').find('option:contains("Pay")').val();
    	$('select[id=' + reasonId+']').val(optionId);
	}
}

function clearReasonsDD(reasonType, rowId)
{
    var optionid = "def";
    var optionValue = "Now select reason";
    if (reasonType == "") {
        optionValue = "Select decision first";
    }
    $('#' + rowId + '').find("select[name='reason']").find('option').remove();
    $('#' + rowId + '').find("select[name='reason']").append($("<option value='" + optionid + "'>" + optionValue + "</option>"));
}

function filterExceptionDecisioning(accountNumber) {
    //Hide All rows first
    $("#exceptionDecisioningTbl > tbody > tr").addClass('hidden');
    //remove the highlighted class from all tds if previously errors were reported during validation
    $("td").find("select").removeClass('highlight');
    //And also hide the error message
    $("#exceptionDecisioningErrors").hide();
    //do nothing if no account number is chosen
    if (accountNumber == "") {
        $("#exceptionDecisioningTbl > tbody > tr").removeClass("hidden");
        return;
    }
    //filter based on the account number - only show the ones that match that account number
    $("#exceptionDecisioningTbl > tbody > tr").filter(function() {
        return $(this).text().indexOf(accountNumber) != -1;
    }).removeClass("hidden");
}