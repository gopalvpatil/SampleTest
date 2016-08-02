$(function() {
    $.ajax({
        type: "GET",
        url: globalBaseURL + "/messages/system",
        contentType: 'application/json; charset=utf-8',
        success: function(result) {
        	//No need to show Maintenance Message on Login Screen. Only LOGIN is allowed.
        	$("#maintenanceMessage").empty();
            if (result.message == null) {
                $("#systemMessage").hide();
            }
            else {
            	$("#maintenanceMessage").hide();
                $("#systemMessage").show();
                $("#labelSystemMessage").text(result.message);
            }
        },
        error: function(req, error, status) {
            window.alert("error: " + error + status);
        }
    });
});