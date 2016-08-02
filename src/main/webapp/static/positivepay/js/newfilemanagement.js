$( document ).ready(function() {
	initializeMultipleFileUpload();
    $('#fileselectbutton').click(function (e) {
        $('#file').trigger('click');
    });

    $('#file').change(function (e) {
		fileData.fileToUpload = e.target.files;
        var val = $(this).val();
        var file = val.split('/[\\/]/');
        $('#filename').val(file[file.length - 1]);
        $('#file').val(file[file.length - 1]);
    });
	
    $('#uploadAgainLink').click(function (e) {
		e.preventDefault();
		initializeMultipleFileUpload();
		$("#multiUploadForm").show();
		$("#fileListTbl > tbody tr").remove();
		$("#fileListTbl").addClass("hidden");
		$( "th" ).first().show();
		$('#uploadAgainLink').hide();
		$('#uploadFilesBtn').hide();
	});
    $('#uploadFilesBtn').click(function (e) {
    	e.stopPropagation(); // Stop stuff happening
        e.preventDefault(); // Totally stop stuff happening
		//Hide the form
		$("#multiUploadForm").hide();
		var noOfAjaxRequests = filesToUpload.length;
		$.each( filesToUpload, function( key, value ) {
			var fileToUpload = value.fileToUpload;
			var fileMappingId = value.fileMappingId;
			 // Create a formdata object and add the files
			var data = new FormData();
			$.each(fileToUpload, function(key, value)
			{
				data.append("file", value);
			});
			data.append("fileMappingId", fileMappingId);
			var statusCell = $("#file"+(value.rowNum)+"").find('td.status');
			$.ajax({
				url: globalBaseURL + "/user/newfilemanagement",
				type: 'POST',
				data: data,
				cache: false,
				dataType: 'json',
				processData: false, // Don't process the files
				contentType: false, // Set content type to false as jQuery will tell the server its a query string request
				beforeSend: function() {
					statusCell.html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Processing...');
				},
				success: function(fileUploadResponse, textStatus, jqXHR)
				{
					noOfAjaxRequests--;
					var response = "<span><img src='" + globalBaseURL + "/static/positivepay/images/icons/success.png' /> "+fileUploadResponse.response+"</span>";
					if(fileUploadResponse.status == 1000)
					{
						if(fileUploadResponse.warnings.length > 0){
							response = response+"<br/>";
							var warnings ="<ul>";
							$.each(fileUploadResponse.warnings, function(key, warning)
							{	
								warnings = warnings.concat("<li>"+warning+"</li>");
							});
							response = response+warnings+"</ul>";
						}
						//statusCell.css();
						statusCell.html(response);
						statusCell.addClass("text-success");
					}
					else
					{	
						response = "<span><img src='" + globalBaseURL + "/static/positivepay/images/icons/red-alert-sprite.png' /> "+fileUploadResponse.response+"</span>";
						statusCell.html(response);
						statusCell.addClass("text-danger");
					}
					if (noOfAjaxRequests == 0) {
						$("#uploadAgainLink").show();
					}
				},
				error: function(jqXHR, textStatus, errorThrown)
				{
					statusCell.html(textStatus);
				},
				complete: function() {
					//$("#file"+value.rowNum+"").find('td.status').html('');
				}
			});
			//hide the status column once the file has been processed
			$('td:nth-child(1),th:nth-child(1)').hide();
		});   	  
    });
    
    $('#addFileRow').click(function (e) {
    	var fileIndex = $('#fileTable tr').children().length;
        $('#fileTable').append(
                '<tr><td>'+
                '   <input type="file" name="files['+ fileIndex +']" />'+
                '</td></tr>');
    });
    
	$('#addFileBtn').click(function (e) {
		e.preventDefault();
        var filePath = $('#filename').val();
		var fileMappingName = $( "#fileMappingId option:selected" ).text();
		var fileMappingValue = $('#fileMappingId').val();
		$("#validationErrorBox").addClass('hidden');
		var errorMessage = "";
		if(!filePath){
			$("#validationErrorBox").removeClass('hidden');
			errorMessage = "No file has been chosen. Please select a file to continue.";
			$("#fileUploadErrorMessage").text(errorMessage);
			return false;
		}
		if(!fileMappingValue){
			$("#validationErrorBox").removeClass('hidden');
			errorMessage = "Please select the file mapping template.";
			$("#fileUploadErrorMessage").text(errorMessage);
			return false;
		} else {
			fileData.fileMappingId = fileMappingValue; 
		}
		//get the file name alone
		var fileName = fileData.fileToUpload[0].name;
		var fileSize = fileData.fileToUpload[0].size;
		var fileExtension = getFileExtension(fileName);
		if(fileExtension.toUpperCase() != "TXT" && fileExtension.toUpperCase() != "CSV"){
			$("#validationErrorBox").removeClass('hidden');
			errorMessage = "Only .csv and .txt files are allowed. Please upload the file in the allowed format.";
			$("#fileUploadErrorMessage").text(errorMessage);
			return false;
		}
		//file size validation
		if(fileSize/1048576 > 15){
			$("#validationErrorBox").removeClass('hidden');
			errorMessage = "You are trying to upload a file that is larger than the maximum file size allowed (15 MB).</strong>";
			$("#fileUploadErrorMessage").text(errorMessage);
			return false;
		}
		//Show table and add file
		$("#fileListTbl").removeClass('hidden');
		noOfFiles++;
		fileData.rowNum = noOfFiles;
		filesToUpload.push(fileData);
		var tableRow = "<tr id='file"+noOfFiles+"'>" +
						"<td align='center'><div class='pp-sprite-delete' onclick='deleteFile(this);'></div></td>" +
						"<td>" + fileName + "</td>" +
						"<td>" + fileMappingName + "</td>" +
						"<td class='status'>Ready To Upload</td>" +
						"</tr>";
		$('#fileListTbl > tbody:last').append(tableRow);
		//Show upload button if hidden
		
		if($("#uploadFilesBtn").is(":hidden") ){
			$("#uploadFilesBtn").show();
		}
		
		//clear fields
		$('#filename').val("");
		$('#fileMappingId').val("");
		//
		fileData = {};
    });
});

function deleteFile(td){
	var tr = $(td).closest('tr');
	var rowid = tr.attr('id');
	var rownum = rowid.substring(4);
	tr.fadeOut(400, function(){
		tr.remove();
	});
	noOfFiles--;
	//delete from the list
	filesToUpload.splice(rownum-1, 1);
	if(noOfFiles == 0){
		$("#fileListTbl").addClass('hidden');
		$("#uploadFilesBtn").hide();
	}
}

function initializeMultipleFileUpload(){
	noOfFiles = 0;
	fileData = {};
	filesToUpload = new Array();
}