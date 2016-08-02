$(function() {
    populateMaintenanceMessage();
    populateSystemMessage();

    $("#formSystemMessageClear").submit(function() {
        var $form = $(this);
        $.ajax({
            url: $form.attr("action"),
            data: $form.serialize(),
            type: "POST",
            statusCode: {
                404: function() {
                    alert("404 not found");
                },
                500: function() {
                    alert("500 server error");
                }
            },
            success: function(result) {
                populateSystemMessage();

                $("#textSystemMessageUpdate").val("");
                $("#dateFromSystemMessage").val("");
                $("#dateToSystemMessage").val("");
            }
        });

        return false;
    });

    $("#formSystemMessageUpdate").submit(function() {
        var $form = $(this);
        $.ajax({
            url: $form.attr("action"),
            data: $form.serialize(),
            type: "POST",
            statusCode: {
                404: function() {
                    alert("404 not found");
                },
                500: function() {
                    alert("500 server error");
                }
            },
            success: function(result) {
                populateSystemMessage();

                $("#textSystemMessageUpdate").val("");
                $("#dateFromSystemMessage").val("");
                $("#dateToSystemMessage").val("");
            }
        });

        return false;
    });

    $("#formMaintenanceMessageClear").submit(function() {
        var $form = $(this);
        $.ajax({
            url: $form.attr("action"),
            data: $form.serialize(),
            type: "POST",
            statusCode: {
                404: function() {
                    alert("404 not found");
                },
                500: function() {
                    alert("500 server error");
                }
            },
            success: function(result) {
                populateMaintenanceMessage();

                $("#textMaintenanceMessageUpdate").val("");
                $("#dateFromMaintenanceMessage").val("");
                $("#dateToMaintenanceMessage").val("");
            }
        });

        return false;
    });

    $("#formMaintenanceMessageUpdate").submit(function() {
        var $form = $(this);
        $.ajax({
            url: $form.attr("action"),
            data: $form.serialize(),
            type: "POST",
            statusCode: {
                404: function() {
                    alert("404 not found");
                },
                500: function() {
                    alert("500 server error");
                }
            },
            success: function(result) {
            	populateMaintenanceMessage();
				$("#textMaintenanceMessageUpdate").val("");
                $("#dateFromMaintenanceMessage").val("");
                $("#dateToMaintenanceMessage").val("");
				window.localStorage.setItem('dismiss', "0");
				$("#dismissReadButton").show();
            }
        });

        return false;
    });

    function populateSystemMessage() {
        $.ajax({
            type: "GET",
            url: globalBaseURL + "/messages/system",
            contentType: 'application/json; charset=utf-8',
            success: function(result) {
                if (result.message == null) {
                    $("#labelSystemMessageCurrent").text("There is no current system message set.");
                    $("#textSystemMessageCurrent").val("");
                    $("#labelSystemMessageValid").text("");
                } else {
                    $("#labelSystemMessageCurrent").text("Current Message");
                    $("#textSystemMessageCurrent").val(result.message);
                    $("#labelSystemMessageValid").text("Valid from " + $.format.date(result.startDateTime, 'MM/dd/yyyy hh:mm a') + " PST" + " to " + $.format.date(result.endDateTime, 'MM/dd/yyyy hh:mm a') + " PST" + ".");
                }
            },
            error: function(req, error, status) {
                window.alert("error: " + error + status);
            }
        });
    }

    function populateMaintenanceMessage() {
        $.ajax({
            type: "GET",
            url: globalBaseURL + "/messages/maintenance",
            contentType: 'application/json; charset=utf-8',
            success: function(result) {
                if (result.message == null) {
                    $("#labelMaintenanceMessageCurrent").text("There is no current maintenance message set.");
                    $("#textMaintenanceMessageCurrent").val("");
                    $("#labelMaintenanceMessageValid").text("");
                } else {
					var index = result.message.indexOf("N/A");
                    $("#dismissReadButton1").show();
					if(index > -1)
					{
						result.message = result.message.substring(0,index);
	                    $("#dismissReadButton1").hide();
					}
                    $("#labelMaintenanceMessageCurrent").text("Current Message");
                    $("#textMaintenanceMessageCurrent").val(result.message);
                    $("#labelMaintenanceMessageValid").text("Valid from " + $.format.date(result.startDateTime, 'MM/dd/yyyy hh:mm a') + " PST" + " to " + $.format.date(result.endDateTime, 'MM/dd/yyyy hh:mm a') + " PST" + ".");
                    $("#labelMaintenanceMessage").text(result.message);
                }
            },
            error: function(req, error, status) {
                window.alert("error: " + error + status);
            }
        });
    }
        
    $('#selectAllCheck').click(function(event) {  //on click 
        if(this.checked) {
            $('.checkBoxes').each(function() {
                this.checked = true;
            });
        } else {
            $('.checkBoxes').each(function() {
                this.checked = false;              
            });         
        }
    });
});