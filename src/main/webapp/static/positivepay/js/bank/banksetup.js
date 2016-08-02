$(document).ready(function() {
    var us_states = {AL: 'Alabama', AK: 'Alaska', AZ: 'Arizona', AR: 'Arkansas', CA: 'California', CO: 'Colorado', CT: 'Connecticut', DE: 'Delaware', DC: 'District of Columbia', FL: 'Florida', GA: 'Georgia', HI: 'Hawaii', ID: 'Idaho', IL: 'Illinois', IN: 'Indiana', IA: 'Iowa', KS: 'Kansas', KY: 'Kentucky', LA: 'Louisiana', ME: 'Maine', MD: 'Maryland', MA: 'Massachusetts', MI: 'Michigan', MN: 'Minnesota', MS: 'Mississippi', MO: 'Missouri', MT: 'Montana', NE: 'Nebraska', NV: 'Nevada', NH: 'New Hampshire', NJ: 'New Jersey', NM: 'New Mexico', NY: 'New York', NC: 'North Carolina', ND: 'North Dakota', OH: 'Ohio', OK: 'Oklahoma', OR: 'Oregon', PA: 'Pennsylvania', RI: 'Rhode Island', SC: 'South Carolina', SD: 'South Dakota', TN: 'Tennessee', TX: 'Texas', UT: 'Utah', VT: 'Vermont', VA: 'Virginia', WA: 'Washington', WV: 'West Virginia', WI: 'Wisconsin', WY: 'Wyoming'};

    //Populate the state drop down
    $.each(us_states, function(key, value) {
        $('#state').append($('<option>').text(value).attr('value', key));
    });
    $('#state').val($('#state').attr('value'));

    $('input[name="redirectUrl"]').val("");//Make redirect link Empty on load of page


    $('#banksetup-form').submit(function(event) {
        if (!validateBankSetupForm()) {
            $('input[name="redirectUrl"]').val("");//Make redirect link Empty
            event.preventDefault();
        }
    });

    $("#banksetup-form :input").change(function() {
        //$("#banksetup-form").data('changed', true);
    	if($(this).attr('id') != 'showArchievedCompanies')
    		$('input[name="formDataChanged"]').val('true');
    });
    
    $("#showArchievedCompanies").click(function() {
    	var showArchives = $(this).is(":checked");
    	$("tr[active='false']").each(function() {
    		if(showArchives)
    			$(this).fadeIn();
    		else
    			$(this).fadeOut();
    	});
    });

    $('a#addCompany,a#editCompany').click(function(event) {
        var anchor = this;
        var redirectLink = $(anchor).attr('href1');

        //if($("#banksetup-form").data('changed')) {
        if ($('input[name="formDataChanged"]').val()) {

            $('#saveBankDataModal').modal('show');

            $('button#discardBankChangesAndNavigate').click(function(event) {
                window.location.href = redirectLink;
            });

            $('button#saveBankChangesAndNavigate').click(function(event) {
                $('input[name="redirectUrl"]').val(redirectLink);
                $('#saveBankDataModal').modal('hide');
                //saveBankDataAndNavigate(redirectLink);
                $('#banksetup-form').submit();
            });
        } else {
            window.location.href = redirectLink;
        }
        event.preventDefault();
    });

    $('a#deleteCompanyAnchor').click(function(event) {

    	//Empty error Message
        $("#errors").empty();
    	if ($("#errorBox").hasClass("show")) {
            $("#errorBox").removeClass('show').addClass('hidden');
        }
        
        var anchor = this;
        var companyId = $(anchor).attr('companyId');
        var bankId = $(anchor).attr('bankId');
        var requestData = {"id": companyId, "bankId": bankId};
        $('#deleteCompanyModal').modal('show');
        $('button#confirmDeleteCompany').click(function(event) {
            $('#deleteCompanyModal').modal('hide');
            $.ajax({
                url: 'banksetup/delete/company',
                type: "POST",
                data: JSON.stringify(requestData),
                contentType: 'application/json; charset=utf-8',
                datatype: "json",
                traditional: true,
                success: function(result) {
                    $(anchor).css({ 'display': "none" });
                    $('#td_active_'+companyId).text('Inactive');
                    $('#tr_row_'+companyId).attr("active",false);
                    if($("#showArchievedCompanies").is(":checked") == false)
                    	$('#tr_row_'+companyId).fadeOut();
                },
                error: function(response) {
                    $("#errors").empty();//Empty previous errors
                    $("#errors").append($("<li></li>").html('Problem occurred, company not deleted.'));
                    if ($("#errorBox").hasClass('hidden')) {
                        $("#errorBox").removeClass('hidden').addClass('show');
                    }
                },
                async: false
            });
            event.preventDefault();
        });
        return false;
    });

});

