$(document).ready(function() {
    //Global Variable to track number of rows
     noOfRows = 1;
    //Global Variable to track row ids.
    rowIdsArray = new Array();
    rowIdsArray.push(1);
    companies = new Array();
    issueCodes = new Array();
    action = "ADD";
    copiedFromRowId = 1;
    //Initialize date field on row 1
    $("#issueDate1").datepicker({
        showAnim: "clip"
    });
	
    $('#issueCode1').change(function(){
		var text = $('option:selected', $(this)).text();
		var index = text.indexOf("-");
		if(index > -1) {
			text = text.substring(index+1, text.length);
			text = text + " Date";
		}
		
		$('#issueDate1').attr('placeholder',text);
    });
    
    //load company options and issueCode options and store them locally
    loadCodeOptions(1);
    loadCompanyOptions(1);

    //Save Manual Entry button Click
    $('#saveManualEntry').click(function(e) {
        //Check if the form is valid
        if (!isManualEntryFormValid()) {
            return false;
        }
        //The validation is passed, so now make the list objects
        var checkDetailsArray = new Array();
        for (var i = 0; i < rowIdsArray.length; i++) {
        	/*var issueDate = $("#issueDate" + rowIdsArray[i] + "").val();
            var parts = issueDate.split("/");
            var month = parseInt(parts[0], 10);
            var day = parseInt(parts[1], 10);
            var year = parseInt(parts[2], 10);*/
			
            var checkDetail = {
                "companyId": $("#company" + rowIdsArray[i] + "").val(),
                "accountNumber": $("#accountNumber" + rowIdsArray[i] + "").val(),
                "checkNumber": $("#checkNumber" + rowIdsArray[i] + "").val(),
                "issuedAmount": $("#checkAmount" + rowIdsArray[i] + "").val(),
                "issueCode": $("#issueCode" + rowIdsArray[i] + "").val(),
                //"issueDate": new Date(year, (month-1), day),
                "manualEntryDate": $("#issueDate" + rowIdsArray[i] + "").val(),
                "payee": $("#payee" + rowIdsArray[i] + "").val()
            };
            if (checkDetailsArray.length > 0) {
                if (containsDuplicateRow(checkDetailsArray, checkDetail, rowIdsArray[i])) {
                    return false;
                }
            }
            checkDetailsArray.push(checkDetail);
        }
		$("#error-saving-manualentries").addClass("hidden");
        //Post ajax requests
        $.ajax({
            type: "POST",
            url: "manualentry",
            data: JSON.stringify(checkDetailsArray),
            dataType: "json",
            contentType: 'application/json',
            beforeSend: function() {
                $("#ajax-loader-manualEntry").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Saving, please wait...');
            },
            complete: function() {
                $("#ajax-loader-manualEntry").empty();
            },
            success: function(jsonData) {
                if (jsonData.DUPLICATE_CHECKS != null && jsonData.DUPLICATE_CHECKS.length > 0) {
                    //Duplicate checks in database, loop through each tr to match duplicates and then highlight
                    $('#manualEntryTbl > tbody > tr').each(function() {
                        var accountNumber = $(this).find('select[name="accountNumber"]').val();
                        var checkNumber = $(this).find('input[name="checkNumber"]').val();
                        for (var i = 0; i < jsonData.DUPLICATE_CHECKS.length; i++) {
                            if (jsonData.DUPLICATE_CHECKS[i].accountNumber == accountNumber &&
                                    jsonData.DUPLICATE_CHECKS[i].checkNumber == checkNumber) {
                                $(this).addClass('highlight');
                            }
                        }
                    });

                    $("#successBox").removeClass('show').addClass('hidden');
                    $("#duplicateChecksErrorBox").removeClass('hidden').addClass('show');
                    $("#duplicateChecksErrorBox").empty();
                    $("#duplicateChecksErrorBox").html('<strong>Error!</strong> The highlighted payment items are already present in the database. Please correct and resubmit!');
                } else {
                    //remove hidden class from rows indicating duplicates in the database
                    $(".tblRow").removeClass('highlight');
                    //successfully added
                    $("#successBox").removeClass('hidden').addClass('show');
                    $("#successBox").empty();
                    $("#successBox").html('<strong>Success!</strong> Check Details Saved successfully!');
					resetForm();
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
					resetForm();
                } else {
                    $("#error-saving-manualentries").removeClass("hidden");
                }
				
			}
        });
    });
});

/*
 * Function to add row
 */
