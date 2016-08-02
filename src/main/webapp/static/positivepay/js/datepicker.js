(function($) {
	$.fn.populate_symbolic_values = function(options) {
		var settings = $.extend({
            selectedDate: 'TODAY',
        }, options);
		
		var date = new Date();
        var y = date.getFullYear();
        var m = date.getMonth();
        
        var dateLastWeekday = new Date();
        if(dateLastWeekday.getDay() == 0 || dateLastWeekday.getDay() == 6) {
        	dateLastWeekday.setDate(dateLastWeekday.getDate() - (dateLastWeekday.getDay() + 2));
        }
        
        var today = "Today (" + $.format.date(date, "MM/dd/yyyy") + ")";
        var yesterday = "Yesterday (" + $.format.date(new Date().setDate(new Date().getDate() - 1), "MM/dd/yyyy") + ")";
        var firstDayOfThisMonth = "First Day of This Month (" + $.format.date(new Date(y, m, 1), "MM/dd/yyyy") + ")";
        var lastDayOfThisMonth = "Last Day of This Month (" + $.format.date(new Date(y, m + 1, 0), "MM/dd/yyyy") + ")";
        var firstDayOfLastMonth = "First Day of Last Month (" + $.format.date(new Date(y, m - 1, 1), "MM/dd/yyyy") + ")";
        var lastDayOfLastMonth = "Last Day of Last Month (" + $.format.date(new Date(y, m, 0), "MM/dd/yyyy") + ")";
        var lastWeekday = "Last Weekday (" + $.format.date(dateLastWeekday, "MM/dd/yyyy") + ")";
        
		return this.each(function() {	  	        
	        $(this).empty();
	        $('<option/>').val("TODAY").text(today).appendTo($(this));
	        $('<option/>').val("YESTERDAY").text(yesterday).appendTo($(this));
	        $('<option/>').val("LDOTM").text(lastDayOfThisMonth).appendTo($(this));
	        $('<option/>').val("FDOTM").text(firstDayOfThisMonth).appendTo($(this));
	        $('<option/>').val("LDOLM").text(lastDayOfLastMonth).appendTo($(this));
	        $('<option/>').val("FDOLM").text(firstDayOfLastMonth).appendTo($(this));
	        $('<option/>').val("LWD").text(lastWeekday).appendTo($(this));
	        $(this).val(settings.selectedDate).trigger("change");
		});
	}
	
	$.fn.pp_datepicker = function(options) {
		var settings = $.extend({
            namePrefix: 'datepicker',
            idPrefix: 'pp_datepicker',
            dateFrom: new Date(),
            dateTo: new Date(),
            dateSymbolicFrom: "YESTERDAY",
            dateSymbolicTo: "TODAY",
            operator: "equals",
            isSymbolic: false
        }, options);
		
	    return this.each(function() {	    	
	    	var $operatorDiv = $("<div/>", { class: "col-sm-2" } );
	    	var $operatorSelect = $('<select />', { id: settings.idPrefix + "_operator", name: settings.namePrefix + "Operator", class: "pp-width-full" } );
	    	$('<option/>', {value: "equals", text: "Equals"}).appendTo($operatorSelect);
	    	$('<option/>', {value: "isBefore", text: "Is Before"}).appendTo($operatorSelect);
	    	$('<option/>', {value: "isAfter", text: "Is After"}).appendTo($operatorSelect);
	    	$('<option/>', {value: "isBetween", text: "Is Between"}).appendTo($operatorSelect);
	    	$operatorDiv.append($operatorSelect);
	    		    	
	    	var $fromDateDiv = $('<div/>', { class: "col-sm-3 date-input" } ); 
	    	var $fromDateInput = $('<input/>', { id: settings.idPrefix + "_from_date", name: settings.namePrefix + "FromDate", class: "pp-width-full", type: "text"} );
	    	var $fromDateSelect = $('<select/>', { id: settings.idPrefix + "_from_date_symbolic", name: settings.namePrefix + "FromDateSymbolic", class: "pp-width-full hidden" } );
	    	$fromDateDiv.append($fromDateInput);
	    	$fromDateDiv.append($fromDateSelect);
	    	
	    	var $toDateDiv = $('<div/>', { class: "col-sm-3 date-input hidden" } ); 
	    	var $toDateInput = $('<input/>', { id: settings.idPrefix + "_to_date", name: settings.namePrefix + "ToDate", class: "pp-width-full", type: "text"} );
	    	var $toDateSelect = $('<select/>', { id: settings.idPrefix + "_to_date_symbolic", name: settings.namePrefix + "ToDateSymbolic", class: "pp-width-full hidden" } );
	    	$toDateDiv.append($toDateInput);
	    	$toDateDiv.append($toDateSelect);

	    	var $checkBoxDiv = $('<div/>', { class: "col-sm-2" } );
	    	var $labelSymbolic = $('<label/>').css("margin-top", "5px");
	    	var $checkboxSymbolic = $('<input/>', { id: settings.idPrefix + "_is_symbolic", name: settings.namePrefix + "IsSymbolic", type: "checkbox" } );
	    	$labelSymbolic.append($checkboxSymbolic);
	    	$checkBoxDiv.append($labelSymbolic);
	    	$checkBoxDiv.append(" Symbolic Date");
	    		    	
	    	$(this).append($operatorDiv);
	    	$(this).append($fromDateDiv);
	    	$(this).append($toDateDiv);
	    	$(this).append($checkBoxDiv);
	    		    	
	    	$fromDateDiv.click(function() {
	    		$fromDateInput.focus();
	    	});

	    	$toDateDiv.click(function() {
	    		$toDateInput.focus();
	    	});

	    	$operatorSelect.change(function() {
	    		if($(this).val() == "isBetween") {
	    			$toDateDiv.removeClass("hidden");
	    		} else {
	    			$toDateDiv.addClass("hidden");	
	    		}
    		});
	    	
	    	$checkboxSymbolic.change(function() {
	    		if(this.checked) {
	    			$fromDateInput.addClass("hidden");
	    			$fromDateSelect.removeClass("hidden");
	    			$toDateInput.addClass("hidden");
	    			$toDateSelect.removeClass("hidden");
	    		} else {
	    			$fromDateInput.removeClass("hidden");
	    			$fromDateSelect.addClass("hidden");	
	    			$toDateInput.removeClass("hidden");
	    			$toDateSelect.addClass("hidden");	
	    		}
    		});
	    	
	    	$fromDateInput.val(settings.dateFrom);
	    	$toDateInput.val(settings.dateTo);
	    	$fromDateSelect.populate_symbolic_values( { selectedDate: settings.dateSymbolicFrom } );
	    	$toDateSelect.populate_symbolic_values( { selectedDate: settings.dateSymbolicTo } );
	    	$operatorSelect.val(settings.operator).trigger("change");
	    	$checkboxSymbolic.prop('checked', settings.isSymbolic).trigger("change");
	    	
	    	$fromDateInput.datepicker();
	    	$toDateInput.datepicker();
	    });	    
	}
} (jQuery));