/**
 * Function to display bank logo
 */
function displayBankLogo(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            $('#banklogoImage').attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
        var uploadedFilename = $('#bankLogo').val().split('/').pop().split('\\').pop();
        $('#logoUploadInput').val(uploadedFilename);
    }
}


/*
 * Save Bank Data and Navigate
 */
/*function saveBankDataAndNavigate(redirectLink) {
 var newActionUrl=$('#banksetup-form').attr('action')+"?redirectUrl="+redirectLink;
 $("#search-form").attr("action",newActionUrl);
 $('#banksetup-form').submit();
 }*/
function saveBankDataAndNavigate(redirectLink) {
    var formData = $('#banksetup-form').serialize();
    var actionUrl = $('#banksetup-form').attr('action');
    $.ajax({
        type: 'post',
        url: actionUrl,
        data: formData,
        success: function(result) {
            window.location.href = redirectLink;
        },
        error: function(response) {
            $("#errors").empty();//Empty previous errors
            $("#errors").append($("<li></li>").html('Problem occured, bank not saved.'));
            if ($("#errorBox").hasClass('hidden')) {
                $("#errorBox").removeClass('hidden').addClass('show');
            }
        }
    });
}


function validateBankSetupForm() {

    var errors = new Array();

    if (!$("#bankName").val()) {
        $("#bankName").addClass('highlight');
        errors.push("Bank name is required.");
    } else {
        $("#bankName").removeClass('highlight');
    }

    var bankId = $("#bankId").val();
    if (bankId) {
        if (!$.isNumeric(bankId)) {
            $("#bankId").addClass('highlight');
            errors.push("Please enter numeric value for Bank Id.");
        } else {
            $("#bankId").removeClass('highlight');
        }
    } else {
        $("#bankId").removeClass('highlight');
    }

    if (!$("#streetAddress").val()) {
        $("#streetAddress").addClass('highlight');
        errors.push("streetAddress is required.");
    } else {
        $("#streetAddress").removeClass('highlight');
    }

    if (!$("#city").val()) {
        $("#city").addClass('highlight');
        errors.push("City is required.");
    } else {
        $("#city").removeClass('highlight');
    }

    if (!$("#state").val()) {
        $("#state").addClass('highlight');
        errors.push("State is required.");
    } else {
        $("#state").removeClass('highlight');
    }

    if (!$("#zipCode").val()) {
        $("#zipCode").addClass('highlight');
        errors.push("zipCode is required.");
    } else {
        $("#zipCode").removeClass('highlight');
    }


    //check if errors
    if (errors.length > 0) {
        //empty previous errors if any
        $("#errors").empty();
        //Hide successBox if present
        if ($("#serverSuccessBox").hasClass("show")) {
            $("#serverSuccessBox").removeClass('show').addClass('hidden');
        }
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
    	if ($("#errorBox").hasClass("show")) {
            $("#errorBox").removeClass('show').addClass('hidden');
        }
        //Empty error Message
        $("#errors").empty();
        return true;
    }
}

