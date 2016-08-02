//Global variable for holding payments data
var paymentDataList = null;

$(document).ready(function() {
	
	//Refresh payment data on select of new account
	$("#accountsChartSelect").change(function() {
		var selectedAccountNumber = $("#accountsChartSelect").val();
		$.each(paymentDataList, function(index, paymentData) {
			if(selectedAccountNumber == '' && paymentData.accountNumber == null) {
				drawChart(paymentData);
				return false;
			} else if(paymentData.accountNumber == selectedAccountNumber) {
    			drawChart(paymentData);
    			return false;
    		}
    	});
	});
	
	$('#dataCriteriaBtn').click(function(event){
		event.preventDefault(); 
		if(!validateDataCriteriaInput()) {
			return;
		} else {
			$("#dataCriteriaError").addClass("hidden");
		}
		
		var criteriaDTO = {accountNumbers : [] , paymentStatusTypes : [] ,  paidExceptionStatus : [] , fromCheckNumber  : '', toCheckNumber : '', amountType : '', fromAmount  : '', toAmount : '', dateType : '', fromDate : '', toDate : ''};
		$('#accountNumbers :selected').each(function(i, selected) { 
			criteriaDTO.accountNumbers[i] = $(selected).text(); 
		});
		$('#paymentStatusTypes :selected').each(function(i, selected) { 
			criteriaDTO.paymentStatusTypes[i] = $(selected).text(); 
		});
		$('#paidExceptionStatus :selected').each(function(i, selected) { 
			criteriaDTO.paidExceptionStatus[i] = $(selected).val(); 
		});
		
		criteriaDTO.fromCheckNumber = $("#fromCheckNumber").val();
		criteriaDTO.toCheckNumber = $("#toCheckNumber").val();
		var amountType = $("#amountType").val();
    	if(amountType != "") {
    		criteriaDTO.amountType = $("#amountType option:selected").text();
    	}
    	criteriaDTO.fromAmount = $("#fromAmount").val();
		criteriaDTO.toAmount = $("#toAmount").val();
    	var dateType = $("#dateType").val();
    	if(dateType != "") {
    		criteriaDTO.dateType = $( "#dateType option:selected" ).text();
    	}
    	criteriaDTO.fromDate = $("#fromDate").val();
		criteriaDTO.toDate = $("#toDate").val();
		
		if(typeof(window.localStorage)!=="undefined") {
    		window.localStorage.setItem("dataCriteriaDto", JSON.stringify(criteriaDTO));
    	}
		
		window.location.href = globalBaseURL + "/user/paymentsinfo/";
		return true;
    }); 

	//Load payment data
	loadPaymentData();
	
	$('#accountNumbers').multiselect({
		nonSelectedText: 'Account Number',
		buttonWidth: '868px',
		numberDisplayed: 3
    });
	 
	$('#paymentStatusTypes').multiselect({
		nonSelectedText: 'Payment Status',
		buttonWidth :'868px',
		numberDisplayed: 4
    });
	
	$('#paidExceptionStatus').multiselect({
		nonSelectedText: 'Paid Exception Status',
		buttonWidth: '868px',
		numberDisplayed: 2
	});
	 
	$("#fromDate").datepicker();
	$("#fromDateDiv").click(function() {
		$("#fromDate").focus();
	});

	$("#toDate").datepicker();
	$("#toDateDiv").click(function() {
		$("#toDate").focus();
	});
});

/**
 * Populate account drop down based on list
 * @param paymentDataList
 */
function populateAccountSelect(paymentDataList) {
	$("#accountsChartSelect").empty();
	if(paymentDataList ) {
		if(paymentDataList.length > 2) { //Dont display ALL if only one account is present. checking more than 2 as paymentDataList contains an additonal data for ALL
			$("#accountsChartSelect").append('<option value="">ALL</option>');
		}
		$.each(paymentDataList, function(index, paymentData) {
			if(paymentData.accountNumber != null) {//Null value is bound to ALL
				$("#accountsChartSelect").append('<option value=' + paymentData.accountNumber + '>' + paymentData.accountNumber + '</option>');
			}
		});
		$('#accountsChartSelect option:first-child').attr("selected", "selected");
	}
}

/**
 * Load payment data and populate
 * @param accountNumber
 */
function loadPaymentData() {
	$("#error-loading-paymentInfo").addClass("hidden");
	//var paymentData = null;
	var url = globalBaseURL + "/user/dashboard/allaccounts/payments";
	
	$.ajax({
		type : "GET",
		url : url,
		dataType : "json",
		beforeSend: function() {
			$("#ajax-loader-payments").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> Loading payment data...');
		},
		complete: function() {
			$("#ajax-loader-payments").empty();
			$("#paymentRefreshData").empty();
			$("#paymentRefreshData").text($.format.date(new Date(), "MMMM dd, yyyy hh:mm a"));
		},
		success : function(response) {
			paymentDataList = response;
        	populateAccountSelect(paymentDataList);
        	$.each(paymentDataList, function(index, paymentData) {
        		if(paymentData.accountNumber== null) {
        			drawChart(paymentData);//Draw chart for all payments which will be available when accountNumber is null from server
        			return false;
        		}
        	});
			//google.setOnLoadCallback(drawChart(paymentData));
		},
		error : function() {
			$("#error-loading-paymentInfo").removeClass("hidden");
		}
	});
	
	// Set a callback to run when the Google Visualization API is loaded.
}

