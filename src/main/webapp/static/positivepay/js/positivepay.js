$(function() {
	//browser check mainly for IE < 9
	var paragraph1 = 'Your browser '+$.browser.className+' '+$.browser.version+' is out of date, and may not be compatible with '+
				'Positive Pay. A list of the most popular web browsers can be '+
				'found below.';
	if($.browser.name == 'msie'){
		paragraph1 = 'You are using Internet Explorer '+$.browser.version+' and this may not be compatible with '+
				'positive pay. In order to have a good user experience, you must '+
				'be using Internet Explorer version 9 or above or one from the list of the most popular web browsers '+
				'given below.';
	}
	$.reject({
		reject: {
			msie: 8, // Microsoft Internet Explorer 8 and below
			konqueror: true, // Konqueror (Linux)
			unknown: true // Everything else
		},
		imagePath: "./static/thirdparty/jReject/images/",
		paragraph1: paragraph1,
		header: 'Your browser is not supported.',
		display: ['chrome','firefox','msie','safari'], // What browsers to display and their order (default set below)
		browserShow: true, // Should the browser options be shown?	
		closeMessage: 'By closing this window, you acknowledge that your experience '+
						 'on positive pay may be degraded if you have not upgraded your broswer.',
	});
	// Common ajax set up options
	//1. defaulting the jquery ajax call to not to cache result. Issue with I.E and some browsers
	//2. ajax session timeout function call to redirect to login page.
	$.ajaxSetup(
		{ 
			cache: false,
			statusCode: 
	        {
	            911: ajaxSessionTimeout
	        }
		}
	);
	
	function ajaxSessionTimeout()
	{
		if (opener) self.close();
	    // Handle Ajax session timeout here
		window.location.replace(globalBaseURL + "/login?sessionExpired=true");
	}
	
	function centerModal() {
	    $(this).css('display', 'block');
	    var $dialog = $(this).find(".modal-dialog");
	    var offset = (($(window).height() - $dialog.height()) / 2) - 50;
	    // Center modal vertically in window
	    $dialog.css("margin-top", offset);
	}
	
	$('.modal').on('shown.bs.modal', centerModal);
	$(window).on("resize", function () {
	    $('.modal:visible').each(centerModal);
	});
	
	/**
	 * Below code makes the menu items highlighted
	 */
	$("#menuNavBar > li > a").each(function() {
		var href = $(this).attr("href");
		if(globalRequestUrl.indexOf(href) > -1) {
			$(this).parent('li').addClass("active");
		}
	});
	$("#menuNavBar > li > ul > li > a").each(function() {
		var href = $(this).attr("href");
		if(globalRequestUrl.indexOf(href) > -1) {
			$(this).parent('li').addClass("active");
			$(this).parent('li').parent('ul').parent('li').addClass("active");
		}
	});
});

//Global Functions
function sortByName(array, key) {
    return array.sort(function(a, b) {
        var x = a[key]; var y = b[key];
        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    });
}