$(document).ready(function() {
	//Ajax Call to load recent files
	loadRecentFiles();
	
	//Ajax Call to load reports and extracts
	loadReportsAndExtracts();
	
	//Ajax Call to load Account Info
	loadAccountInfo();
});

function loadRecentFiles() {
	$("#error-loading-recentFiles").addClass("hidden");
	$.ajax({
		type: "GET",
		url: "recentfiles",
		dataType: "json",
		beforeSend: function() {
			$("#ajax-loader-recentFiles").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading recently uploded files...');
		},
		complete: function() {
			$("#ajax-loader-recentFiles").empty();
		},
		success: function(recentFilesListJson) {
			populateRecentFilesListTable(recentFilesListJson);
		},
        error: function() {
        	$("#error-loading-recentFiles").removeClass("hidden");
        }
	});
}

function populateRecentFilesListTable(recentFilesList) {
	//check if the recentFilesList list is empty
	if(recentFilesList.length > 0) {
		//Empty table data if any
		$('#recentFilesTbl > tbody:last').empty();
		//Make table rows by iterating over the payment list from start to end
		for(var i = 0; i < recentFilesList.length; i++) {
			$("#recentFiles").removeClass('hidden').addClass('show');
			var tableRow = "<tr id='recentFile'+(i+1)+''>"+
							"<td>"+(i+1)+"</td>"+
							"<td>"+getFormattedDateWithTimeStamp(recentFilesList[i].uploadDate)+"</td>"+
							"<td>"+recentFilesList[i].fileName+"</td>"+
							"<td>"+recentFilesList[i].noOfRecords + "</td>"+
							"<td>"+recentFilesList[i].itemsLoaded + "</td>"+
							"<td><a href='#' onclick='showErrorItemsPopUp(&quot;"+recentFilesList[i].fileMetaDataId+"&quot;, &quot;"+recentFilesList[i].fileName+"&quot;, &quot;"+recentFilesList[i].uploadDate+"&quot;, &quot;"+recentFilesList[i].companyName+"&quot;);'>" + recentFilesList[i].errorRecordsLoaded + "</a></td>";
							//check if action header is present then show download link, otherwise skip
							if($('#actionHeader').length) {
								tableRow = tableRow+"<td><a class='btn' href='file/download?fileUid="+recentFilesList[i].fileUid+"'><i class='icon-download'></i> Download</a></td>";
							}
							tableRow = tableRow+"</tr>";
			$('#recentFilesTbl > tbody:last').append(tableRow);
		}
	} else {
		//No data to display
		$("#noRecentFiles").removeClass('hidden');
	}	
}

function loadAccountInfo() {
	$("#error-loading-accountInfo").addClass("hidden");
	$.ajax({
		type: "GET",
		url: "accountInfo",
		dataType: "json",
		beforeSend: function() {
			$("#ajax-loader-accountInfo").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading account information...');
		},
		complete: function() {
			$("#ajax-loader-accountInfo").empty();
		},
		success: function(accountInfoJson) {
			populateAccountInfoListTable(accountInfoJson);
		},
        error: function() {
        	$("#error-loading-accountInfo").removeClass("hidden");
        }
	});
}

function populateAccountInfoListTable(accountInfo) {
	var accountInfoList = accountInfo.accountInfoDtoList;
	var decisionWindow = accountInfo.decisionWindow;
	//check if the recentFilesList list is empty
	if(accountInfoList.length > 0) {
		//Empty table data if any
		$('#accountInfoTbl > tbody:last').empty();
		//Make table rows by iterating over the payment list from start to end
		for(var i = 0; i < accountInfoList.length; i++) {
			$("#accountInfo").removeClass('hidden').addClass('show');
			var tableRow = "<tr id='accountInfo' + (i + 1) + ''>" +
							"<td>" + (i + 1) + "</td>" +
							"<td>" + accountInfoList[i].accountName + "</td>" +
							"<td>" + accountInfoList[i].accountNumber + "</td>" +
							"<td>" + accountInfoList[i].bankName + "</td>" +
							"<td>" + accountInfoList[i].accountType + "</td>" +
							"<td>" + accountInfoList[i].exceptions + "</td>" +
							"</tr>";
			$('#accountInfoTbl > tbody:last').append(tableRow);
			//Update the decision window text
			$("#decisionWindowStartTime").text(decisionWindow.start + " " + decisionWindow.timezone);
			$("#decisionWindowEndTime").text(decisionWindow.end + " " + decisionWindow.timezone);
			if(!decisionWindow.outSideWindow) {
				$("#exceptionsBtn").removeClass("hidden");
			}
		}
	} else {
		//No data to display
		$("#noAccountInfo").removeClass('hidden');
	}	
}

/**
 * Load report and extracts
 */
function loadReportsAndExtracts() {
	$("#error-loading-reportsExtracts").addClass("hidden");
	$.ajax({
		type: "GET",
		url: globalBaseURL + "/user/dashboard/reports",
		dataType: "json",
		beforeSend: function() {
			$("#ajax-loader-reportsExtracts").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading Reports and Extracts information...');
		},
		complete: function() {
			$("#ajax-loader-reportsExtracts").empty();
		},
		success: function(response) {
			if(response && response.length > 0) {
				$("#reportsExtracts").removeClass('hidden');
				$.each(response, function(index, reportDto) {
					$("#reportsExtractsTable").append($("<tr/>")
												.append($("<td/>").text(reportDto.reportType))
												.append($("<td/>").text(reportDto.packageName))
												.append($("<td/>").text(reportDto.templateName))
												.append($(['<td>',
														   '<div>',
									                       '<button data-toggle="modal"',
									                       'data-report-id="-1"',
									                       'data-report-name=""' ,
									                       'data-template-name="' + reportDto.templateName + '"',
									                       'data-template-id="' + reportDto.templateId + '"',
									                       'data-output-format="' + reportDto.outputFormat + '"',
									                       'data-as-of-date="' + reportDto.asOfDate + '"',
									                       'data-as-of-date-is-symbolic="' + reportDto.asOfDateIsSymbolic + '"',
									                       'data-as-of-date-symbolic-value="' + reportDto.asOfDateSymbolicValue + '"',
									                       'class="open-reportDialog pp-sprite-run btn">',
									                       '</button>',
									                       '</div>',
									                       '</td>'].join('\n')))
											);
					
					
				});
			} else {
				$("#noReportsAndExtracts").removeClass('hidden');
			}
		},
        error: function() {
        	$("#error-loading-reportsExtracts").removeClass("hidden");
        }
	});
}
