function showErrorItemsPopUp(fileMetaDataId, fileName, uploadDate, companyName){
		$('#errorRecordsModal').modal('show');
		$('#errorRecordsModal > div.modal-dialog').css('margin-top','50px');
		//$('#itemErrorRecords').empty();
		$('#fileName').html("<strong>"+fileName+"</strong>");
	    $('#uploadedDate').html("<strong>" + getFormattedDateWithTimeStamp(uploadDate) + "</strong>");
	    $('#companyName1').html("<strong>" + companyName + "</strong>");
	    $.ajax({
	        url: globalBaseURL+"/user/errorrecords/get/"+fileMetaDataId,
	        type: "GET",
	        datatype: "json",
			beforeSend: function() {
				//Hide previous table
				$("#errorDetails").addClass('hidden');
				$("#ajax-loader-errorDetails").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading records...');
			},
			complete: function() {
				$("#ajax-loader-errorDetails").empty();
			},
	        success: function(itemErrorRecordsList) {
				//check if the recentFilesList list is empty
				if(itemErrorRecordsList.length > 0) {
					$('#errorDetailsTbl > tbody:last').empty();
					$("#noErrorDetails").addClass('hidden');
					//Make table rows by iterating over the payment list from start to end
					for(var i = 0; i < itemErrorRecordsList.length ; i++) {
						$("#errorDetails").removeClass('hidden').addClass('show');
						var tableRow = "<tr id='errorDetails'+(i+1)+''>"+
										'<td style="width: 1.5%;" align="center">'+itemErrorRecordsList[i].fileLineNumber + '</td>'+ 
										'<td>' + itemErrorRecordsList[i].accountNumber + '</td>'+ 
										'<td>' + itemErrorRecordsList[i].routingNumber + '</td>'+
										'<td align="right">' + itemErrorRecordsList[i].checkNumber + '</td>'+ 
										'<td align="center">' + itemErrorRecordsList[i].issueCode + '</td>'+ 
										'<td align="right">' + formatDollarAmount(itemErrorRecordsList[i].issueAmount) + '</td>'+ 
										'<td>' + itemErrorRecordsList[i].issueDate + '</td>'+ 
										'<td>' + formatString(itemErrorRecordsList[i].payee) + '</td>'+ 
										'<td>' + itemErrorRecordsList[i].exceptionTypeName + '</td>'
										"</tr>";
						$('#errorDetailsTbl > tbody:last').append(tableRow);
					}
				} else {
					//No data to display
					$('#errorDetailsTbl > tbody:last').empty();
					$("#noErrorDetails").removeClass('hidden');
				}
	        },
	        error: function(response) {
				$("#errorDetails").addClass('hidden');
				$("#error-loading-errorDetails").removeClass('hidden');
	        }
	    });
}