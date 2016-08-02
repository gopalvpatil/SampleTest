$(document).ready(function() {	
	//initialize();
	//Ajax Call to load Account Info
	$( "#logLevel" ).change(function() {
		logLevel = $(this).val();
		if(logLevel != "") {
			loadApplicationLogs(logLevel);
		} else {
			$("#logLevel").notify("Please select the log level", "error");
		}
	});
	
	$(window).scroll(function() { //detect page scroll
        
        if($(window).scrollTop() + $(window).height() == $(document).height()) { //user scrolled to bottom of the page?
            if(!endOfLog && (pagesLoaded < totalPages && loading == false)) { //there's more data to load
                loading = true; //prevent further ajax loading
                //$('.ajax-loader-autoscroll').show(); //show loading image
                $('.ajax-loader-autoscroll').html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading more logs...');
                //load data from the server using a HTTP POST request
                //$.post('autoload_process.php',{'group_no': track_load}, function(data){
				//loaded group increment            
                //$("#appLogs").append(data); //append received data into the element
				displayAppLogs(appLogs, logLevel, pagesLoaded);
				pagesLoaded++; 
                //hide loading image
                $(".ajax-loader-autoscroll").empty(); //hide loading image once data is received
                loading = false; 
            } else {
				//$("#appLogs").append("<div class='logLine'><strong>No more logs to display!!</strong></div>");
			}
        }
    });
});

function loadApplicationLogs(logLevel, query) {
	initialize();
	$("#error-loading-appLogs").addClass("hidden");
	$("#appLogs").empty();
	$.ajax({
		type: "GET",
		url: globalBaseURL+"/fetchlogs?logLevel="+logLevel,
		dataType: "json",
		beforeSend: function() {
			$("#ajax-loader-appLogs").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading application logs...');
		},
		complete: function() {
			$("#ajax-loader-appLogs").empty();
		},
		success: function(appLogsJson) {
			appLogs = appLogsJson;
			if(appLogs.length > initialRecordsToFetch){
				totalPages = Math.ceil((appLogsJson.length - initialRecordsToFetch)/recordsPerPage) + 1;
			}
			displayAppLogs(appLogs, logLevel, pagesLoaded);
			pagesLoaded++;
		},
        error: function() {
        	$("#error-loading-appLogs").removeClass("hidden");
        }
	});
}

function displayAppLogs(appLogs, logLevel, pagesLoaded) {
	$("#noAppLogs").addClass('hidden');
	//check if the recentFilesList list is empty
	if(appLogs.length > 0) {
		if(pagesLoaded == 0) {
			$("#appLogs").append("<div class='alert alert-info'><strong>Showing " + appLogs.length + " lines of " + logLevel + " level logs...</strong> " +
			"<a id='reloadLogs' class='btn' href='#'><i class='icon-repeat'></i> Reload</a></div>");
			for(var i = (appLogs.length < initialRecordsToFetch ? appLogs.length : initialRecordsToFetch); i > 0; i--) {
				$("#appLogs").removeClass('hidden').addClass('show');
				$("#appLogs").append("<div class='logLine'>" + appLogs[i - 1] + "</div>");
			}
			$("#reloadLogs").click(function() {
				loadApplicationLogs(logLevel);
			});
		} else {
			if(appLogs.length > initialRecordsToFetch){
				var start = initialRecordsToFetch + (pagesLoaded-1) * recordsPerPage;
				var end = start+recordsPerPage;
				if(end > appLogs.length){
					end = appLogs.length;
				}
				for(var i = end; i > start; i--) {
					//$("#appLogs").removeClass('hidden').addClass('show');
					$("#appLogs").append("<div class='logLine'>" + appLogs[i - 1] + "</div>");
				}
			} else {
				//hide loading image
				endOfLog = true;
                $("#ajax-loader-autoscroll").empty(); 
				//$("#appLogs").append("<div class='logLine'><strong>No more logs to display!!</strong></div>");
			}
		}
	} else {
		//No data to display
		$("#noAppLogs").removeClass('hidden');
	}	
}

function initialize() {
	pagesLoaded = 0;
	totalPages = 1;
	loading = false;
	initialRecordsToFetch = 5000;
	recordsPerPage = 1000;
	endOfLog = false;
	appLogs = new Array();
}