//Callback that creates and populates a data table,
// instantiates the pie chart, passes in the data and
// draws it.
function drawChart(paymentData) {	
	var colors =  ['#006B87','#669295','#0F4D76','#2090AA','#666666','#DCDCD4','#BFBFBF','#D7D7D7','#ECECEC',
	               '#e0440e', '#e6693e', '#ec8f6e', '#f3b49f', '#f6c7b6',
	               'DarkGreen', 'DarkKhaki', 'DarkMagenta', 'DarkOliveGreen',
	               'DarkOrange','DarkOrchid','DarkRed','DarkSalmon','DarkSeaGreen',
	               'DarkSlateBlue','DarkSlateGray','DarkTurquoise','Gainsboro'];
	
	var amountByStatusPie = [['ItemType','Amount']];
	$.each(paymentData.amountByStatus, function(key, value) {
		amountByStatusPie.push([key,value]);
	});

	var amountByDateColumn = [['ItemType','Amount',{ role: 'style' }]];
	$.each(paymentData.amountByDate, function(key, value) {
		amountByDateColumn.push([new Date(Date.parse(key)),value,'#006B87']);		
	});
	
	var countByStatusPie = [['ItemType','Count']];
	$.each(paymentData.countByStatus, function(key, value) {
		countByStatusPie.push([key,value]);
	});
	
	var countByDateColumn = [['ItemType','Count',{ role: 'style' }]];
	$.each(paymentData.countByDate, function(key, value) {
		countByDateColumn.push([new Date(Date.parse(key)),value, '#006B87']);
	});
	
	 // Set chart options
    var pieChartOption = {'title': 'Outstanding Payments by Status',
    					'width':250,
    					'height':250,
    					'legend': {position: 'left',alignment:'center',textStyle:{fontSize: 12}},
    					//'legend': 'none',
    					'sliceVisibilityThreshold':0,
    					'chartArea':{left:"0%",top:"10%",width:"95%",height:"100%"},
    					'colors': colors};
    
    var byDateChartOption = {'title': 'Paid status',
				'width':250,
				'height':250,
				'legend': 'none',
				'chartArea':{left:"20%",top:"10%",width:"100%",height:"65%"},
				'hAxis' : {format:'MM/dd',slantedText:true,slantedTextAngle:'90'},
				'vAxis' : { viewWindowMode:'explicit', viewWindow: {min:0}}
				};
	
	// Create the data table.
    var amountPie = google.visualization.arrayToDataTable(amountByStatusPie);
    var amountColumn = google.visualization.arrayToDataTable(amountByDateColumn);
    var countPie = google.visualization.arrayToDataTable(countByStatusPie);
    var countColumn = google.visualization.arrayToDataTable(countByDateColumn);
    
    var formatter = new google.visualization.NumberFormat({prefix: '$'});
    formatter.format(amountPie, 1); // Apply formatter Amount second column
    formatter.format(amountColumn, 1); // Apply formatter Amount second column

    // Instantiate and draw our chart, passing in some options.
    var amountPiechart = new google.visualization.PieChart(document.getElementById('amountsPiechart_div'));
    amountPiechart.draw(amountPie, pieChartOption);
    var amountColumnchart = new google.visualization.ColumnChart(document.getElementById('amountsColumnchart_div'));
    amountColumnchart.draw(amountColumn, byDateChartOption);
    var countPiechart = new google.visualization.PieChart(document.getElementById('countsPiechart_div'));
    countPiechart.draw(countPie, pieChartOption);
    var countColumnchart = new google.visualization.ColumnChart(document.getElementById('countsColumnchart_div'));
    countColumnchart.draw(countColumn, byDateChartOption);
    
    google.visualization.events.addListener(amountPiechart, 'select', openDataCriteria);
    google.visualization.events.addListener(amountColumnchart, 'select', openDataCriteria);
    google.visualization.events.addListener(countPiechart, 'select', openDataCriteria);
    google.visualization.events.addListener(countColumnchart, 'select', openDataCriteria);
}

function openDataCriteria() {
	$("#dataCriteriaError").addClass("hidden");
	$("#dataCriteria").modal('show');
}

function validateDataCriteriaInput() {
	if(!$("#accountNumbers").val()) {
		errorMessage('Please select account number(s)');
		return false;
	}
	
	if(!isNumber('#fromCheckNumber') || !isNumber('#toCheckNumber')) {
		errorMessage('Check number should be number only'); 
		return false;
	}
	
	var fromCheckNumber = $("#fromCheckNumber").val();
	var toCheckNumber = $("#toCheckNumber").val();
	if((fromCheckNumber != "" && toCheckNumber == "") || (fromCheckNumber == "" && toCheckNumber != "")) {
		errorMessage('Please enter both to and from values for check number!!');
		return false;
	}
	var fromAmount = $("#fromAmount").val();
	var toAmount = $("#toAmount").val();
	if((fromAmount != "" && toAmount == "") || (fromAmount == "" && toAmount != "")) {
		errorMessage('Please enter both to and from values for amount types!!');
		return false;
	}
	
	if($("#amountType").val() && fromAmount == "" && toAmount == "") {
		errorMessage('Please enter both to and from values for amount types!!');
		return false;
	}
	var fromDate = $("#fromDate").val();
	var toDate = $("#toDate").val();
	if((fromDate != "" && toDate == "") || (fromDate == "" && toDate != "")) {
		errorMessage('Please enter both to and from values for date types!!');
		return false;
	}
	if($("#dateType").val() && fromDate == "" && toDate == "") {
		errorMessage('Please enter both to and from values for date types!!');
		return false;
	}
	return true;
	//TODO : Need to add validation on remaining fields.	
}

function isNumber(element) {
	if($(element).val() && !$.isNumeric($(element).val())) {
		return false;
	}
	return true;
}

function errorMessage(errorMessage) {
	$("#errors").empty();//Empty previous errors
    $("#errors").append($("<li></li>").html(errorMessage));
    if ($("#dataCriteriaError").hasClass('hidden')) {
        $("#dataCriteriaError").removeClass('hidden').addClass('show');
    }
}
