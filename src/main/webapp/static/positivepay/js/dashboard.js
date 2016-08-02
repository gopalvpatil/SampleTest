$(document).ready(function() {
    companies = new Array();

    var companyNameVar = $("#companyName").val();
    var statusVar = $("#status").val();
    var dateRangeVar = $("#dateRange").val();

    loadCompanyOptions(companyNameVar);
    
    var status = {"Processed": "Processed", "UnProcessed": "Unprocessed"};
    for (var text in status) {
        var val = status[text];
        if(val == statusVar) {
        	$('<option selected="true"/>').val(val).text(text).appendTo($('#statusSearchCriteria'))
        } else {
        	$('<option/>').val(val).text(text).appendTo($('#statusSearchCriteria'))
        }
    };
    
    //Start Set date range Today, Yesterday, Last 7 Days, Last 14 Days, Last 30 Days, All
    var dateRange = {"Today": "0", "Yesterday": "1", "Last 7 Days": "7", "Last 14 Days": "14", " Last 30 Days": "30"};
    for (var text in dateRange) {
        var val = dateRange[text];
        if(val == dateRangeVar) {
        	$('<option selected="true"/>').val(val).text(text).appendTo($('#dateRangeSearchCriteria'))
        } else {
        	$('<option/>').val(val).text(text).appendTo($('#dateRangeSearchCriteria'))
        }
    };

    $('a.errorRecordsLink').click(function(event) {
    	var url = ($(this).attr('href1'));
    	var	fileName = '<strong>"' + ($(this).attr('fileName')) + '"</strong>';
    	var uploadedDate = '<strong>"' + ($(this).attr('uploadedDate')) + '"</strong>';
    	var companyName = '<strong>"' + ($(this).attr('companyName')) + '"</strong>';

	    $.ajax({
	        url: url,
	        type: "GET",
	        datatype: "json",
			beforeSend: function() {
				$("#ajax-loader-errorRecords").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading error records...');
			},
			complete: function() {
				$("#ajax-loader-errorRecords").empty();
			},
	        success: function(itemErrorRecordsList) {
	            $('#itemErrorRecords').empty();
	            $('#fileName').empty();
	            $('#uploadedDate').empty();
	            $('#companyName1').empty();
	            if (itemErrorRecordsList.length > 0) {
	                for (var i = 0; i < itemErrorRecordsList.length; i++) {
	                	var srNum = i+1;
	                    var itemErrorRecordsDto = itemErrorRecordsList[i];
                        var itemErrorRecords = '<tr>'
                                + '<td style="width: 1.5%;" align="center">'
                                + itemErrorRecordsDto.fileLineNumber
                                + '</td>'
                                + '<td>'
                                + itemErrorRecordsDto.accountNumber
                                + '</td>'
                                + '<td>'
                                + itemErrorRecordsDto.routingNumber
                                + '</td>'
                                + '<td align="right">'
                                + itemErrorRecordsDto.checkNumber
                                + '</td>'
                                + '<td align="center">'
                                + itemErrorRecordsDto.issueCode
                                + '</td>'
                                + '<td align="right">'
                                + formatDollarAmount(itemErrorRecordsDto.issueAmount)
                                + '</td>'
                                + '<td>'
                                + itemErrorRecordsDto.issueDate
                                + '</td>'
                                + '<td>'
                                + formatString(itemErrorRecordsDto.payee)
                                + '</td>'
                                + '<td>'
                                + itemErrorRecordsDto.exceptionTypeName
                                + '</td>'
                                + '</tr>';
                        $('#itemErrorRecords').append(itemErrorRecords);
	                }
	            } else {
	            	var itemErrorRecords = '<tr>'
                        + '<td>'
                        + 	'No Records Found'
                        + '</td>'
                        + '</tr>';
                        $('#itemErrorRecords').append(itemErrorRecords);
	            	
	                //$('#itemErrorRecords').append('<div>No Records Found</div>');
	            }
	        },
	        error: function(response) {
	            $('#error1').html('Problem occured, Item error records not retrieved.');
	            $('#error1').show();
	        },
	        async: false
	    });
	    event.preventDefault();
	    
	    $('#fileName').append(fileName);
	    $('#uploadedDate').append(uploadedDate);
	    $('#companyName1').append(companyName);
	  	$('#errorRecordsModal').modal('show');
	  	$('#errorRecordsModal > div.modal-dialog').css('margin-top','50px');
    });
});

/*
 * This method loads Customer Options in the dropdown
 * First time it will make an ajax request to get the company list and store it locally
 * Next time onwards, the local company list will be refertenced.
 */
function loadCompanyOptions(companySelected) {
    if (companies.length > 0) {
        //Do not make ajax request, but fetch it from locally stored value
        for (var i = 0; i < companies.length; i++) {
            $('#companyNameSearchCriteria').append($("<option value='" + companies[i].name + "'>" + companies[i].name + "</option>"));
        }
    } else {
        //Companies not avalable locally so Make an ajax request
        $.ajax({
            type: "GET",
            url: "company",
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
                    //$('#companyNameSearchCriteria').append($("<option value='" + companyname + "'>" + companyname + "</option>"));
                    if(companyname == companySelected) {
                    	$('<option selected="true"/>').val(companyname).text(companyname).appendTo($('#companyNameSearchCriteria'));
                    } else {
                    	$('<option/>').val(companyname).text(companyname).appendTo($('#companyNameSearchCriteria'));
                    }
                }
            }
        });
    }
}
