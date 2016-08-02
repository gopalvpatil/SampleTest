/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 1/8/14
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
$( document ).ready(function() {
    $('#fileselectbutton').click(function (e) {
        $('#file').trigger('click');
    });

    $('#file').change(function (e) {
        var val = $(this).val();
        var file = val.split('/[\\/]/');
        $('#filename').val(file[file.length - 1]);
        $('#file').val(file[file.length - 1]);
    });
    
    fetchRecentFilesList("allfiles");
	
	$( "#uploadDate" ).datepicker({
		showAnim: "clip",
		onSelect: function(selectedDate,event) {
			filterRecentFiles();
		}
    });
	
	$('#calendarBtn').click(function(e) {
		e.preventDefault();
		$("#uploadDate").datepicker().focus();
	});
});

function fetchRecentFilesList(url) {
	$("#error-loading-recentFiles").addClass("hidden");
	var loadingMsg = "Loading recently uploaded files...";
	if(url.indexOf("filterfiles") != -1){
		loadingMsg = "Filtering the recently uploaded files...";
	}
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		beforeSend: function() {
			$("#ajax-loader-recentFiles").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> '+loadingMsg);
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
	//Empty table data if any
	$('#recentFilesTbl > tbody:last').empty();
	$("#noRecentFiles").addClass('hidden');
	//check if the recentFilesList list is empty
	if(recentFilesList.length > 0) {
		//Make table rows by iterating over the payment list from start to end
		for(var i = 0; i < recentFilesList.length; i++) {
			$("#recentFiles").removeClass('hidden').addClass('show');
			$('#recentFilesTbl').removeClass('hidden');
			var tableRow = "<tr id='recentFile'+(i+1)+''>"+
							"<td>" + (i + 1) + "</td>"+
							"<td>" + getFormattedDateWithTimeStamp(recentFilesList[i].uploadDate) + "</td>"+
							"<td>" + recentFilesList[i].fileName + "</td>"+
							"<td>" + recentFilesList[i].noOfRecords + "</td>"+
							"<td>" + recentFilesList[i].itemsLoaded + "</td>"+
							"<td><a href='#' onclick='showErrorItemsPopUp(&quot;"+recentFilesList[i].fileMetaDataId+"&quot;, &quot;"+recentFilesList[i].fileName+"&quot;, &quot;"+recentFilesList[i].uploadDate+"&quot;, &quot;"+recentFilesList[i].companyName+"&quot;);'>" + recentFilesList[i].errorRecordsLoaded + "</a></td>";
			//check if action header is present then show download link, otherwise skip
			if($('#actionHeader').length) {
				tableRow = tableRow+"<td><a class='btn' href='file/download?fileUid="+recentFilesList[i].fileUid+"'><i class='icon-download'></i> Download</a></td>";
			}
			tableRow = tableRow+"</tr>";
			$('#recentFilesTbl > tbody:last').append(tableRow);
			//initializeErrorLinks();
		}
	} else {
		//No data to display
		$("#noRecentFiles").removeClass('hidden');
	}	
}

function filterRecentFiles() {
	var uploadDate = $("#uploadDate").val() ;
	var	accountNumber = $("#accountNumber").val();
	var	noOfDaysBefore = $("#dateRange").val();
	var filterUrl = "filterfiles?uploadDate=" + uploadDate + "&accountNumber=" + accountNumber + "&noOfDaysBefore=" + noOfDaysBefore;
	$('#recentFilesTbl').addClass('hidden');
	fetchRecentFilesList(filterUrl);
}

function clearFilter(){
	if($('#uploadDate').val() == "" && $('#accountNumber').val() == "" && $('#dateRange').val() == ""){
		//do nothing
		return;
	}
	$('#uploadDate').val("");
	$('#accountNumber').val("");
	$('#dateRange').val("");
	//Now refresh the list
	filterRecentFiles();
}