function addRow(clickedRowId) {
	action = "ADD";
    if (!isValidRow(clickedRowId)) {
        return false;
    }
    noOfRows++;
    rowIdsArray.push(noOfRows);
    var lastRow = $('#row' + clickedRowId + '').attr("islastRow");
    if(lastRow) {//if clicked row was last row then make the clicked row as not last row
    	$('#row' + clickedRowId + '').attr("islastRow","false");//make the previous row as not last row
    }
    var htmlTableRow = '<tr id="row' + noOfRows + '" class="tblRow" islastRow="'+lastRow+'">' +
            '<td><select id="issueCode' + noOfRows + '" name="issueCode">' +
            '<option value="">Issue Code</option>' +
            '</select></td>' +
            '<td><select class="pp-width-full" id="company' + noOfRows + '" name="company" onchange="loadAccounts(this.value, $(this).closest(\'tr\').attr(\'id\'));">' +
            '<option value="">Company</option>' +
            '</select></td>' +
            '<td><select id="accountNumber' + noOfRows + '" name="accountNumber">' +
            '<option value="">Account No.</option>' +
            '</select></td>' +
            '<td><input type="text" id="checkNumber' + noOfRows + '" name="checkNumber"' +
            'size="12" placeholder="Check Number"></td>' +
            '<td><input type="text" id="checkAmount' + noOfRows + '" name="checkAmount"' +
            'size="8" placeholder="Amount" onblur="this.value = toDecimal(this.value);"></td>' +
            '<td><div id="calendarBtn' + noOfRows + '" class="date-input">' +
            '<input type="text" id="issueDate' + noOfRows + '" name="issueDate" size="10" placeholder="Issue Date"></div></td>' +
            '<td><input type="text" id="payee' + noOfRows + '" name="payee"' +
            'size="12" placeholder="Payee Name"></td>' +
            '<td><div style="width: 75px; float: left;"><div style="float: left; margin-right: 4px;"><a href="#" id="copy-button' + noOfRows + '" onclick="copyRow(' + noOfRows + ');"><img alt="Copy" src="' + globalBaseURL + '/static/positivepay/images/icons/copy-sprite.png" /></a></div>' +
            '<div style="float: left; margin-right: 4px;"><a href="#" id="delete-button' + noOfRows + '" data-id=' + noOfRows + ' data-toggle="modal" onclick="showDeleteConfirmationDialogue(' + noOfRows + ');"><img alt="Delete" src="' + globalBaseURL + '/static/positivepay/images/icons/delete-sprite.png" /></a></div>' +
            '<div style="float: left;"><a href="#" id="add-button' + noOfRows + '" onclick="addRow(' + noOfRows + ');"><img alt="Add" src="' + globalBaseURL + '/static/positivepay/images/icons/add-sprite.png"/></a></div></div></td>' +
            '</tr>';
    //Add HTML dynamically
    $('#row' + clickedRowId + '').after(htmlTableRow);
    if(lastRow == "false") {//if clicked row was not last row then hide add button on new added row
    	$('#add-button' + noOfRows + '').hide();
    }
    
	$("#issueCode" + noOfRows + "").change(function(){
		var text = $('option:selected', $(this)).text();
		var index = text.indexOf("-");
		if(index > -1)
		{ 
		  text = text.substring(index+1, text.length);
		  text = text + " Date";
		}
		
		$("#issueDate" + noOfRows + "").attr('placeholder',text);
    });
    
    //Get the customers in the options
    loadCompanyOptions(noOfRows);
    //load the issue codes
    loadCodeOptions(noOfRows);
    //Add date picker to the date field.
    $("#issueDate" + noOfRows + "").datepicker({
        showAnim: "clip"
    });
	$("#calendarBtn" + noOfRows + "").click(function(e) {
		e.preventDefault();
		$("#issueDate" + noOfRows + "").datepicker().focus();
	});
    //Hide the add button in the previous row
    $('#add-button' + clickedRowId + '').hide();

    //Show the delete button on the first row when there is more than one row
    if (rowIdsArray.length > 1) {
        $('#delete-button' + rowIdsArray[0] + '').show();
    } else {
        $('#delete-button' + rowIdsArray[0] + '').hide();
    }
    //Notification for Row Added
    $("#row" + noOfRows + "").notify(
        "Row Added!",
        {
            elementPosition: "bottom center",
            className: 'success'
        }
    );
    return true;
}

/*
 * function to copy row
 */
