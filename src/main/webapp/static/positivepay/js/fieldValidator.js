function validateInput(label, id, required, type, value) {
	errorCount = 0;
	$('#' + id + 'Error').remove();
	//required validation
	if(required) {
		if(!value) {
			var message = "" + label + " is a required field.";
			showErrorMessage(id, message);
			return;
		}
		else {
			removeErrorMessage(id);
		}
	}
	if(type.toUpperCase()=="alpha".toUpperCase()) {
		if(!isAlphanumeric(value)) {
			var message = "" + label + " should be alphanumeric only.";
			showErrorMessage(id, message);
			return;
		}
		else {
			removeErrorMessage(id);
		}
	}
	if(type.toUpperCase()=="number".toUpperCase()) {
		if(!isNumber(value)) {
			var message = "" + label + " should contain numbers only.";
			showErrorMessage(id, message);
			return;
		}
		else{
			removeErrorMessage(id);
		}
	}
	if(type.toUpperCase() == "date".toUpperCase()) {
		if(!isValidDate(value)) {
			var message = "" + value + " is not a valid date (MM/DD/YYYY).";
			showErrorMessage(id, message);
			return;
		}
		else{
			removeErrorMessage(id);
		}
	}
	if(type.toUpperCase() == "email".toUpperCase()) {
		if(!isValidEmail(value)) {
			var message = "" + value + " is not a valid email.";
			showErrorMessage(id, message);
			return;
		}
		else{
			removeErrorMessage(id);
		}
	}
	
	if(type.toUpperCase()=="select".toUpperCase()) {
		if(value.indexOf("--") != -1) {
			var message = "Please select an appropriate " + label + ".";
			showErrorMessage(id, message);
			return;
		}
		else{
			removeErrorMessage(id);
		}
	}
	
	if(type.toUpperCase()=="money".toUpperCase()) {
		if(!isDecimal(value)) {
			var message = "" + label + " should be entered in decimal format only.";
			showErrorMessage(id, message);
			return;
		}
		else{
			var dotPosition=value.indexOf(".");
			if(dotPosition == "-1") {
				$('#' + id + '').val(value + ".00");
			} else{
				$('#' + id + '').val(parseFloat(value).toFixed(2));
			}
			removeErrorMessage(id);
		}
	}
}

//Functions for Basic field Validation
function isValidEmail(email) {
	var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
	if(!emailReg.test( email) ) {
		return false;
	} else {
		return true;
	}
}

/*function isNumber(string) {
	var numberOnlyRegex = /^[0-9]*$/;
	if( !numberOnlyRegex.test( string ) ) {
		return false;
	} else {
		return true;
	}
}*/

function isAlphanumeric(string) {
	var regexAlphaNumeric = /^[a-zA-Z0-9]*$/;
	if(!regexAlphaNumeric.test( string)) {
		return false;
	} else {
		return true;
	}
}

//Validates that the input string is a valid date formatted as "dd/mm/yyyy"
function isValidDate(dateString) {
    // First check for the pattern
    if(!/^\d{2}\/\d{2}\/\d{4}$/.test(dateString))
        return false;

    // Parse the date parts to integers
    var parts = dateString.split("/");
    var month = parseInt(parts[0], 10);
    var day = parseInt(parts[1], 10);
    var year = parseInt(parts[2], 10);

    // Check the ranges of month and year
    if(year < 1000 || year > 3000 || month == 0 || month > 12)
        return false;

    var monthLength = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

    // Adjust for leap years
    if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
        monthLength[1] = 29;

    // Check the range of the day
    return day > 0 && day <= monthLength[month - 1];
};

/*function isDecimal(decimalString) {
	decimalString = decimalString.replace(".","");
	if(isNumber(decimalString)) {
		return true;
	} else{
		return false;
	}
}*/

function toDecimal(decimalString) {
	if(decimalString) {
		if(isNumber(decimalString) || isDigits(decimalString)) {
			var dotPosition=decimalString.indexOf(".");
			if(dotPosition=="-1") {
				decimalString = decimalString+".00";
			} else{
				decimalString = parseFloat(decimalString).toFixed(2);
			}
		}
	}
	return decimalString;
}

function showErrorMessage(id, message) {
	$('#' + id + '').addClass('highlight'); // this will highlight the input with red border
	$('#' + id + '').parent().after('<div class="col-sm-3" style="color:red;" id="' + id + 'Error">' + message + '</div>');
	errorCount++;
}

function removeErrorMessage(id) {
	$('#' + id + '').removeClass('highlight');
	$('#' + id + '').removeClass('required');
	$('#' + id + 'Error').remove();
}

//End Functions for Basic field Validation

//http://docs.jquery.com/Plugins/Validation/Methods/email
function isEmail( value ) {
	// contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
	return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(value);
};

