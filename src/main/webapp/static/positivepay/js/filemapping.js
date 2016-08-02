$(document).ready(function() {
    actionName = "CREATE";
    notificationOptions = {
        arrowShow: false,
        //elementPosition: 'top right',
        className: 'success',
        style: 'bootstrap'
    };
    $('#saveFileMappingBtn').click(function(e) {
        //Check if the action is not delete and fileMappingName is selected means actionName = Update
        if (actionName !== "DELETE" && $("#fileMappingName").val()) {
            actionName = "UPDATE";
        }
        fileMapping = {
            "accountNumberPosition": "",
            "routingNumberPosition": "",
            "checkNumberPosition": "",
            "issueCodePosition": "",
            "issueDatePosition": "",
            "checkAmountPosition": "",
            "payeePosition": "",
            "fileType": "",
            "fileMappingName": ""
        };
        var positions = new Array();
        fileMapping.fileType = $("#fileType").val();
        if (actionName == "CREATE") {
            fileMapping.fileMappingName = $("#newMapping").val();
        } else if (actionName == "UPDATE" || actionName == "DELETE") {
            fileMapping.fileMappingName = $("#fileMappingName option:selected").text();
            //Set ID too
            fileMapping.id = $("#fileMappingName").val();
        }
        if (fileMapping.fileType == "CSV" || fileMapping.fileType == "TXT_DELIMITER") {
            if (fileMapping.fileType == "TXT_DELIMITER") {
                fileMapping.delimiter = {};
                fileMapping.delimiter.id = $("#delimiterType").val();
                fileMapping.delimiter.name = $("#delimiterType").text();
            }
            var position1 = $("#position1 option:selected").val();
            var position2 = $("#position2 option:selected").val();
            var position3 = $("#position3 option:selected").val();
            var position4 = $("#position4 option:selected").val();
            var position5 = $("#position5 option:selected").val();
            var position6 = $("#position6 option:selected").val();
            var position7 = $("#position7 option:selected").val();
            positions.push(position1);
            positions.push(position2);
            positions.push(position3);
            positions.push(position4);
            positions.push(position5);
            positions.push(position6);
            positions.push(position7);
            var allButPayeeSelected = false;
            if ($.inArray('ACCOUNT_NUMBER', positions) != -1 &&
                    $.inArray('ROUTING_NUMBER', positions) != -1 &&
                    $.inArray('CHECK_NUMBER', positions) != -1 &&
                    $.inArray('ISSUE_CODE', positions) != -1 &&
                    $.inArray('ISSUE_DATE', positions) != -1 &&
                    $.inArray('CHECK_AMOUNT', positions) != -1) {
                if ($.inArray('PAYEE', positions) == -1) {
                    allButPayeeSelected = true;
                }
            }
            for (var i = 0; i < positions.length; i++) {
                //Validation
                //First validate if any field is not selected except the 7th
                var selectedValue = $("#position" + [i + 1] + " option:selected").val();
                if (selectedValue == "") {
                    if (!allButPayeeSelected) {
                        $("#position" + [i + 1] + "").notify("Please select an appropriate value.", {position: "right"});
                        return;
                    }
                }
                //Now validated if there are duplicates.
                if (isDuplicate("position" + (i + 1))) {
                    return;
                }
                switch (positions[i]) {
                    case "ACCOUNT_NUMBER":
                        fileMapping.accountNumberPosition = (i + 1);
                        break;
                    case "ROUTING_NUMBER":
                        fileMapping.routingNumberPosition = (i + 1);
                        break;
                    case "CHECK_NUMBER":
                        fileMapping.checkNumberPosition = (i + 1);
                        break;
                    case "ISSUE_CODE":
                        fileMapping.issueCodePosition = (i + 1);
                        break;
                    case "ISSUE_DATE":
                        fileMapping.issueDatePosition = (i + 1);
                        break;
                    case "CHECK_AMOUNT":
                        fileMapping.checkAmountPosition = (i + 1);
                        break;
                    case "PAYEE":
                        fileMapping.payeePosition = (i + 1);
                        break;
                    default:
                        break;
                }
            }
        } else {
            if (!isValidFixedFormat()) {
                return;
            }
            fileMapping.accountNumberPosition = $("#accountNumberStartPos").val() + "-" + $("#accountNumberEndPos").val();
            fileMapping.routingNumberPosition = $("#routingNumberStartPos").val() + "-" + $("#routingNumberEndPos").val();
            fileMapping.checkNumberPosition = $("#checkNumberStartPos").val() + "-" + $("#checkNumberEndPos").val();
            fileMapping.issueCodePosition = $("#issueCodeStartPos").val() + "-" + $("#issueCodeEndPos").val();
            fileMapping.issueDatePosition = $("#issueDateStartPos").val() + "-" + $("#issueDateEndPos").val();
            fileMapping.checkAmountPosition = $("#checkAmountStartPos").val() + "-" + $("#checkAmountEndPos").val();
            fileMapping.payeePosition = $("#payeeStartPos").val() + "-" + $("#payeeEndPos").val();
        }

        var type = "POST";
        if (actionName == "DELETE")
            type = "DELETE";
        $.ajax({
            type: type,
            url: "filemapping",
            data: JSON.stringify(fileMapping),
            dataType: "json",
            contentType: 'application/json',
            beforeSend: function() {
                $.blockUI({message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif"/>Saving file mapping, please wait...</div>'});
            },
            complete: function() {
                $.unblockUI();
            },
            success: function(jsonData) {
                //Show success notification
                if (actionName == "CREATE") {
                    $(".panel-title").notify("File mapping was created successfully.", notificationOptions);
                    //If dropdown list doesn't exist because this is the first create
                    if ($("#fileMappingName").length <= 0) {
                        var selectfileMappingNameHTML = '<div class="form-group"><label for="orText">Or </label></div>' +
                                '<div class="form-group"><select id="fileMappingName" name="fileMappingName"' +
                                'onchange="displayFileMapping(this.value);" class="form-control">' +
                                '<option value="">Select to Update:</option>' +
                                '</select>' +
                                '</div>';
                        $("#createOrUpdate").append(selectfileMappingNameHTML);
                        $("#fileMappingNotSet").html("You can <a href='filemanagement'>upload files</a> now.");
                    }
                    // and Add new mapping to option
                    $('#fileMappingName').append($("<option value='" + jsonData.id + "'>" + jsonData.fileMappingName + "</option>"));
                    //Disable Create
                    $('#fileMappingName').prop('disabled', false);
                    $("#createNewMappingBtn").prop('disabled', true);
                    //Enable Update
                    $("#newMapping").prop('disabled', true);
                    //Set selected value
                    $("#fileMappingName").val(jsonData.id);
                    // Show the delete button as well.
                    if ($("#deleteFileMappingBtn").hasClass('hidden')) {
                        $("#deleteFileMappingBtn").removeClass('hidden').addClass('show');
                    }
                    //now the action
                } else if (actionName == "UPDATE") {
                    $(".panel-title").notify("File mapping was updated successfully.", notificationOptions);
                } else if (actionName == "DELETE") {
                    $(".panel-title").notify("File mapping was deleted successfully.", notificationOptions);
                    //Remove from option
                    $("#fileMappingName option[value=" + fileMapping.id + "]").remove();
                    //Reset form and Hide the elements
                    resetForm();
                }
            },
            error: function(req, error, status) {
                if (req.status == '999') {
                    $(".panel-title").notify("Operation Successful. However data was not saved because the user is in emulation mode.", notificationOptions);
                } else {
                    notificationOptions.className = 'error';
                    $(".panel-title").notify("There was a problem saving the file mapping. Please try again later.", notificationOptions);
                }
            }
        });
    });

    $("#deleteFileMappingBtn").click(function(event) {
        actionName = "DELETE";
        $("#saveFileMappingBtn").click();
    });

    $("#createNewMappingBtn").click(function(event) {
        actionName = "CREATE";
        event.preventDefault();
        if (!$("#newMapping").val()) {
            $("#newMapping").notify("Please enter the mapping name", "error");
        }
        else if ($("#fileTypeSelection").hasClass('hidden')) {
            $("#fileTypeSelection").removeClass('hidden').addClass('show');
        }
        $('#fileMappingName').prop('disabled', 'disabled');
    });

    $("#cancelFileMappingBtn").click(function(event) {
        event.preventDefault();
        resetForm();
        $(".panel-title").notify("Last operation was canceled, please start over again.", notificationOptions);
    });

    $("#fileType").change(function() {
        showFormatSelection($(this).val());
    });

    $("#fileMappingName").change(function() {
        //disable the create
        $("#newMapping").prop('disabled', true);
        $("#createNewMappingBtn").prop('disabled', true);
        actionName = "UPDATE";
        var id = $(this).val();
        //Make Ajax request and get the MileMapping object
        $.ajax({
            type: "GET",
            url: "filemapping/" + id,
            dataType: "json",
            beforeSend: function()
            {
                $.blockUI({message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif"/>Please wait...</div>'});
            },
            complete: function()
            {
                $.unblockUI();
            },
            success: function(fileMapping)
            {
                //Show the hidden part accordingly
                showFormatSelection(fileMapping);
            },
            error: function()
            {
                notificationOptions.className = 'error';
                $(".panel-title").notify("A problem was encountered while displaying the selected file mapping. Please try again later.", notificationOptions);
            }
        });
    });

});

function resetForm() {
    $('#fileMappingForm')[0].reset();
    hideAll();
    $('#fileMappingName').prop('disabled', false);
    $("#newMapping").prop('disabled', false);
    $("#createNewMappingBtn").prop('disabled', false);
}

function hideAll() {
    $("#fileTypeSelection").removeClass('show').addClass('hidden');
    $("#txtSetting").removeClass('show').addClass('hidden');
    $("#delimiterSetting").removeClass('show').addClass('hidden');
    $("#csvSetting").removeClass('show').addClass('hidden');
    if ($("#saveFileMappingBtn").hasClass('show')) {
        $("#saveFileMappingBtn").removeClass('show').addClass('hidden');
    }
    if ($("#cancelFileMappingBtn").hasClass('show')) {
        $("#cancelFileMappingBtn").removeClass('show').addClass('hidden');
    }
    if ($("#deleteFileMappingBtn").hasClass('show')) {
        $("#deleteFileMappingBtn").removeClass('show').addClass('hidden');
    }
}
function showCSV() {
    if ($("#txtSetting").hasClass('show')) {
        $("#txtSetting").removeClass('show').addClass('hidden');
    }
    if ($("#delimiterSetting").hasClass('show')) {
        $("#delimiterSetting").removeClass('show').addClass('hidden');
    }
    if ($("#saveFileMappingBtn").hasClass('hidden')) {
        $("#saveFileMappingBtn").removeClass('hidden').addClass('show');
    }
    if ($("#cancelFileMappingBtn").hasClass('hidden')) {
        $("#cancelFileMappingBtn").removeClass('hidden').addClass('show');
    }
    $("#csvSetting").removeClass('hidden').addClass('show');
}

function showFixedWidth() {
    if ($("#csvSetting").hasClass('show')) {
        $("#csvSetting").removeClass('show').addClass('hidden');
    }
    if ($("#delimiterSetting").hasClass('show')) {
        $("#delimiterSetting").removeClass('show').addClass('hidden');
    }
    if ($("#saveFileMappingBtn").hasClass('hidden')) {
        $("#saveFileMappingBtn").removeClass('hidden').addClass('show');
    }
    if ($("#cancelFileMappingBtn").hasClass('hidden')) {
        $("#cancelFileMappingBtn").removeClass('hidden').addClass('show');
    }
    $("#txtSetting").removeClass('hidden').addClass('show');
}

function showFixedWidthWithDelimiter() {
    if ($("#txtSetting").hasClass('show')) {
        $("#txtSetting").removeClass('show').addClass('hidden');
    }
    if ($("#saveFileMappingBtn").hasClass('hidden')) {
        $("#saveFileMappingBtn").removeClass('hidden').addClass('show');
    }
    if ($("#cancelFileMappingBtn").hasClass('hidden')) {
        $("#cancelFileMappingBtn").removeClass('hidden').addClass('show');
    }
    $("#delimiterSetting").removeClass('hidden').addClass('show');
    $("#csvSetting").removeClass('hidden').addClass('show');
}

function showFormatSelection(fileMapping) {
    if (actionName == "UPDATE") {
        if ($("#deleteFileMappingBtn").hasClass('hidden')) {
            $("#deleteFileMappingBtn").removeClass('hidden').addClass('show');
        }
    }

    //set values
    var fileType = "";
    if (actionName == "CREATE") {
        fileType = fileMapping;
    } else {
        fileType = fileMapping.fileType;
    }

    if (fileType == "CSV" || fileType == "TXT_DELIMITER") {
        if (fileType == "TXT_DELIMITER") {
            showFixedWidthWithDelimiter();
            if (actionName !== "CREATE") {
                $("#delimiterType").val(fileMapping.delimiter.id);
            }
        } else {
            showCSV();
        }
        if (actionName !== "CREATE") {
            //FileMapping contains values and they need to be set on the page
            $("#position" + fileMapping.accountNumberPosition + "").val("ACCOUNT_NUMBER");
            $("#position" + fileMapping.routingNumberPosition + "").val("ROUTING_NUMBER");
            $("#position" + fileMapping.checkNumberPosition + "").val("CHECK_NUMBER");
            $("#position" + fileMapping.issueCodePosition + "").val("ISSUE_CODE");
            $("#position" + fileMapping.issueDatePosition + "").val("ISSUE_DATE");
            $("#position" + fileMapping.checkAmountPosition + "").val("CHECK_AMOUNT");
            $("#position" + fileMapping.payeePosition + "").val("PAYEE");
        }
    } else if (fileType == "TXT_FIXED_WIDTH") {
        showFixedWidth();
        if (actionName !== "CREATE") {
            //FileMapping contains values and they need to be set on the page
            $("#accountNumberStartPos").val(getStartPosition(fileMapping.accountNumberPosition));
            $("#accountNumberEndPos").val(getEndPosition(fileMapping.accountNumberPosition));
            $("#routingNumberStartPos").val(getStartPosition(fileMapping.routingNumberPosition));
            $("#routingNumberEndPos").val(getEndPosition(fileMapping.routingNumberPosition));
            $("#checkNumberStartPos").val(getStartPosition(fileMapping.checkNumberPosition));
            $("#checkNumberEndPos").val(getEndPosition(fileMapping.checkNumberPosition));
            $("#issueCodeStartPos").val(getStartPosition(fileMapping.issueCodePosition));
            $("#issueCodeEndPos").val(getEndPosition(fileMapping.issueCodePosition));
            $("#issueDateStartPos").val(getStartPosition(fileMapping.issueDatePosition));
            $("#issueDateEndPos").val(getEndPosition(fileMapping.issueDatePosition));
            $("#checkAmountStartPos").val(getStartPosition(fileMapping.checkAmountPosition));
            $("#checkAmountEndPos").val(getEndPosition(fileMapping.checkAmountPosition));
            $("#payeeStartPos").val(getStartPosition(fileMapping.payeePosition));
            $("#payeeEndPos").val(getEndPosition(fileMapping.payeePosition));
        }
    } else {
        hideAll();
    }
}

function getStartPosition(value) {
    return value.substring(0, value.indexOf("-"));
}

function getEndPosition(value) {
    return value.substring(value.indexOf("-") + 1, value.length);
}

function displayFileMapping(id) {
    //disable the create
    $("#newMapping").prop('disabled', true);
    $("#createNewMappingBtn").prop('disabled', true);
    actionName = "UPDATE";
    //Make Ajax request and get the MileMapping object
    $.ajax({
        type: "GET",
        url: "filemapping/" + id,
        dataType: "json",
        beforeSend: function()
        {
            $.blockUI({message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif"/> Please wait...</div>'});
        },
        complete: function()
        {
            $.unblockUI();
        },
        success: function(fileMapping)
        {
            //Show the hidden part accordingly
            showFormatSelection(fileMapping);
            $("#fileType").val(fileMapping.fileType);
        }
    });
}

function isDuplicate(id) {
    var position1 = $("#position1 option:selected").val();
    var position2 = $("#position2 option:selected").val();
    var position3 = $("#position3 option:selected").val();
    var position4 = $("#position4 option:selected").val();
    var position5 = $("#position5 option:selected").val();
    var position6 = $("#position6 option:selected").val();
    var position7 = $("#position7 option:selected").val();
    var selectedValue = $("#" + id + "").val();
    /*if(selectedValue == "") {
     $("#"+id+"").notify("Please select an appropriate value.", { position:"right" });
     return true;
     }*/
    if (id == "position1") {
        if (selectedValue == position2) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position2").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position3) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position3").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position4) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position4").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position5) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position6) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position7) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
    }

    if (id == "position2") {
        if (selectedValue == position1) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position2").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position3) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position3").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position4) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position4").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position5) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position6) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position7) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
    }

    if (id == "position3") {
        if (selectedValue == position1) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position3").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position2) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position3").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position4) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position4").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position5) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position6) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position7) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
    }

    if (id == "position4") {
        if (selectedValue == position1) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position4").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position2) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position4").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position3) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position4").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position5) {
            $("#position4").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position6) {
            $("#position4").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position7) {
            $("#position4").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
    }
    if (id == "position5") {
        if (selectedValue == position1) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position2) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position3) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position4) {
            $("#position4").notify("Duplicates Identified", {position: "right"});
            $("#position5").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position6) {
            $("#position5").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position7) {
            $("#position5").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
    }

    if (id == "position6") {
        if (selectedValue == position1) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position2) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position3) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position4) {
            $("#position4").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position5) {
            $("#position5").notify("Duplicates Identified", {position: "right"});
            $("#position6").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position7) {
            $("#position6").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
    }

    if (id == "position7") {
        if (selectedValue == position1) {
            $("#position1").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position2) {
            $("#position2").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position3) {
            $("#position3").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position4) {
            $("#position4").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position5) {
            $("#position5").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
        if (selectedValue == position6) {
            $("#position6").notify("Duplicates Identified", {position: "right"});
            $("#position7").notify("Duplicates Identified", {position: "right"});
            return true;
        }
    }
}