function copyRow(clickedRowId) {
	copiedFromRowId = clickedRowId;
    //add the row first
    var rowAdded = addRow(clickedRowId);
    if (rowAdded) {
		action = "COPY";

		//And now copy values as well using set timeout so that it gives a chance for loading the customer options
		$("#company" + noOfRows + "").val($("#company" + clickedRowId + "").val());
		$("#issueCode" + noOfRows + "").val($("#issueCode" + clickedRowId + "").val());
        $("#issueDate" + noOfRows + "").val($("#issueDate" + clickedRowId + "").val());	
		$("#issueCode" + noOfRows + "").change(function(){
			var text = $('option:selected', $(this)).text();
			var index = text.indexOf("-");
			if(index > -1) {
				text = text.substring(index+1, text.length);
				text = text + " Date";
			}
		
			$("#issueDate" + noOfRows + "").attr('placeholder',text);
    });
        //load Account Numbers
        var companyid = $('#company' + clickedRowId + '').val();
        loadAccounts(companyid, "row" + noOfRows);
        //Notification for Row copied
        //$("#row"+noOfRows+"").notify("Copied", { elementPosition: "bottom center", className: 'success'});
        $("#row" + noOfRows + "").notify(
            "Row Copied!",
            {
                elementPosition: "bottom center",
                className: 'success'
            }
        );
    }
}

/*
 * function to delete row
 */
function deleteRow(rowId) {
    var index = rowIdsArray.indexOf(rowId);
    if (index > -1) {
        rowIdsArray.splice(index, 1);
    }
    var tr = $("#row" + rowId + "");
    tr.remove();

    //Make last tr as last row
    $("#manualEntryTbl tbody tr:last").attr("islastRow", "true");
    var lastRowAttrId = $("#manualEntryTbl tbody tr:last" ).attr("id");
    var newlastRowId = lastRowAttrId.substring(3); //delete first three characters i.e row
    var numOfRows = $("#manualEntryTbl tbody tr").length;
    //Add the add button
    $('#add-button' + newlastRowId + '').show();
    if (numOfRows == 1) {
        //if this is the only row remaining after delete, hide the delete button
        $('#delete-button' + newlastRowId + '').hide();
    }
   
    //Notification for Row deleted
    //$(".panel-title").notify("Entry was successfully deleted.", "success");
    $(".panel-title").notify(
        "Row " + (index + 1) + " was deleted successfully.",
        {
            arrowShow: false,
            className: 'success',
            elementPosition: "bottom center"
        }
    );
}

/*
 * function to check if a row is valid
 */
