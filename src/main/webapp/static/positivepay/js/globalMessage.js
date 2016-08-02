$(function() {
    $.ajax({
        type: "GET",
        url: globalBaseURL + "/messages/maintenance",
        contentType: 'application/json; charset=utf-8',
        success: function(result) {
            if (result.message == "undefined" || result.message == null || result.message.indexOf("N/A") > -1) {
                $("#maintenanceMessage").hide();
				$("#dismissReadButton1").hide();
				if(typeof(result.message)!="undefined")
				{
					var index = result.message.indexOf("N/A");
					if(index > -1)
						result.message = result.message.substring(0,index);
				}
				$("#labelMaintenanceMessage").text(result.message);
				$("#dismissReadButton").show();
            }
            else {
                $("#maintenanceMessage").show();
                $("#dismissReadButton").show();
				$("#dismissReadButton1").show();
                $("#labelMaintenanceMessage").text(result.message);
            }
        },
        error: function(req, error, status) {
            window.alert("error: " + error + status);
        }
    });
});


$( "#dismissReadButton1" ).click(function() {
	 $.ajax({
         type: "POST",
         url: globalBaseURL + "/user/dismissasread",
         contentType: 'application/json; charset=utf-8',
         success: function(result) {
			 if(result)
			 {
				 $("#dismissReadButton1").hide();
				 window.localStorage.setItem('dismiss', "1");
			 }
         },
         error: function(req, error, status) {
             window.alert("error: " + error + status);
         }
    });
});