function isValidFixedFormat() {
    var accountNumberStartPos = $("#accountNumberStartPos").val();
    var accountNumberEndPos = $("#accountNumberEndPos").val();
    var routingNumberStartPos = $("#routingNumberStartPos").val();
    var routingNumberEndPos = $("#routingNumberEndPos").val();
    var checkNumberStartPos = $("#checkNumberStartPos").val();
    var checkNumberEndPos = $("#checkNumberEndPos").val();
    var issueCodeStartPos = $("#issueCodeStartPos").val();
    var issueCodeEndPos = $("#issueCodeEndPos").val();
    var issueDateStartPos = $("#issueDateStartPos").val();
    var issueDateEndPos = $("#issueDateEndPos").val();
    var checkAmountStartPos = $("#checkAmountStartPos").val();
    var checkAmountEndPos = $("#checkAmountEndPos").val();
    var payeeStartPos = $("#payeeStartPos").val();
    var payeeEndPos = $("#payeeEndPos").val();
    if (!isValidFixedFormatField("accountNumberStartPos", accountNumberStartPos)) {
        return false;
    } else if (!isValidFixedFormatField("accountNumberEndPos", accountNumberEndPos)) {
        return false;
    } else if (!isValidFixedFormatField("routingNumberStartPos", routingNumberStartPos)) {
        return false;
    } else if (!isValidFixedFormatField("routingNumberEndPos", routingNumberEndPos)) {
        return false;
    } else if (!isValidFixedFormatField("checkNumberStartPos", checkNumberStartPos)) {
        return false;
    } else if (!isValidFixedFormatField("checkNumberEndPos", checkNumberEndPos)) {
        return false;
    } else if (!isValidFixedFormatField("issueCodeStartPos", issueCodeStartPos)) {
        return false;
    } else if (!isValidFixedFormatField("issueCodeEndPos", issueCodeEndPos)) {
        return false;
    } else if (!isValidFixedFormatField("issueDateStartPos", issueDateStartPos)) {
        return false;
    } else if (!isValidFixedFormatField("issueDateEndPos", issueDateEndPos)) {
        return false;
    } else if (!isValidFixedFormatField("checkAmountStartPos", checkAmountStartPos)) {
        return false;
    } else if (!isValidFixedFormatField("checkAmountEndPos", checkAmountEndPos)) {
        return false;
    } else if (!isValidFixedFormatField("payeeStartPos", payeeStartPos)) {
        return false;
    } else if (!isValidFixedFormatField("payeeEndPos", payeeEndPos)) {
        return false;
    } else {
        return true;
    }
}

function isValidFixedFormatField(id, value) {
    if (id.indexOf("payee") == -1 && !value) {
        $("#" + id + "").closest('tr').notify("Missing Required Field!", {position: "right"});
        $("#" + id + "").addClass('highlight').focus();
        return false;
    }
    $("#" + id + "").removeClass('highlight');
    if (value && !isDigits(value)) {
        $("#" + id + "").closest('tr').notify("Only digits allowed!", {position: "right"});
        $("#" + id + "").addClass('highlight').focus();
        return false;
    }
    return true;
}