function isValidRow(rowId) {
    var checkDetail = {
        "company": $("#company" + rowId + "").val(),
        "accountNumber": $("#accountNumber" + rowId + "").val(),
        "checkNumber": $("#checkNumber" + rowId + "").val(),
        "issuedAmount": $("#checkAmount" + rowId + "").val(),
        "issueCode": $("#issueCode" + rowId + "").val(),
        "issueDate": $("#issueDate" + rowId + "").val(),
        "payee": $("#payee" + rowId + "").val()
    };
    //perform validation
    var errors = new Array();
    //Company Validation
    if (!checkDetail.company) {
        var errorMessage = "Please select the company";
        $("#company" + rowId + "").addClass('highlight');
        $("#company" + rowId + "").focus();
        errors.push(errorMessage);
    } else {
        $("#company" + rowId + "").removeClass('highlight');
    }
    //Account Number Validation
    if (!checkDetail.accountNumber) {
        var errorMessage = "Please select the account number.";
        $("#accountNumber" + rowId + "").addClass('highlight');
        $("#accountNumber" + rowId + "").focus();
        errors.push(errorMessage);
    } else {
        if (!isDigits(checkDetail.accountNumber)) {
            var errorMessage = "Account number should only contain digits.";
            $("#accountNumber" + rowId + "").addClass('highlight');
            $("#accountNumber" + rowId + "").focus();
            errors.push(errorMessage);
        } else {
            $("#accountNumber" + rowId + "").removeClass('highlight');
        }
    }
    //Issue Code Validation
    if (!checkDetail.issueCode) {
        var errorMessage = "Please select the issue code.";
        $("#issueCode" + rowId + "").addClass('highlight');
        $("#issueCode" + rowId + "").focus();
        errors.push(errorMessage);
    } else {
        $("#issueCode" + rowId + "").removeClass('highlight');
    }
    //Check Number Validation
    if (!checkDetail.checkNumber) {
        var errorMessage = "Check number is required.";
        $("#checkNumber" + rowId + "").addClass('highlight');
        $("#checkNumber" + rowId + "").focus();
        errors.push(errorMessage);
    } else {
        if (!isDigits(checkDetail.checkNumber)) {
            var errorMessage = "Check number should only contain digits.";
            $("#checkNumber" + rowId + "").addClass('highlight');
            $("#checkNumber" + rowId + "").focus();
            errors.push(errorMessage);
        } else {
            $("#checkNumber" + rowId + "").removeClass('highlight');
        }
    }
    //Amount Validation
    if (!checkDetail.issuedAmount) {
        var errorMessage = "Amount is required.";
        $("#checkAmount" + rowId + "").addClass('highlight');
        $("#checkAmount" + rowId + "").focus();
        errors.push(errorMessage);
    } else {
        if (!isPositiveAmount(checkDetail.issuedAmount)) {
            var errorMessage = "Check amount is not valid. It should only be a positive decimal number.";
            $("#checkAmount" + rowId + "").addClass('highlight');
            $("#checkAmount" + rowId + "").focus();
            errors.push(errorMessage);
        } else {
            $("#checkAmount" + rowId + "").removeClass('highlight');
        }
    }
    //Issue Date Validation
    if (!checkDetail.issueDate) {
        var errorMessage = "Issue Date is required.";
        $("#issueDate" + rowId + "").addClass('highlight');
        //$("#issueDate"+rowId+"").focus();
        errors.push(errorMessage);
    } else {
        if (!isValidDate(checkDetail.issueDate)) {
            var errorMessage = "Issue date should be in mm/dd/yyyy format only.";
            $("#issueDate" + rowId + "").addClass('highlight');
            $("#issueDate" + rowId + "").focus();
            errors.push(errorMessage);
        } else {
            $("#issueDate" + rowId + "").removeClass('highlight');
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
        //Hide duplicateChecksErrorBox if present
        if ($("#duplicateChecksErrorBox").hasClass("show")) {
            $("#duplicateChecksErrorBox").removeClass('show').addClass('hidden');
        }
        //errors found, show the error box
        if ($("#errorBox").hasClass('hidden')) {
            $("#errorBox").removeClass('hidden').addClass('show');
        }
        //Add all errors to the errors div
        for (var i = 0; i < errors.length; i++) {
            $("#errors").append($("<li style='display: list-item;'></li>").html(errors[i]));
        }
        return false;
    } else {
        //No error, validation pass, so hide error box
        $("#errorBox").addClass('hidden');
        $("#duplicateChecksErrorBox").addClass('hidden');
        //Empty error Message
        $("#errors").empty();
        return true;
    }
}

/*
 * Function to check if the form is valid
 * This will validate each row and the moment a row is found which is not valid, will return false else true
 */
function isManualEntryFormValid() {
    $(".form-control").removeClass("highlight");
    for (var i = 0; i < rowIdsArray.length; i++) {
        if (!isValidRow(rowIdsArray[i])) {
            return false;
        }
    }
    return true;
}

/*
 * This function will show the delete confirmation dialogue
 */
function showDeleteConfirmationDialogue(rowId) {
    var id = $("#delete-button" + rowId + "").data('id');
    $('#deleteConfirmModal').data('id', id).modal('show');
}

/*
 * Upon confirmation, this will delete the particular row. 
 */
function onConfirmDelete() {
    var id = $('#deleteConfirmModal').data('id');
    deleteRow(id);
    $('#deleteConfirmModal').modal('hide');
}

/*
 * This method will load accounts for a given company if and rowid
 */
function loadAccounts(companyId, rowId)
{
    var queryString = "companyId=" + companyId;
    clearAccountNumbersDD(rowId);
    if (companyId !== "") {
        $.ajax({
            type: "GET",
            url: "manualentry/accounts",
            data: queryString,
            dataType: "json",
            beforeSend: function() {
                $.blockUI({
                    message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif"/>Fetching account numbers...</div>',
                    blockMsgClass: 'alert',
                    css: { padding: '10px', color: '#000', border: '1px solid #006B87', 'border-radius': '5px', '-moz-border-radius': '5px', '-webkit-border-radius': '5px' }
                });
            },
            complete: function() {
            	$.unblockUI();
            },
            success: function(accountsListJson) {
                $.each(accountsListJson, function(index, value) {
                    $('#' + rowId + '').find("select[name='accountNumber']").append($('<option>', {
                        value: value,
                        text: value
                    }));
                });
                //Check if action=COPY, then set the value to previously selected account number
                if(action == "COPY") {
                	$('#' + rowId + '').find("select[name='accountNumber']").val($("#accountNumber" + copiedFromRowId + "").val());
                }
                //Notify
                var notifyOptions = {
                    autoHide: false,
                };
                if (accountsListJson.length > 0) {
                    $('#' + rowId + '').find("select[name='accountNumber']").notify("Account numbers populated.", "success", notifyOptions).focus();
                } else {
                    $('#' + rowId + '').find("select[name='accountNumber']").notify("No account numbers found.", "info", notifyOptions).focus();
                }
            },
            error: function() {
                $('#' + rowId + '').find("select[name='accountNumber']").notify("Error populating account numbers.", "error");
            }
        });
    }
}

/*
 * This method loads Customer Options in the dropdown
 * First time it will make an ajax request to get the company list and store it locally
 * Next time onwards, the local company list will be refertenced.
 */
function loadCompanyOptions(rowId) {
    if (companies.length > 0) {
        //Do not make ajax request, but fetch it from locally stored value
        for (var i = 0; i < companies.length; i++)
		{
            $('#company' + rowId + '').append($("<option value='" + companies[i].id + "'>" + companies[i].name + "</option>"));
        }
    } else {
        //Companies not avalable locally so Make an ajax request
        $.ajax({
            type: "GET",
            url: "manualentry/companies",
            dataType: "json",
            success: function(companiesJson) {
				sortByName(companiesJson,"name");
                for (var i = 0; i < companiesJson.length; i++) {
                    var companyid = companiesJson[i].id;
                    var companyname = companiesJson[i].name;
                    var company = {
                        id: companyid,
                        name: companyname
                    };
                    companies.push(company);
                    $('#company' + rowId + '').append($("<option value='" + companyid + "'>" + companyname + "</option>"));
                }
            }
        });
    }
}

function loadCodeOptions(rowId) {
    if (issueCodes.length > 0) {
        //Do not make ajax request, but fetch it from locally stored value
        for (var i = 0; i < issueCodes.length; i++) {
            $('#issueCode' + rowId + '').append($("<option value='" + issueCodes[i].itemCode + "'>" + issueCodes[i].itemCode + " - " + issueCodes[i].name + "</option>"));
        }
    } else {
        //Companies not avalable locally so Make an ajax request
        $.ajax({
            type: "GET",
            url: "manualentry/issuecodes",
            dataType: "json",
            success: function(issueCodesJson) {
                for (var i = 0; i < issueCodesJson.length; i++) {
                    var id = issueCodesJson[i].id;
                    var name = issueCodesJson[i].name;
                    var itemCode = issueCodesJson[i].itemCode;
                    var issueCode = {
                        id: id,
                        name: name,
                        itemCode: itemCode
                    };
                    issueCodes.push(issueCode);
                    $('#issueCode' + rowId + '').append($("<option value='" + itemCode + "'>" + itemCode + " - " + name + "</option>"));
                }
            }
        });
    }
}

/*
 *this method clears all the present options in the Account Numbers Drop Down
 */
function clearAccountNumbersDD(rowId)
{
    var optionid = "";
    var optionValue = "Account No.";
    $('#' + rowId + '').find("select[name='accountNumber']").find('option').remove();
    $('#' + rowId + '').find("select[name='accountNumber']").append($("<option value='" + optionid + "'>" + optionValue + "</option>"));
}

function containsDuplicateRow(checkDetailsArray, checkDetail, rowId) {
    for (var i = 0; i < checkDetailsArray.length; i++) {
        //Remove previous highlights
        $("#accountNumber" + rowId + "").removeClass('highlight');
        $("#checkNumber" + rowId + "").removeClass('highlight');
        $("#checkNumber" + (i + 1) + "").removeClass('highlight');
        $("#accountNumber" + (i + 1) + "").removeClass('highlight');
        if (checkDetailsArray[i].checkNumber == checkDetail.checkNumber &&
            checkDetailsArray[i].accountNumber == checkDetail.accountNumber) {
            $("#duplicateChecksErrorBox").removeClass('hidden').addClass('show');
            $("#duplicateChecksErrorBox").empty();
            $("#duplicateChecksErrorBox").html("<strong>Error!</strong> The highlighted entries seem to be duplicates. Please correct and resubmit.");
            //Highlight
            $("#accountNumber" + rowId + "").addClass('highlight');
            $("#accountNumber" + (i + 1) + "").addClass('highlight');
            $("#checkNumber" + rowId + "").addClass('highlight');
            $("#checkNumber" + (i + 1) + "").addClass('highlight');
            return true;
        }
    }
    return false;
}

function resetForm(){
	//Delete all table rows except first
	$("table").find("tr:gt(1)").remove();
	//reset form values
	$('#checkEntryForm')[0].reset();
}