$(function() {
    $('#asOfDate').show();
    $('#asOfDateSymbolicValue').hide();
    $('#asOfDateSymbolicValue').css({ "visibility": "hidden" });
    
    $.validator.addMethod('date', function (value, element) {
        if (this.optional(element)) {
            return true;
        }
        var ok = true;
        try {
            $.datepicker.parseDate('mm/dd/yy', value);
        }
        catch (err) {
            ok = false;
        }
        return ok;
    });
    
    function populateOptionalParameters() {
    	var reportId = $("#reportId").val();
    	var templateId = $("#templateId").val();
        $.ajax({
            type: "GET",
            url: globalBaseURL + "/report/getOptionalParametersForReportTemplate/" + reportId + "/" + templateId,
            dataType: "json",
            contentType: 'application/json; charset=utf-8',
            beforeSend: function() {
                
            },
            complete: function() {
                
            },
            success: function(result) {
            	$("#optionalParameters").empty();
                $.each(result, function(i, parameter) {
                	var $divFormGroup = $("<div>", { class: "form-group" });
                	$divFormGroup.append($("<label>", { class: "col-sm-2 control-label" } ).append(parameter.displayName));
                	if(parameter.type == "char") {
                		if(parameter.name == 'exception_status') {
                			var $combo = $("<select/>", { id: parameter.name, name: parameter.name, type: "text", class: "pp-width-full", placeholder: "Exception status" });
                			$combo.append($("<option/>", { value: "Open", text: "Open" } ));
                			$combo.append($("<option/>", { value: "Closed", text: "Closed" } ));
                			$combo.attr("selectedIndex", -1);
                			$divFormGroup.append($("<div>", { class: "col-sm-2" }).append($combo));	
                		} else {
                			$divFormGroup.append($("<div>", { class: "col-sm-2" }).append($("<input>", { id: parameter.name, name: parameter.name, type: "text", class: "form-control" }).val(parameter.valueChar)));
                		}
                	} else if(parameter.type == "date") {
                		$divFormGroup.pp_datepicker({ idPrefix: parameter.name, 
                										namePrefix: parameter.name, 
                										operator: parameter.operator, 
                										dateFrom: parameter.valueDateFrom,
                										dateTo: parameter.valueDateTo,
                										dateSymbolicFrom: parameter.valueDateFromSymbolic, 
                										dateSymbolicTo: parameter.valueDateToSymbolic,
                										isSymbolic: parameter.isValueDateSymbolic });
                	}
                	
                	$("#optionalParameters").append($divFormGroup);
                });

                setupCompanyCheckboxes();
                //populateAccounts();
                
                $("#reportShowForm :input").change(function() {
                	$("#reportShowForm").data('changed', true);
            	});
            },
            error: function(req, error, status) {
                window.alert("error: " + error + status);
            }
        });        
    }
    
    function populateCompanies() {    	
        var checked = [];
        checked.push(-1);
        $("input[name='bankIds']:checked").each(function() {
            checked.push(parseInt($(this).val()));
        });

        var jsonChecked = JSON.stringify(checked);
        var reportId = $("#reportId").val();
        $.ajax({
            type: "POST",
            url: globalBaseURL + "/report/getCompaniesForReport/" + reportId,
            data: jsonChecked,
            dataType: "json",
            contentType: 'application/json; charset=utf-8',
            beforeSend: function() {
                $("#tableCompanies tbody tr").remove();
                $("#tableAccounts tbody tr").remove();
                $('#tableCompanies').append('<tr><td></td><td><div class="pp-ajax-loader">Loading companies, please wait...</div></td></tr>');
            },
            complete: function() {
                //$("#tableCompanies tbody tr").remove();
            },
            success: function(result) {
            	////WALPP-403 - sorting on company name
            	sortByName(result,"companyName");
                $("#tableCompanies tbody tr").remove();
                $("#tableAccounts tbody tr").remove();
                $('#selectAllCompanies').prop('checked', false);
                $('#selectAllAccounts').prop('checked', false);
                
                var checkedCount = 0;
                $.each(result, function(i, company) {
                	var checkedText = "";
                	if(company.selected == true) {
                		checkedText = " checked";
                		checkedCount++;
                	}
                	
                    $('#tableCompanies').append('<tr><td><input type="checkbox" name="companyIds" value="' + company.id + '"' + checkedText + '/></td><td>' + company.companyName + '</td></tr>');
                });

                setupCompanyCheckboxes();
                
                if(checkedCount > 0) {
                	populateAccounts();
                } else {
                	$("#reportShowForm :checkbox").change(function() {
                    	$("#reportShowForm").data('changed', true);
                	});
                }
            },
            error: function(req, error, status) {
                window.alert("error: " + error + status);
            }
        });        
    }

    function populateAccounts() {
        var checked = [];
        checked.push(-1);
        $("input[name='companyIds']:checked").each(function() {
            checked.push(parseInt($(this).val()));
        });

        var jsonChecked = JSON.stringify(checked);

        var reportId = $("#reportId").val();
        
        $.ajax({
            type: "POST",
            url: globalBaseURL + "/report/getAccountsForReport/" + reportId,
            data: jsonChecked,
            dataType: "json",
            contentType: 'application/json; charset=utf-8',
            beforeSend: function() {
                $("#tableAccounts tbody tr").remove();
                $('#tableAccounts').append('<tr><td></td><td colspan="2"><div class="pp-ajax-loader">Loading accounts, please wait...</div></td></tr>');
            },
            complete: function() {
                //$("#tableAccounts tbody tr").remove();
            },
            success: function(result) {
            	//WALPP-403 - sorting on account name
            	sortByName(result,"accountName");
                $("#tableAccounts tbody tr").remove();
                $('#selectAllAccounts').prop('checked', false);
                $.each(result, function(i, account) {
                	var checkedText = "";
                	if(account.selected == true) {
                		checkedText = " checked";
                	}
                	
                    $('#tableAccounts').append('<tr><td><input type="checkbox" name="accountIds" value="' + account.id + '"' + checkedText  + '/></td><td>' + account.accountNumber + '</td><td>' + account.accountName + '</td></tr>');
                });
                
                $("#reportShowForm :checkbox").change(function() {
                	$("#reportShowForm").data('changed', true);
            	});
            },
            error: function(req, error, status) {
            	$('#tableAccounts').append('<tr><td></td><td><p>Error loading accounts.</p></td></tr>');
            }
        }); 
    }

    function setupBankCheckboxes() {
        $('#tableBanks').find('td input:checkbox').off('click');
        $('#tableBanks').find('td input:checkbox').click(function(e) {
            populateCompanies();
        });

        $('#selectAllBanks').off('click');
        $('#selectAllBanks').click(function(e) {
            $('#tableBanks').find('td input:checkbox').prop('checked', this.checked);
            populateCompanies();
        });
    }

    function setupCompanyCheckboxes() {
        $('#tableCompanies').find('td input:checkbox').off('click');
        $('#tableCompanies').find('td input:checkbox').click(function(e) {
            populateAccounts();
        });

        $('#selectAllCompanies').off('click');
        $('#selectAllCompanies').click(function(e) {
            $('#tableCompanies').find('td input:checkbox').prop('checked', this.checked);
            populateAccounts();
        });
    }

    function setupAsOfDate(asOfDateIsSymbolic) {
    	if (asOfDateIsSymbolic == true) {
            $('#asOfDate').hide();
            $('#asOfDateSymbolicValue').show();
            $('#asOfDateSymbolicValue').css({"visibility": "visible"});
            $('#asOfDateIsSymbolic').prop('checked', true);
        } else {
            $('#asOfDate').show();
            $('#asOfDateSymbolicValue').hide();
            $('#asOfDateSymbolicValue').css({"visibility": "hidden"});
            $('#asOfDateIsSymbolic').prop('checked', false);
        }
    }
    
    $('#selectAllAccounts').click(function(e) {
        $('#tableAccounts').find('td input:checkbox').prop('checked', this.checked);
    });
    
    $("#reportShowForm").validate({
    	rules: {
            reportName: {
                required: true,
                minlength: 3,
                maxlength: 50
            },
            asOfDate: {
            	required: true,
            	date: true
            },
            outputFormat: {
            	required: true
            },
            reportType: {
                required: true
            },
            accountIds: {
                required: true,
                minlength: 1
            },
            companyIds: {
                required: true,
                minlength: 1
            }
        },
        messages: {
            reportName: {
                required: "Please enter a report name.",
                minlength: "The report name must be at least 3 characters long.",
                maxlength: "The report name can not be longer than 50 characters."
            },
	        asOfDate: {
	        	required: "Please enter an as of date.",
	        	date: "Please enter a valid date (example: 11/28/2005)."
	        },
	        outputFormat: {
            	required: "Please select an output format."
            },
            reportType: {
                required: "Output format is required.",
            },
            accountIds: {
            	required: "At least one account needs to be selected."
            },
            companyIds: {
            	required: "At least one company needs to be selected."
            }
        },
    	submitHandler: function(e) {
	    	var $form = $("#reportShowForm");
	        var reportId = $("#reportId").val();
	        
        	$('#reportShowModal').modal('hide');
        	
        	if($form.data('changed') == true) {
		        $.ajax({
		            // url can be obtained via the form action attribute passed to the JSP.
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
		            beforeSend: function() {
                        $.blockUI({
                            message: '<div><img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif"/>Saving report...</div>',
                            blockMsgClass: 'alert',
                            css: { padding: '10px', color: '#000', border: '1px solid #006B87', 'border-radius': '5px', '-moz-border-radius': '5px', '-webkit-border-radius': '5px' }
                        });
                    },
                    complete: function() {
                    	$.unblockUI();
                    },
		            success: function(report) {
		            	var dateCreated = new Date(report.auditInfo.dateCreated);
		                var tr = $('<tr id=reportListTableRow' + report.id + '/>');
		                if (reportId != -1) {
		                    tr = $("#reportListTableRow" + reportId);
		                    tr.empty();
		                } else {
		                    $("#reportListTable tbody").prepend(tr);
		                }
		
		                tr.append('<td><div class="pp-sprite pp-sprite-gold-star"></div></td>');
		                tr.append('<td>' + report.name + '</td>');
		                tr.append('<td>' + report.reportTemplate.reportTemplateType.name + '</td>');
		                tr.append('<td>' + report.reportTemplate.name + '</td>');
		                tr.append('<td>' + $.format.date(dateCreated, 'MM/dd/yyyy') + '</td>');
		                
		                var iconRow = '';
		                iconRow += '<td><div class="pp-width-full" style="width: 75px; float: left;">';
		                iconRow += '<div style="width: 26px; height: 24px; float: left; margin-right: 4px;"><button class="delete-reportDialog pp-sprite-delete btn" data-toggle="modal" data-report-name="' + report.name + '" data-report-id="' + report.id + '"></button></div>';
		        		iconRow += '<div style="float: left; margin-right: 4px;"><button class="open-reportDialog pp-sprite-run btn" data-toggle="modal" data-report-name="' + report.name + '" data-report-id="' + report.id + '" data-template-name="' + report.reportTemplate.name + '" data-template-id="' + report.reportTemplate.id + '" data-output-format="' + report.outputFormat + '" data-as-of-date="' + $.format.date(report.asOfDate, "MM/dd/yyyy") + '" data-as-of-date-is-symbolic="' + report.asOfDateIsSymbolic + '" data-as-of-date-symbolic-value="' + report.asOfDateSymbolicValue + '" href="#reportShowModal"></button></div>';
		        		iconRow += '</div></td>';
		        		
		        		tr.append(iconRow);
		                
		                window.location.href = globalBaseURL + "/report/run/" + report.id;
		            }
		        });
        	} else {
        		window.location.href = globalBaseURL + "/report/run/" + reportId;
        	}
        	
	        return false;
	    }
    });
    
    $(document).on("click", ".open-reportDialog", function(e) {
       	$("#reportShowForm").data('changed', false);

    	var reportName = $(this).data('report-name');
        var reportId = $(this).data('report-id');
        var templateName = $(this).data('template-name');
        var templateId = $(this).data('template-id');
        var outputFormat = $(this).data('output-format');
        var asOfDate = $(this).data('as-of-date');
        var asOfDateSymbolicValue = $(this).data('as-of-date-symbolic-value');
        var asOfDateIsSymbolic = $(this).data('as-of-date-is-symbolic');
        setupAsOfDate(asOfDateIsSymbolic);
        //setupAsOfDateSymbolicValue(asOfDateSymbolicValue);
        $('#asOfDateSymbolicValue').populate_symbolic_values( { selectedDate: asOfDateSymbolicValue } );
        
        $("#asOfDate").datepicker();
        $("#asOfDateDiv").click(function() {
    		$("#asOfDate").focus();
    	});
        
        $("#reportId").val(reportId);
        $("#templateId").val(templateId);
        $("#reportName").val(reportName);
        $("#templateName").val(templateName);
        $("#templateId").val(templateId);
        $("#asOfDate").val(asOfDate);
        $("#asOfDateSymbolicValue").val(asOfDateSymbolicValue);
        $("#asOfDateIsSymbolic").prop('checked', asOfDateIsSymbolic);
        $("#outputFormat").val(outputFormat);
                
        var reportShowModal = $("#reportShowModal");
        reportShowModal.modal('show');
        
        //reportShowModal.css('margin-top', ($(window).height() - reportShowModal.height()) / 2 - parseInt(reportShowModal.css('padding-top')));
        
        populateCompanies();
        //populateAccounts();
        populateOptionalParameters();
        
        $("#reportShowForm :input").change(function() {
        	$("#reportShowForm").data('changed', true);
    	});
    });

    $(document).on("click", ".delete-reportDialog", function(e) {
        var _self = $(this);
    	var reportName = $(this).data('report-name');
        var reportId = $(this).data('report-id');

        $("#deleteReportName").text(reportName);
        $("#deleteReportId").val(reportId);
        $("#reportButtonDelete").click(function() {
            $.ajax({
                type: "POST",
                url: globalBaseURL + "/report/deleteById",
                data: {"deleteReportId": reportId},
                success: function(result) {
                    if (result == 1) {
                    	_self.closest("tr").remove();
                        $('#reportDeleteModal').modal('hide');
                    }
                },
                error: function(req, error, status) {
                    window.alert("error: " + error + status);
                }
            });
        });

        $("#reportDeleteModal").modal('show');
    });
    
    $('#asOfDateIsSymbolic').click(function() {
    	setupAsOfDate($('#asOfDateIsSymbolic').is(':checked'));
    });
});