$(document).ready(function() {
    $("#emulateBtn").click(function() {
        //validate form before submitting
        var bankName = $("#bankName").val();
        var companyName = $("#companyName").val();
        var userName = $("#userName").val();
        var formValid = true;
        if (!bankName) {
            formValid = false;
            $("#bankName").notify("Please select a bank", "error");
            $("#bankName").addClass('highlight');
        } else {
        	$("#bankName").removeClass('highlight');
        }
        if (!companyName) {
            formValid = false;
            $("#companyName").notify("Please select a company", "error");
            $("#companyName").addClass('highlight');
        } else {
        	$("#companyName").removeClass('highlight');
        }
        if (!userName) {
            formValid = false;
            $("#userName").notify("Please select a user", "error");
            $("#userName").addClass('highlight');
        } else {
        	$("#userName").removeClass('highlight');
        }
        if (formValid) {
            $("#customerEmulationForm").submit();
        }
    });
});

/*
 * This method will load accounts for a given bank id
 */
function loadCompaniesByBankId(bankId)
{
    var queryString = "bankId=" + bankId;
    clearCompanyDD();
    clearUserDD();
    if (bankId !== "") {
        $.ajax({
            type: "GET",
            url: "emulation/companies",
            data: queryString,
            dataType: "json",
            beforeSend: function() {
                $.blockUI({
                    message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif"/>Loading companies for the selected bank...</div>',
                    blockMsgClass: 'alert',
                    css: { padding: '10px', color: '#000', border: '1px solid #006B87', 'border-radius': '5px', '-moz-border-radius': '5px', '-webkit-border-radius': '5px'}
                });
            },
            complete: function() {
            	$.unblockUI();
            },
            success: function(companyListJson) {
            	sortByName(companyListJson,"name");
                if (companyListJson.length > 0) {
                    $('#companyName').find('option').remove();
                    $('#companyName').append($("<option value=''>Now select a company...</option>"));
                    $.each(companyListJson, function(index, value) {
                        $('#companyName').append($('<option>', {
                            value: value.id,
                            text: value.name
                        }));
                    });
                    $('#companyName').notify("Companies populated.", "success");
                } else {
                	$('#companyName').notify("No company found.", "warn");
                }
            },
            error: function() {
                $('#companyName').notify("Error Populating Companies.", "error");
            }
        });
    }
}

/*
 * This method will load users for a given company id
 */
function loadUsersByCompanyId(companyId) {
    var queryString = "companyId=" + companyId;
    clearUserDD();
    if (companyId !== "") {
        $.ajax({
            type: "GET",
            url: "emulation/users",
            data: queryString,
            dataType: "json",
            beforeSend: function() {
                $.blockUI({
                    message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif"/>Loading users for the selected company...</div>',
                    blockMsgClass: 'alert',
                    css: {padding: '10px', color: '#000', border: '1px solid #006B87', 'border-radius': '5px', '-moz-border-radius': '5px', '-webkit-border-radius': '5px'}
                });
            },
            complete: function() {
            	$.unblockUI();
            },
            success: function(userListJson) {
            	sortByName(userListJson,"name");
                if (userListJson.length > 0) {
                    $('#userName').find('option').remove();
                    $('#userName').append($("<option value=''>Now select a user...</option>"));
                    $.each(userListJson, function(index, value) {
                        $('#userName').append($('<option>', {
                            value: value.userName,
                            text: value.userName
                        }));
                    });
                    $('#userName').notify("Users populated.", "success");
                } else {
                	$('#userName').notify("No user found", "warn");
                }
            },
            error: function() {
            	$('#userName').notify("Error Populating Users.", "error");
            }
        });
    }
}

/*
 *this method clears all the present options in the company Drop Down
 */
function clearCompanyDD() {
    var optionid = "";
    var optionValue = "Select a bank first...";
    $('#companyName').find('option').remove();
    $('#companyName').append($("<option value='" + optionid + "'>" + optionValue + "</option>"));
}

/*
 *this method clears all the present options in the User Drop down
 */
function clearUserDD() {
    var optionid = "";
    var optionValue = "Select a company first...";
    $('#userName').find('option').remove();
    $('#userName').append($("<option value='" + optionid + "'>" + optionValue + "</option>"));
}