// http://docs.jquery.com/Plugins/Validation/Methods/url
function isURL( value ) {
	// contributed by Scott Gonzalez: http://projects.scottsplayground.com/iri/
	return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
};

// http://docs.jquery.com/Plugins/Validation/Methods/date
function isDate( value ) {
	return !/Invalid|NaN/.test(new Date(value).toString());
};

// http://docs.jquery.com/Plugins/Validation/Methods/dateISO
function isDateISO( value ) {
	return /^\d{4}[\/\-]\d{1,2}[\/\-]\d{1,2}$/.test(value);
};

// http://docs.jquery.com/Plugins/Validation/Methods/number
function isNumber( value ) {
	return /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(value);
};

//customized for PP
function isPositiveAmount( value ) {
	return /^(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(value);
};

// http://docs.jquery.com/Plugins/Validation/Methods/digits
function isDigits( value ) {
	return /^\d+$/.test(value);
};

// http://docs.jquery.com/Plugins/Validation/Methods/creditcard
// based on http://en.wikipedia.org/wiki/Luhn
function isCreditcard(value) {
	// accept only spaces, digits and dashes
	if ( /[^0-9 \-]+/.test(value) ) {
		return false;
	}
	var nCheck = 0;
	var	nDigit = 0;
	var	bEven = false;

	value = value.replace(/\D/g, "");

	for (var n = value.length - 1; n >= 0; n--) {
		var cDigit = value.charAt(n);
		nDigit = parseInt(cDigit, 10);
		if (bEven) {
			if ((nDigit *= 2) > 9) {
				nDigit -= 9;
			}
		}
		nCheck += nDigit;
		bEven = !bEven;
	}

	return (nCheck % 10) === 0;
};

function getFormattedDate(timeInMilliSeconds) {
	if(!timeInMilliSeconds) {
		return "";
	}
	var date = new Date(parseInt(timeInMilliSeconds));
	var theyear = date.getFullYear();
	var themonth=("0" + (date.getMonth() + 1)).slice(-2);
	var thetoday=("0" + date.getDate()).slice(-2);
	return themonth + "/" + thetoday + "/" + theyear;
}

function getFormattedDateFromDateString(dateString) {
	if(!dateString) {
		return "";
	}
	
	var year = dateString.substring(0,dateString.indexOf("-"));
	var monthAndDate = dateString.substring(dateString.indexOf("-")+1,dateString.length);
	var month = monthAndDate.substring(0,monthAndDate.indexOf("-"));
	var dayOfMonth = monthAndDate.substring(monthAndDate.indexOf("-")+1,monthAndDate.length);
	
	return month + "/" + dayOfMonth + "/" + year;
}

function getFormattedDateWithTimeStamp(timeInMilliSeconds) {
	if(!timeInMilliSeconds) {
		return "";
	}
	var date = new Date(parseInt(timeInMilliSeconds));
	var theyear = date.getFullYear();
	var themonth = ("0" + (date.getMonth() + 1)).slice(-2);
	var thetoday = ("0" + date.getDate()).slice(-2);
	var theMinutes = ("0" + date.getMinutes()).slice(-2);
	var theSeconds = ("0" + date.getSeconds()).slice(-2);
	var theHours = ("0" + date.getHours()).slice(-2);
	var meridiam = "AM";
	if(theHours == 0) {
		theHours = 12;
	} else if(theHours > 11){
		theHours = theHours%12;
		if(theHours == 0){
			theHours = 12;
		}
		meridiam = "PM";
	}
	theHours = ("0" + theHours).slice(-2);
	return themonth+"/"+thetoday+"/"+theyear+" "+theHours+":"+theMinutes+":"+theSeconds+" "+meridiam;
}

function formatString(stringToFormat) {
	return (!stringToFormat) ? "" : stringToFormat;
}
function formatDollarAmount(dollarAmount) {
	if (!dollarAmount) {
		return "";
	} else if(isNaN(dollarAmount)){
		return dollarAmount;
	}
	else {
		dollarAmount = "$" + parseFloat(dollarAmount).toFixed(2);
		//Seperates the components of the number
		var n= dollarAmount.toString().split(".");
		//Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		//Combines the two sections
		return n.join(".");
	}
}
function convertFromMilitaryToNormalTime(militaryTime){
	var militaryArray = militaryTime.split(":");
	var hour = militaryArray[0];
	var meridiam = "AM";
	var minutes = militaryArray[1];
	if(hour == 0) {
		hour = 12;
	} else if(hour > 11){
		hour = hour%12;
		if(hour == 0){
			hour = 12;
		}
		meridiam = "PM";
	}
	hour = ("0" + hour).slice(-2);
	return hour + ":" + minutes + " " + meridiam;
}

function getFileExtension(fileName){
	return fileName.split('.').pop();
}