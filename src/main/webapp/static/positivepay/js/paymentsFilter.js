$(document).ready(function() {
	//Initialize date fields
	
	$("#exCreateDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#exCreateDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#exCreateDateFrom").datepicker().focus();
	});
	$("#exCreateDateTo").datepicker({
        showAnim: "clip"
    });
	$('#exCreateDateToDiv').click(function(e) {
		e.preventDefault();
		$("#exCreateDateTo").datepicker().focus();
	});
	

	$("#issueDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#issueDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#issueDateFrom").datepicker().focus();
	});
	$("#issueDateTo").datepicker({
        showAnim: "clip"
    });
	$('#issueDateToDiv').click(function(e) {
		e.preventDefault();
		$("#issueDateTo").datepicker().focus();
	});	
	
	
	$("#paidDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#paidDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#paidDateFrom").datepicker().focus();
	});
	$("#paidDateTo").datepicker({
        showAnim: "clip"
    });
	$('#paidDateToDiv').click(function(e) {
		e.preventDefault();
		$("#paidDateTo").datepicker().focus();
	});	
	

	$("#stopDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#stopDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#stopDateFrom").datepicker().focus();
	});
	$("#stopDateTo").datepicker({
        showAnim: "clip"
    });
	$('#stopDateToDiv').click(function(e) {
		e.preventDefault();
		$("#stopDateTo").datepicker().focus();
	});	
	
	
	$("#stopExpDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#stopExpDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#stopExpDateFrom").datepicker().focus();
	});
	$("#stopExpDateTo").datepicker({
        showAnim: "clip"
    });
	$('#stopExpDateToDiv').click(function(e) {
		e.preventDefault();
		$("#stopExpDateTo").datepicker().focus();
	});
	
	
	$("#voidDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#voidDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#voidDateFrom").datepicker().focus();
	});
	$("#voidDateTo").datepicker({
        showAnim: "clip"
    });
	$('#voidDateToDiv').click(function(e) {
		e.preventDefault();
		$("#voidDateTo").datepicker().focus();
	});
	
	
	$("#itemDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#itemDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#itemDateFrom").datepicker().focus();
	});
	$("#itemDateTo").datepicker({
        showAnim: "clip"
    });
	$('#itemDateToDiv').click(function(e) {
		e.preventDefault();
		$("#itemDateTo").datepicker().focus();
	});
	
	
	$("#createDateFrom").datepicker({
        showAnim: "clip"
    });
	$('#createDateFromDiv').click(function(e) {
		e.preventDefault();
		$("#createDateFrom").datepicker().focus();
	});
	$("#createDateTo").datepicker({
        showAnim: "clip"
    });
	$('#createDateToDiv').click(function(e) {
		e.preventDefault();
		$("#createDateTo").datepicker().focus();
	});	
});

var searchParametersMap = new Object();

var paymentsAndItemsController = function($scope, $http) {
	$scope.bArray = [];
	$scope.cArray = [];
	$scope.bankNames = "";
	$scope.bankIds = [];
	$scope.compIds = [];
	$scope.compNames = "";
	$scope.fiteredCompany = [];
	$scope.accntIds = [];
	$scope.accntNames = "";
	$scope.paymentStatus = "";
	$scope.itemTypes = "";
	$scope.createdMethods = "";
	$scope.matchStatusName = "";
	$scope.exStatusName = "";
	$scope.exTypes = "";
	$scope.exStatus = "";
	$scope.resActions = "";
	$scope.allResActions = ""
    $scope.companies = "Select Company";
    $scope.banks = "";
    $scope.accounts = "";
	$scope.reset = false;
    $scope.allpaymentStatus = "";
    $scope.allitemTypes = "";
    $scope.allcreateMethods = "";
    $scope.allmatchStatus = "";
    $scope.allexceptionTypes = "";
    $scope.allexceptionStatus = "";
    $scope.resolutionActions = "";
	$scope.isIterateMap = false;
    $scope.paymentFilters = {};
	$scope.accountArray = [];
    $scope.currentFilter = {searchParametersMap : {}, filterName  : '', filterDescription : ''}
    $scope.baseUrl = globalBaseURL;
    
	/*$scope.getCompany = function() {
        $http({
            url: $scope.baseUrl + "/job/company",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.companies = response;
			$scope.fiteredCompany = [];
            for (var key in $scope.companies) {
                  $scope.fiteredCompany.push($scope.companies[key]);
            }
        }).error(function(error) {
            $scope.error = error;
        });
    };*/
	
	$('#comp').multiselect({
		nonSelectedText: 'Select company',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var comps = $('#comp option:selected');
			var compArray = new Array();
			var i = 0;
			$(comps).each(function(index, comp) {
				$scope.compIds.push([$(this).val()]);
				compArray[i] = $(this).val();
				$scope.cArray = compArray;
				$scope.compNames = $(this).text() + "|" + $scope.compNames;
				i++;
			});
			if($scope.compIds.length > 0) {
				//$scope.getCompaniesByBank(bankArray);
				$scope.getAccountsByCompany(compArray);
			}			
			
			$scope.len = $scope.compNames.length;
			$scope.idx = $scope.compNames.lastIndexOf("|");
			$scope.compNames = $scope.compNames.substring(0, ($scope.idx));
 		}
	});
	$('#accnt').multiselect({
		nonSelectedText: 'Select accounts',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var accnts = $('#accnt option:selected');
			$(accnts).each(function(index, comp) {
				$scope.accntIds.push([$(this).val()]);
				$scope.accntNames = $(this).text() + "|" + $scope.accntNames;
			});
		 
			$scope.len = $scope.accntNames.length;
			$scope.idx = $scope.accntNames.lastIndexOf("|");
			$scope.accntNames = $scope.accntNames.substring(0, ($scope.idx));		 
 		}
	});
    $('#pymntStatus').multiselect({
		nonSelectedText: 'Select payment status',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var pymntStatus = $('#pymntStatus option:selected');
			$(pymntStatus).each(function(index, comp) {
				$scope.paymentStatus = $(this).val() + "|" + $scope.paymentStatus;
			});
			
			$scope.len = $scope.paymentStatus.length;
			$scope.idx = $scope.paymentStatus.lastIndexOf("|");
			$scope.paymentStatus = $scope.paymentStatus.substring(0, ($scope.idx));
 		}
	});
    
    $('#itemType').multiselect({
		nonSelectedText: 'Select item type',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var itmType = $('#itemType option:selected');
			$(itmType).each(function(index, comp) {
				$scope.itemTypes = $(this).text() + "|" + $scope.itemTypes;
			});
			
			$scope.len = $scope.itemTypes.length;
			$scope.idx = $scope.itemTypes.lastIndexOf("|");
			$scope.itemTypes = $scope.itemTypes.substring(0, ($scope.idx));
 		}
	});
    
    $('#resAction').multiselect({
		nonSelectedText: 'Select resolution actions',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var resAction = $('#resAction option:selected');
			$(resAction).each(function(index, comp) {
				$scope.resActions = $(this).val() + "|" + $scope.resActions;
			});
		    
			$scope.len = $scope.resActions.length;
		    $scope.idx = $scope.resActions.lastIndexOf("|");
		    $scope.resActions = $scope.resActions.substring(0, ($scope.idx));
 		}
	});
    
    $('#createMethod').multiselect({
		nonSelectedText: 'Select create method',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var creatMethod = $('#createMethod option:selected');
			$(creatMethod).each(function(index, comp){
				$scope.createdMethods = $(this).val() + "|" + $scope.createdMethods;
			});
			
			$scope.len = $scope.createdMethods.length;
			$scope.idx = $scope.createdMethods.lastIndexOf("|");
			$scope.createdMethods = $scope.createdMethods.substring(0, ($scope.idx));
 		}
	});
    
	$('#matchStatus').multiselect({
		nonSelectedText: 'Select match status',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var matchStatus = $('#matchStatus option:selected');
			$(matchStatus).each(function(index, comp){
				$scope.matchStatusName = $(this).text() + "|" + $scope.matchStatusName;
			});
			
			$scope.len = $scope.matchStatusName.length;
			$scope.idx = $scope.matchStatusName.lastIndexOf("|");
			$scope.matchStatusName = $scope.matchStatusName.substring(0, ($scope.idx));
 		}	
	});
	
	$('#exStatus').multiselect({
		nonSelectedText: 'Select exception status',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var exStatus = $('#exStatus option:selected');
			$(exStatus).each(function(index, comp){
				$scope.exStatusName = $(this).text() + "|" + $scope.exStatusName;
			});
			
			$scope.len = $scope.exStatusName.length;
			$scope.idx = $scope.exStatusName.lastIndexOf("|");
			$scope.exStatusName = $scope.exStatusName.substring(0, ($scope.idx));
 		}
	});
	
	$('#exType').multiselect({
		nonSelectedText: 'Select exception types',
		buttonWidth: '300px',
		numberDisplayed: 3,
		selectAllText: true,
		onDropdownHide: function(element, checked) {
			var exType = $('#exType option:selected');
			$(exType).each(function(index, comp){
				$scope.exTypes = $(this).text() + "|" + $scope.exTypes;
			});
		 
			$scope.len = $scope.exTypes.length;
			$scope.idx = $scope.exTypes.lastIndexOf("|");
			$scope.exTypes = $scope.exTypes.substring(0, ($scope.idx));
 		}
	});
	
	
	
    /**
     * All the global variable declarations
     */

    /**
     * Needed for radio's to behave properly
     */
    $scope.options = {
        value: '-'
    };
    
    $scope.$on('$viewContentLoaded', function() {
    	alert("here this time, angular!!");
    	});

    /**
     * Fetch the Bank List for displaying bank names in Bank Select combo
     * 
     * Bank combo-box
     */
    $scope.getBank = function() {
        $http({
            url: $scope.baseUrl + "/user/bank",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.banks = response;
			sortByName($scope.banks,"name");
			var bankArray = new Array();
			$('#bank').multiselect({
				nonSelectedText: 'Select Bank',
				buttonWidth: '300px',
				numberDisplayed: 3,
				selectAllText: true,
				onDropdownHide:function(element, checked) {
					bankArray = new Array();
					var banks = $('#bank option:selected');					
					var i = 0;					
					$(banks).each(function(index, bank){
						if($(this).text()!=null || $(this).text().length > 0) {
							$scope.bankIds.push([$(this).val()]);
							//bankArray.push([$(this).val()]);
							bankArray[i] = $(this).val();
							$scope.bArray = bankArray;							
							$scope.bankNames = $(this).text() + "|" + $scope.bankNames;	
							i++;
						} else {
							return false;
						}
					});
					
					$scope.len = $scope.bankNames.length;
				    $scope.idx = $scope.bankNames.lastIndexOf("|");
				    $scope.bankNames = $scope.bankNames.substring(0,($scope.idx));
				     
					if($scope.bankIds.length > 0)
						$scope.getCompaniesByBank(bankArray);
				}
			});			
		
			$.each($scope.banks, function(id, name)
			{
				$('#bank').append('<option value="' + name.id + '">' + name.name + '</option>').multiselect('rebuild');
			});
			
		}).error(function(error) {
            $scope.error = error;
        });
    };
    
    /**
     * It's a comparator to sort bankNames/companyNames
     * @param array
     * @param key
     * @returns
     */
	/*function sortByName(array, key) {
    return array.sort(function(a, b) {
        var x = a[key]; var y = b[key];
        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    });
    }*/
	
    $scope.populateFields = function() {
    	var paymentsData = window.localStorage.getItem("showpaymentsdata");
    	if(paymentsData == "true") {
    		var lookFor = window.localStorage.getItem("lookfor");
    		if(lookFor == 'Payments') {
    			$('.itemListCls').hide();
    			$('.paymentListCls').show();
    		} else {
    			$('.itemListCls').show();
    			$('.paymentListCls').hide();
    		}
    		window.localStorage.setItem("showpaymentsdata", "false");
    		$("#lookFor").val(lookFor);
    		$("#filterTbl").show();
    		var selectedFilterId = window.localStorage.getItem("selectedFilterVal");
    		var filterJson = window.localStorage.getItem("paymentsFilter");
    		if($scope.paymentFilters != [] && (filterJson !=null && filterJson != "")) {
    			if(selectedFilterId == "") {
    				$scope.selectedfilter = "";
    			} else {
	    			angular.forEach($scope.paymentFilters, function(value, key) {
	    				if(value.id == selectedFilterId) {
	    					$scope.selectedfilter = value;
	    				}
	    			});
    			}
				var filterObj = angular.fromJson(filterJson);
				$scope.applyMapToFields(filterObj.searchParametersMap);
				$("#bank").multiselect('refresh');
				$("#comp").multiselect('refresh');
				$("#accnt").multiselect('refresh');
				$("#accnt").multiselect('refresh');
    	} else {
    		$scope.addSearchForField();
    	}
    }
   }
    
    $scope.applyFilterToTable = function(selectedfilter) {
    	if($scope.selectedfilter == null) {
    		$scope.clearFields(true);
    		return;
    	}
    	if($("#lookFor").val() == "") {
    		$("#lookFor").notify("Please select payments or items before selecting filter!!", "error");
    		$scope.selectedfilter = null;
    		return;
    	}
    	$("#ajax-loader-filter").html('<img src="' + globalBaseURL + '/static/positivepay/images/ajax/ajax-loader.gif" /> <b>Loading values for selected filter..<b>');
    	if($scope.paymentFilters != []) {
    		window.localStorage.setItem("selectedFilterVal", $scope.selectedfilter.id);
    		$scope.currentFilter = $scope.selectedfilter;
    		var searchMap = $scope.currentFilter.searchParametersMap;
    		$scope.clearFields(false);
    		$scope.applyMapToFields(searchMap);
    	}
		  /*$('#bank').val('');
		  $('#comp').val('');
		  $('#bank').html("");
		  $('#comp').html("");*/
		$("#bank").multiselect('refresh');
		$("#comp").multiselect('refresh');
		$("#accnt").multiselect('refresh');
		$("#accnt").multiselect('refresh');
		 //$("#accnt").empty();
		 //$("#pymntStatus").empty();
		 //$("#matchStatus").empty();
		 //$("#exType").empty();
		 //$("#exStatus").empty();
		  // $("#bank option:first").attr("selected", true);
		  // $("#comp option:first").attr("selected", true);
	
    	$("#ajax-loader-filter").empty();
    };

    $scope.applyMapToFields = function(searchMap) {
        if($scope.currentFilter == []) {
               $scope.currentFilter = {searchParametersMap : {}, filterName  : '', filterDescription : '', searchCriteria : ''};
        }
        $scope.currentFilter.searchParametersMap = {};

        angular.forEach(searchMap, function(value, key) {
                      if(key == 'Bank') {
                            //$scope.showBankForFilter(value);
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                    	  $("#bankSearchOption option:contains(" + relationalOperator + ")").attr("selected", true);
                      }
                      var parameterCsv = value.parameterCsv;
                      $scope.bankNames = parameterCsv;
                      if(parameterCsv != null) {
                             var bankArray = new Array();
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");
                                   $("#bank").val([]);
                                   $("#bank").val("");
                                   $("#bank").multiselect("refresh");
                                   for (var i=0; i <selectedValues.length;i++ ) {
                                                 $scope.currentFilter.searchParametersMap['Bank'] = value;
                                                 for(var j=0; j < $scope.banks.length;j++)
                                                 {
                                                        if($scope.banks[j].name == selectedValues[i])
                                                        {
                                                               bankArray[i] = $scope.banks[j].id;
                                                               $("#bank").find("option[value="+$scope.banks[j].id+"]").prop("selected", "selected");
                                                               $("#bank").multiselect('refresh');
                                                        }
                                                 }
                                   }
                                   $scope.bArray = bankArray;
                                   //load companies again
                                   $scope.getCompaniesByBankSynchronous(bankArray);
                      }
                      }
                      if(key == 'Customer' ) {
                            //$scope.showComapnyForFilter(value);
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#compSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }

                      var parameterCsv = value.parameterCsv;
                      $scope.compNames = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                                   var compArray = new Array();
                             selectedValues = parameterCsv.split("|");
                                   $("#comp").val("");
                                   $("#comp").multiselect("refresh");
                                   $("#accnt").val("");
                                   $("#accnt").multiselect("refresh");
                                   for (var i in selectedValues) {
                                          $scope.currentFilter.searchParametersMap['Customer'] = value;
                                          for(var j in $scope.fiteredCompany)
                                          {
                                                 if($scope.fiteredCompany[j].name == selectedValues[i])
                                                 {
                                                        compArray[i] = $scope.fiteredCompany[j].id;
                                                        //$("#comp option:contains("+selectedValues[i]+")").attr("selected", true);
                                                        $("#comp").find("option[value="+$scope.fiteredCompany[j].id+"]").prop("selected", "selected");
                                                        $("#comp").multiselect('refresh');
                                                 }
                                          }
                                   }
                                   if(!$scope.reset)
                                   {
                                          $("#accnt").val([]);
                                          $("#accnt").val("");
                                          $("#accnt").multiselect("refresh");
                                          $("#accnt").empty();
                                          $scope.getAccountsByCompanySynchronous(compArray,$scope.bArray);
                                          for(var ac in $scope.accountArray)
                                          {
                                                 $("#accnt option:contains("+$scope.accountArray[ac]+")").attr("selected", true);
                                                 $("#accnt").multiselect('refresh');
                                          }
                                          $scope.reset = false;
                                   }
                                   else
                                   {
                                          $("#accnt").val([]);
                                          $("#accnt").val("");
                                          $("#accnt").multiselect("refresh");
                                   }
                                   $scope.reset = false;

                      }
                      }
                      if(key == 'Account Number') {
                            //alert('accnt key');
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#accntSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }

                      var parameterCsv = value.parameterCsv;
                      $scope.accntNames = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");

                                for (var i in selectedValues) {
                                          $scope.currentFilter.searchParametersMap['Account Number'] = value;
                                          $scope.accountArray[i] = selectedValues[i];
                                   }
                                
                                for(var ac in $scope.accountArray)
                                {
                                       $("#accnt option:contains("+$scope.accountArray[ac]+")").attr("selected", true);
                                       $("#accnt").multiselect('refresh');
                                }

                                   if(selectedValues == "")
                                   {
                                          $("#accnt").val([]);
                                          $("#accnt").val("");
                                          $("#accnt").multiselect("refresh");
                                          $scope.reset = true;
                                   }
                                   }
                      //}

                      }
                      if(key == 'Payment Status') {
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#pymntStatusSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }
                            //alert('payment status');
                            $("#pymntStatus").val([]);
                            $("#pymntStatus").val("");
                            $("#pymntStatus").multiselect("refresh");
                      var parameterCsv = value.parameterCsv;
                      $scope.paymentStatus = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");
                                   for (var i in selectedValues) {
                                     $scope.currentFilter.searchParametersMap['Payment Status'] = value;
                                     //$("#pymntStatus option:contains("+selectedValues[i]+")").attr("selected", true);
                                     $("#pymntStatus").find("option[value=\""+selectedValues[i]+"\"]").prop("selected", "selected");
                                      $("#pymntStatus").multiselect('refresh');
                                   }

                                   if(selectedValues == "")
                                   {
                                          $("#pymntStatus").val([]);
                                          $("#pymntStatus").val("");
                                          $("#pymntStatus").multiselect("refresh");
                                   }

                                   }
                      //}
                      }
                      if(key == 'Item Type') {
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#itemTypeSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }
                            //alert('payment status');
                            $("#itemType").val([]);
                            $("#itemType").val("");
                            $("#itemType").multiselect("refresh");
                      var parameterCsv = value.parameterCsv;
                      $scope.itemTypes = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");
                                   for (var i in selectedValues) {
                                     $scope.currentFilter.searchParametersMap['Item Type'] = value;
                                     $("#itemType").find("option[value="+selectedValues[i]+"]").prop("selected", "selected");
                                     //$("#pymntStatus").find("option[value="+selectedValues[i]+"]").prop("selected", "selected");
                                      $("#itemType").multiselect('refresh');
                                   }

                                   if(selectedValues == "")
                                   {
                                          $("#itemType").val([]);
                                          $("#itemType").val("");
                                          $("#itemType").multiselect("refresh");
                                   }

                                   }
                      //}
                      }
                      
                      if(key == 'Resolution Action') {
                          var relationalOperator = value.relationalOperator;
                          if(relationalOperator != null) {
                                 $("#resActionSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                          }
                                //alert('payment status');
                                $("#resAction").val([]);
                                $("#resAction").val("");
                                $("#resAction").multiselect("refresh");
                          var parameterCsv = value.parameterCsv;
                          $scope.resActions = parameterCsv;
                          if(parameterCsv != null) {
                                 var selectedValues = new Array();
                                 selectedValues = parameterCsv.split("|");
                                       for (var i in selectedValues) {
                                         $scope.currentFilter.searchParametersMap['Resolution Action'] = value;
                                         $("#resAction").find("option[value="+selectedValues[i]+"]").prop("selected", "selected");
                                         //$("#pymntStatus").find("option[value="+selectedValues[i]+"]").prop("selected", "selected");
                                          $("#resAction").multiselect('refresh');
                                       }

                                       if(selectedValues == "")
                                       {
                                              $("#resAction").val([]);
                                              $("#resAction").val("");
                                              $("#resAction").multiselect("refresh");
                                       }

                                       }
                          //}
                          }
                      if(key == 'Created Method') {
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#createMethodSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }
                            //alert('payment status');
                            $("#createMethod").val([]);
                            $("#createMethod").val("");
                            $("#createMethod").multiselect("refresh");
                      var parameterCsv = value.parameterCsv;
                      $scope.createdMethods = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");
                                   for (var i in selectedValues) {
                                     $scope.currentFilter.searchParametersMap['Created Method'] = value;
                                     $("#createMethod").find("option[value="+selectedValues[i]+"]").prop("selected", "selected");
                                     //$("#pymntStatus").find("option[value="+selectedValues[i]+"]").prop("selected", "selected");
                                      $("#createMethod").multiselect('refresh');
                                   }

                                   if(selectedValues == "")
                                   {
                                          $("#createMethod").val([]);
                                          $("#createMethod").val("");
                                          $("#createMethod").multiselect("refresh");
                                   }

                                   }
                      //}
                      }
                      if(key == 'Match Status') {
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#matchStatusSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }
                      var parameterCsv = value.parameterCsv;
                      $scope.matchStatusName = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");
                                          for (var i in selectedValues) {
                                          $scope.currentFilter.searchParametersMap['Match Status'] =  value;
                                          //$("#matchStatus option:contains("+selectedValues[i]+")").attr("selected", true);
                                          $("#matchStatus").find("option[value=\""+selectedValues[i]+"\"]").prop("selected", "selected");
                                          $("#matchStatus").multiselect('refresh');
                                   }
                                   if(selectedValues == "")
                                   {
                                          $("#matchStatus").val([]);
                                          $("#matchStatus").val("");
                                          $("#matchStatus").multiselect("refresh");
                                   }

                      }
                      }
                      if(key == 'Exception Type') {
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#exTypeSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }
                      var parameterCsv = value.parameterCsv;
                      $scope.exTypes = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");
                                   for (var i in selectedValues) {
                                          $scope.currentFilter.searchParametersMap['Exception Type'] = value;
                                          //$("#exType option:contains("+selectedValues[i]+")").attr("selected", true);
                                          $("#exType").find("option[value=\""+selectedValues[i]+"\"]").prop("selected", "selected");
                                          $("#exType").multiselect('refresh');
                                   }
                                   if(selectedValues == "")
                                   {
                                   $("#exType").val([]);
                                   $("#exType").val("");
                                   $("#exType").multiselect("refresh");

                                   }

                            }
                      }
                      if(key == 'Exception Status') {
                      var relationalOperator = value.relationalOperator;
                      if(relationalOperator != null) {
                             $("#exStatusSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                      }

                      var parameterCsv = value.parameterCsv;
                      $scope.exStatusName = parameterCsv;
                      if(parameterCsv != null) {
                             var selectedValues = new Array();
                             selectedValues = parameterCsv.split("|");
                                   for (var i in selectedValues) {
                                          $scope.currentFilter.searchParametersMap['Exception Status'] = value;
                                          //$("#exStatus option:contains("+selectedValues[i]+")").attr("selected", true);
                                          $("#exStatus").find("option[value="+selectedValues[i]+"]").prop("selected", "selected");
                                          $("#exStatus").multiselect('refresh');
                                   }
                               if(selectedValues == ""){
                                   $("#exStatus").val([]);
                             $("#exStatus").val("");
                                   $("#exStatus").multiselect("refresh");

                                   }

                      }
                      }

                      if(key == 'Void Amount') {
	                      var relationalOperator = value.relationalOperator;
	                      var parameterCsv = value.parameterCsv;
	                      if(relationalOperator != null && parameterCsv != null) {
	                             $("#voidAmtSearchOption option:contains("+relationalOperator+")").attr("selected", true);
	                             $scope.currentFilter.searchParametersMap['Void Amount'] = value;
	                             if(relationalOperator != 'is between') {
	                            	 $scope.voidAmt = parameterCsv;
	                             }else {
	                                 var selectedValues = new Array();
	                                 selectedValues = parameterCsv.split(",");
	                                 $scope.voidAmt = selectedValues[0];
	                                 $scope.voidAmtTo = selectedValues[1];
	                                 $("#voidAmtTo" ).removeClass("hidden");
	                          }
	                      }
                      }
                      if(key == 'Check Number') {
	                      var relationalOperator = value.relationalOperator;
	                      var parameterCsv = value.parameterCsv;
	                      if(relationalOperator != null && parameterCsv != null) {
	                             $("#snumSearchOption option:contains("+relationalOperator+")").attr("selected", true);
	                             $scope.currentFilter.searchParametersMap['Check Number'] = value;
	                             if(relationalOperator != 'is between') {
	                            	 $scope.snum = parameterCsv;
	                             }else {
	                                 var selectedValues = new Array();
	                                 selectedValues = parameterCsv.split(",");
	                                 $scope.snum = selectedValues[0];
	                                 $scope.snumTo = selectedValues[1];
	                                 $("#snumTo" ).removeClass("hidden");
	                          }
	                      }
                      }
                      if(key == 'Item Amount') {
	                      var relationalOperator = value.relationalOperator;
	                      var parameterCsv = value.parameterCsv;
	                      if(relationalOperator != null && parameterCsv != null) {
	                             $("#itemAmountSearchOption option:contains("+relationalOperator+")").attr("selected", true);
	                             $scope.currentFilter.searchParametersMap['Item Amount'] = value;
	                             if(relationalOperator != 'is between') {
	                            	 $scope.itemAmount = parameterCsv;
	                             }else {
	                                 var selectedValues = new Array();
	                                 selectedValues = parameterCsv.split(",");
	                                 $scope.itemAmount = selectedValues[0];
	                                 $scope.itemAmountTo = selectedValues[1];
	                                 $("#itemAmountTo" ).removeClass("hidden");
	                          }
	                      }
                      }
                      /*if(key == 'Payment Amount') {
                            $scope.payAmt = value;
                            $scope.currentFilter.searchParametersMap['Payment Amount'] = value;
                      }*/
                      /*if(key == 'Payment Date') {
                            $scope.payDate = value;
                            $scope.currentFilter.searchParametersMap['Payment Date'] = value;
                      }*/
                      if(key == 'Exception Create Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#exCreateDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Exception Create Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.exCreateDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                    selectedValues = parameterCsv.split(",");
                                    $scope.exCreateDateFrom = selectedValues[0];
                                    $scope.exCreateDateTo = selectedValues[1];
                                    $("#exCreateDateToDiv" ).removeClass("hidden");
                             }
                      }
                      }
                      if(key == 'Item Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#itemDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Item Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.itemDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                   selectedValues = parameterCsv.split(",");
                                    $scope.itemDateFrom = selectedValues[0];
                                    $scope.itemDateTo = selectedValues[1];
                                    $("#itemDateToDiv" ).removeClass("hidden");
                             }
                      }
                      }
                      if(key == 'Created Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#createDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Created Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.createDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                    selectedValues = parameterCsv.split(",");
                                    $scope.createDateFrom = selectedValues[0];
                                    $scope.createDateTo = selectedValues[1];
                                    $("#createDateToDiv" ).removeClass("hidden");
                             }
                      }
               }
                      if(key == 'Issued Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#issueDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Issued Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.issueDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                    selectedValues = parameterCsv.split(",");
                                    $scope.issueDateFrom = selectedValues[0];
                                    $scope.issueDateTo = selectedValues[1];
                                    $("#issueDateToDiv" ).removeClass("hidden");
                             }
                      }
                      }
                      if(key == 'Issued Amount') {
	                      var relationalOperator = value.relationalOperator;
	                      var parameterCsv = value.parameterCsv;
	                      if(relationalOperator != null && parameterCsv != null) {
	
	                             $("#issueAmtSearchOption option:contains("+relationalOperator+")").attr("selected", true);
	                             $scope.currentFilter.searchParametersMap['Issued Amount'] = value;
	                             if(relationalOperator != 'is between') {
	                            	 $scope.issueAmt = parameterCsv;
	                             }else {
	                                 var selectedValues = new Array();
	                                 selectedValues = parameterCsv.split(",");
	                                 $scope.issueAmt = selectedValues[0];
	                                 $scope.issueAmtTo = selectedValues[1];
	                                 $("#issueAmtTo" ).removeClass("hidden");
	                          }
	                      }
                      }
                      if(key == 'Paid Amount') {
	                      var relationalOperator = value.relationalOperator;
	                      var parameterCsv = value.parameterCsv;
	                      if(relationalOperator != null && parameterCsv != null) {
	
	                             $("#paidAmtSearchOption option:contains("+relationalOperator+")").attr("selected", true);
	                             $scope.currentFilter.searchParametersMap['Paid Amount'] = value;
	                             if(relationalOperator != 'is between') {
	                            	 $scope.paidAmt = parameterCsv;
	                             }else {
	                                 var selectedValues = new Array();
	                                 selectedValues = parameterCsv.split(",");
	                                 $scope.paidAmt = selectedValues[0];
	                                 $scope.paidAmtTo = selectedValues[1];
	                                 $("#paidAmtTo" ).removeClass("hidden");
	                          }
	                      }
                      }
                      if(key == 'Paid Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#paidDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Paid Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.paidDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                    selectedValues = parameterCsv.split(",");
                                    $scope.paidDateFrom = selectedValues[0];
                                    $scope.paidDateTo = selectedValues[1];
                                    $("#paidDateToDiv" ).removeClass("hidden");
                             }
                      }
                      }
                      if(key == 'Stop Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#stopDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Stop Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.stopDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                    selectedValues = parameterCsv.split(",");
                                    $scope.stopDateFrom = selectedValues[0];
                                    $scope.stopDateTo = selectedValues[1];
                                    $("#stopDateToDiv" ).removeClass("hidden");
                             }
                      }

                      }
                      if(key == 'Stop Expiration Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#stopExpDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Stop Expiration Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.stopExpDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                    selectedValues = parameterCsv.split(",");
                                    $scope.stopExpDateFrom = selectedValues[0];
                                    $scope.stopExpDateTo = selectedValues[1];
                                    $("#stopExpDateToDiv" ).removeClass("hidden");
                             }
                      }
                      }
                      if(key == 'Stop Amount') {
	                      var relationalOperator = value.relationalOperator;
	                      var parameterCsv = value.parameterCsv;
	                      if(relationalOperator != null && parameterCsv != null) {
	
	                             $("#stopAmtSearchOption option:contains("+relationalOperator+")").attr("selected", true);
	                             $scope.currentFilter.searchParametersMap['Stop Amount'] = value;
	                             if(relationalOperator != 'is between') {
	                            	 $scope.stopAmt = parameterCsv;
	                             }else {
	                                 var selectedValues = new Array();
	                                 selectedValues = parameterCsv.split(",");
	                                 $scope.stopAmt = selectedValues[0];
	                                 $scope.stopAmtTo = selectedValues[1];
	                                 $("#stopAmtTo" ).removeClass("hidden");
	                          }
	                             
	                      }
                      }
                      if(key == 'Void Date') {
                      var relationalOperator = value.relationalOperator;
                      var parameterCsv = value.parameterCsv;

                      if(relationalOperator != null && parameterCsv != null) {
                             $("#voidDateSearchOption option:contains("+relationalOperator+")").attr("selected", true);
                             $scope.currentFilter.searchParametersMap['Void Date'] = value;
                             if(relationalOperator != 'is between') {
                                    $scope.voidDateFrom = parameterCsv;
                             } else {
                                    var selectedValues = new Array();
                                    selectedValues = parameterCsv.split(",");
                                    $scope.voidDateFrom = selectedValues[0];
                                    $scope.voidDateTo = selectedValues[1];
                                    $("#voidDateToDiv" ).removeClass("hidden");
                             }
                      }

                      }
                      if(key == 'Trace Number') {
	                      var relationalOperator = value.relationalOperator;
	                      var parameterCsv = value.parameterCsv;
	                      if(relationalOperator != null && parameterCsv != null) {
	
	                             $("#traceNumSearchOption option:contains("+relationalOperator+")").attr("selected", true);
	                             $scope.currentFilter.searchParametersMap['Trace Number'] = value;
	                             if(relationalOperator != 'is between') {
	                            	 $scope.traceNum = parameterCsv;
	                             } else {
	                            	 var selectedValues = new Array();
	                                 selectedValues = parameterCsv.split(",");
	                                 $scope.traceNum = selectedValues[0];
	                                 $scope.traceNumTo = selectedValues[1];
	                                 $("#traceNumTo" ).removeClass("hidden");
	                             }
	                      }
                      }
                      /*if(key == 'Reference Number') {
                            $scope.refNum = value;
                            $scope.currentFilter.searchParametersMap['Reference Number'] = value;
                      }*/

        });
        $scope.currentFilter.searchCriteria = $("#lookFor").val();
        if(typeof(window.localStorage)!=="undefined") {
               window.localStorage.setItem("paymentsFilter", JSON.stringify($scope.currentFilter));
        }
     }
    
    //select multiple banks here.
    $scope.showBankForFilter = function(bankName)
	{
		for (var i in $scope.banks) {
			if($scope.banks[i].name == bankName) {
				  //$("#bank").find("option[value="+$scope.banks[i].id+"]").prop("selected", "selected");
				  //$("#bank").find("option[text="+$scope.banks[i].name+"]").prop("selected", "selected");
				  $("#bank option:contains(" + bankName + ")").attr('selected', 'selected');
				  $("#bank").multiselect('refresh');
			}
		}
    } 
    
    //display company here...
    $scope.showComapnyForFilter = function(comapnyName) {
    	for (var i in $scope.fiteredCompany) {
			if($scope.fiteredCompany[i].name == comapnyName) {
				$scope.comp = $scope.fiteredCompany[i];
				$scope.getAccountsByCompanySynchronous();
				$scope.currentFilter.searchParametersMap['Customer'] = comapnyName;
				$("#bank").find("option[text="+companyName+"]").prop("selected", "selected");
				$("#bank").multiselect('refresh');
			}
		}
    }     
    
	$scope.getCompaniesByBankSynchronous = function(bankArray) {
		var bankIds = "";
    	for(var i = 0; i < bankArray.length; i++)
    		{
    		   var id = bankArray[i];
			   if(bankArray.length > 1)
			   {
				bankIds =  id + "-" +bankIds;
			   }
			   else
			   {
				 bankIds = id;
			   }
    	}
    	var fetchAll = true;
    	if($('#bankSearchOption').val() == 'doesNotContain') {
    		fetchAll = false;
    	}
        if (!bank.$pristine) {
	    	 $.ajax({
	             url: $scope.baseUrl + "/user/fetchCompanies/"+bankIds+"/"+fetchAll,
	             type: "GET",
	             contentType: 'application/json',
	             datatype: "json",
	            
	             success: function(response) {
					$scope.fiteredCompany = [];
					$scope.companies = response;
					for (var key in $scope.companies) {
						$scope.fiteredCompany.push($scope.companies[key]);
					}
					$scope.fiteredCompany = sortByName($scope.fiteredCompany, "name");
					$.each($scope.fiteredCompany, function(id, name) {
							var cmp = $("#comp option[value='" + name.id + "']").val();
							if(cmp!="undefined" && cmp != name.id)
							  $('#comp').append('<option value="'+name.id+'">'+name.name+'</option>').multiselect('rebuild');
					});
					$scope.waitprocess = false;
	             },
	             error: function(response) {
	                $scope.waitprocess = false;
	             },
	             async: false
	         });
        }
		
    }
    
	
	
    $scope.getAccountsByCompanySynchronous = function(compArray, bankArray) {
	    if(compArray.length == 0 && bankArray.length == 0)
		{
			return false;
		}
		var bankIds = "";
    	for(var i = 0; i < $scope.bArray.length; i++)
    		{
    		   var id = $scope.bArray[i];
			   if($scope.bArray.length > 1)
			   {
				bankIds =  id + "-" +bankIds;
			   }
			   else
			   {
				 bankIds = id;
			   }
    	}
		var compIds = "";
    	for(var i = 0; i < compArray.length; i++)
    		{
    		   var id = compArray[i];
			   if(compArray.length > 1)
			   {
				   compIds =  id + "-" +compIds;
			   }
			   else
			   {
				   compIds = id;
			   }
    	}	
        if (!comp.$pristine && compArray.length > 0 && bankArray.length > 0) {
            $.ajax({
	             url: $scope.baseUrl + "/user/accounts/" +  bankIds + "/" + compIds,
	             type: "GET",
	             contentType: 'application/json',
	             datatype: "json",
	            
	             success: function(response) {
					$scope.filteredAccount = [];
					$scope.accounts = response;
					for(var i=0; i < response.length;i++)
					{
						var act = $("#accnt option[value='" + response[i].accountNumber + "']").val();
						if(act!="undefined" && act != response[i].accountNumber)
						{
							$('#accnt').append('<option value="'+response[i].accountNumber+'">'+response[i].accountNumber+'</option>').multiselect('rebuild');
						}
					}
					$scope.waitprocess = false;
					},
					error: function(response) {
					$scope.waitprocess = false;
					},
	             async: false
	         });
        }
    };

   
    /**
     * Filter the Company List by bank id
     * combo
     * 
     * Company Combo-Box
     */

    $scope.getCompaniesByBank = function(bankArray) {
    	var bankIds = "";
    	for(var i = 0; i < bankArray.length; i++)
    		{
    		   var id = bankArray[i];
			   if(bankArray.length > 1)
			   {
				bankIds =  id + "-" +bankIds;
			   }
			   else
			   {
				 bankIds = id;
			   }
    	}
    	if(bankIds.length == 0)
		{
			bankIds = 0
		}
    	var fetchAll = true;
    	if($('#bankSearchOption').val() == 'doesNotContain') {
    		fetchAll = false;
    	}
    	if (!bank.$pristine) {
    		 $scope.waitprocess = true;
        	 $http({
                 url: $scope.baseUrl + "/user/fetchCompanies/"+bankIds+"/"+fetchAll,
                 params: { 'reqtime': $.now()},
                 dataType: "json",
                 method: "GET",
                 headers: {
                     "Content-Type": "application/json"
                 }
             }).success(function(response) {
            	 $scope.fiteredCompany = [];
                 $scope.companies = response;
                 $('#comp').empty();
				 if(response.length == 0)
				 {
					$('#comp >option').remove();
				 }
                 for (var key in $scope.companies) {
                   $scope.fiteredCompany.push($scope.companies[key]);
                 }
				 	$scope.fiteredCompany = sortByName($scope.fiteredCompany, "name");
				$.each($scope.fiteredCompany, function(id, name) {
					var cmp = $("#comp option[value='" + name.id + "']").val();
					if(cmp!="undefined" && cmp != name.id)
  				      $('#comp').append('<option value="'+name.id+'">'+name.name+'</option>').multiselect('rebuild');
				});
				 $scope.waitprocess = false;
             }).error(function(error) {
                 $scope.error = error;
                 $scope.waitprocess = false;
             });
        }
    };

    /**
     * This method expects BankId and CompanyId to fetch Account numbers from
     * DB. Logic to send request if the user clicks both select boxes and a
     * 
     * Accounts Combo-Box
     */
    $scope.getAccountsByCompany = function(compArray) {
    	var bankIds = "";
    	for(var i = 0; i < $scope.bArray.length; i++)
    		{
    		   var id = $scope.bArray[i];
			   if($scope.bArray.length > 1)
			   {
				bankIds =  id + "-" +bankIds;
			   }
			   else
			   {
				 bankIds = id;
			   }
    	}
    	var compIds = "";
    	for(var i = 0; i < compArray.length; i++)
    		{
    		   var id = compArray[i];
			   if(compArray.length > 1)
			   {
				   compIds =  id + "-" +compIds;
			   }
			   else
			   {
				   compIds = id;
			   }
    	}	      

    	if(bankIds.length == 0 || compIds.length == 0)
		{
			bankIds = 0;
			compIds = 0;
		}	
        if (!comp.$pristine) {
        	 $scope.waitprocess = true;
            $http({
                url: $scope.baseUrl + "/user/accounts/" +  bankIds + "/" + compIds,
                params: { 'reqtime': $.now()},
                dataType: "json",
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            }).success(function(response) {
				$scope.filteredAccount = [];
                $scope.accounts = response;
                $('#accnt').empty();
				for(var i=0; i < response.length;i++)
				{
					var act = $("#accnt option[value='" + response[i].accountNumber + "']").val();
					if(act!="undefined" && act != response[i].accountNumber)
						{
							$('#accnt').append('<option value="'+response[i].accountNumber+'">'+response[i].accountNumber+'</option>').multiselect('rebuild');
						}
				}
				 $scope.waitprocess = false;
            }).error(function(error) {
                $scope.error = error;
                $scope.waitprocess = false;
            });
        }
    };
    
    /**
     * Fetch the Bank List for displaying bank names in Bank Select combo
     * 
     * Bank combo-box
     */
    $scope.getAllPaymentStatus = function() {
        $http({
            url: $scope.baseUrl + "/user/allpaymentstatus",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.allpaymentStatus = response;
			$.each($scope.allpaymentStatus, function(id, name) {
					$('#pymntStatus').append('<option value="'+name.description+'">'+name.description+'</option>').multiselect('rebuild');
			});
        }).error(function(error) {
            $scope.error = error;
        });
    };
    
    $scope.getAllItemTypes = function() {
        $http({
            url: $scope.baseUrl + "/user/allitemtypes",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.allitemTypes = response;
			$.each($scope.allitemTypes, function(id, name) {
					$('#itemType').append('<option value="'+name.name+'">'+name.name+'</option>').multiselect('rebuild');
			});
        }).error(function(error) {
            $scope.error = error;
        });
    };
    
    $scope.getAllCreatedMethods = function() {
        $http({
            url: $scope.baseUrl + "/user/allcreatedmethods",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.allcreateMethods = response;
			$.each($scope.allcreateMethods, function(id, name) {
					$('#createMethod').append('<option value="'+name.name+'">'+name.description+'</option>').multiselect('rebuild');
			});
        }).error(function(error) {
            $scope.error = error;
        });
    };
    
   
    $scope.getAllExceptionStatus = function() {
        $http({
            url: $scope.baseUrl + "/user/allexceptionstatus",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.allexceptionStatus = response;
			$.each($scope.allexceptionStatus, function(id, name) {
					$('#exStatus').append('<option value="'+name.name+'">'+name.name+'</option>').multiselect('rebuild');
			});
        }).error(function(error) {
            $scope.error = error;
        });
    };
    
    $scope.getAllExceptionTypes = function() {
        $http({
            url: $scope.baseUrl + "/user/allexceptiontypes",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.allexceptionTypes = response;
			$.each($scope.allexceptionTypes, function(id, name) {
					$('#exType').append('<option value="'+name.label+'">'+name.label+'</option>').multiselect('rebuild');
			});
        }).error(function(error) {
            $scope.error = error;
        });
    };
    
    $scope.getAllMatchStatus = function() {
        $http({
            url: $scope.baseUrl + "/user/allmatchstatus",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.allmatchStatus = response;
			$.each($scope.allmatchStatus, function(id, name) {
				$('#matchStatus').append('<option value="'+name+'">'+name+'</option>').multiselect('rebuild');
			});
        }).error(function(error) {
            $scope.error = error;
        });
    };
	
	$scope.getResolutionActions = function() {
        $http({
            url: $scope.baseUrl + "/user/allactions",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.allResActions = response;
			$.each($scope.allResActions, function(id, name) {
				$('#resAction').append('<option value="'+name.name+'">'+name.description+'</option>').multiselect('rebuild');
			});
        }).error(function(error) {
            $scope.error = error;
        });
    };
	

    $scope.addSelectionToFilterMap = function() {
    	if($scope.currentFilter == []) {
    		$scope.currentFilter = {searchParametersMap : {}, filterName  : '', filterDescription : '', searchCriteria : ''};
    	}
    	$scope.currentFilter.searchParametersMap = {};
    	var bank = $( "#bank" ).val();
    	if($scope.bankNames != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#bankSearchOption option:selected" ).text();
    		//searchParameterDto.parameterCsv = $( "#bank option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.bankNames;
    		$scope.currentFilter.searchParametersMap['Bank'] = searchParameterDto;
    	}
    	var comp = $( "#comp" ).val();
    	if($scope.compNames != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#compSearchOption option:selected" ).text();
    		//searchParameterDto.parameterCsv = $( "#comp option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.compNames;
    		$scope.currentFilter.searchParametersMap['Customer'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Customer'] = $( "#comp option:selected" ).text();
    	}
    	var accnt = $( "#accnt" ).val();
    	if($scope.accntNames != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#accntSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.accntNames; //$( "#accnt option:selected" ).text();
    		$scope.currentFilter.searchParametersMap['Account Number'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Account Number'] = $( "#accnt option:selected" ).text();
    	}
    	//PRE
    	var pymntStatus = $( "#pymntStatus" ).val();
    	if($scope.paymentStatus != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#pymntStatusSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.paymentStatus;//$( "#pymntStatus option:selected" ).text();
    		$scope.currentFilter.searchParametersMap['Payment Status'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Payment Status'] = $( "#pymntStatus option:selected" ).text();
    	}
    	var itemType = $( "#itemType" ).val();
    	if($scope.itemTypes != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#itemTypeSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.itemTypes;//$( "#pymntStatus option:selected" ).text();
    		$scope.currentFilter.searchParametersMap['Item Type'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Payment Status'] = $( "#pymntStatus option:selected" ).text();
    	}
    	var resAction = $( "#resAction" ).val();
    	if($scope.resActions != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#resActionSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.resActions;
    		$scope.currentFilter.searchParametersMap['Resolution Action'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Resolution Action'] = $( "#resAction option:selected" ).text();
    	}
    	var createMethod = $( "#createMethod" ).val();
    	if($scope.createdMethods != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#createMethodSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.createdMethods;//$( "#pymntStatus option:selected" ).text();
    		$scope.currentFilter.searchParametersMap['Created Method'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Payment Status'] = $( "#pymntStatus option:selected" ).text();
    	}
    	var matchStatus = $( "#matchStatus" ).val();
    	if($scope.matchStatusName != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#matchStatusSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.matchStatusName;//$( "#matchStatus option:selected" ).text();
    		$scope.currentFilter.searchParametersMap['Match Status'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Match Status'] = $( "#matchStatus option:selected" ).text();
    	}
    	var exType = $( "#exType" ).val();
    	if($scope.exTypes != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#exTypeSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.exTypes;//$( "#exType option:selected" ).text();
    		$scope.currentFilter.searchParametersMap['Exception Type'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Exception Type'] = $( "#exType option:selected" ).text();
    	}
    	var exStatus = $( "#exStatus" ).val();
    	if($scope.exStatusName != "") {
    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
    		searchParameterDto.relationalOperator = $( "#exStatusSearchOption option:selected" ).text();
    		searchParameterDto.parameterCsv = $scope.exStatusName;//$( "#exStatus option:selected" ).text();
    		$scope.currentFilter.searchParametersMap['Exception Status'] = searchParameterDto;
    		//$scope.currentFilter.searchParametersMap['Exception Status'] = $( "#exStatus option:selected" ).text();
    	}
    	var voidAmtRelationalOperator = $( "#voidAmtSearchOption option:selected" ).text();
    	var voidAmt = $( "#voidAmt" ).val();
    	var voidAmtTo = $( "#voidAmtTo" ).val();
    	if(voidAmtRelationalOperator != 'is between') {
	    	if(voidAmt != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = voidAmtRelationalOperator;
	    		searchParameterDto.parameterCsv = voidAmt;
	    		$scope.currentFilter.searchParametersMap['Void Amount'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Void Amount'] = voidAmt;
	    	}
    	}else {
	    	if(voidAmt != '' && voidAmtTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = voidAmtRelationalOperator;
	    		searchParameterDto.parameterCsv = voidAmt + "," + voidAmtTo;
	    		$scope.currentFilter.searchParametersMap['Void Amount'] = searchParameterDto;	
	    	}
    	} 
    	var snumRelationalOperator = $( "#snumSearchOption option:selected" ).text();
    	var snum = $( "#snum" ).val();
    	var snumTo = $( "#snumTo" ).val();
    	if(snumRelationalOperator != 'is between') {
	    	if(snum != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = snumRelationalOperator;
	    		searchParameterDto.parameterCsv = snum;
	    		$scope.currentFilter.searchParametersMap['Check Number'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Check Number'] = snum;
	    	}
    	}else {
	    	if(snum != '' && snumTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = snumRelationalOperator;
	    		searchParameterDto.parameterCsv = snum + "," + snumTo;
	    		$scope.currentFilter.searchParametersMap['Check Number'] = searchParameterDto;	
	    	}
    	} 
    	var itemAmountRelationalOperator = $( "#itemAmountSearchOption option:selected" ).text();
    	var itemAmount = $( "#itemAmount" ).val();
    	var itemAmountTo = $( "#itemAmountTo" ).val();
    	if(itemAmountRelationalOperator != 'is between') {
	    	if(itemAmount != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = itemAmountRelationalOperator;
	    		searchParameterDto.parameterCsv = itemAmount;
	    		$scope.currentFilter.searchParametersMap['Item Amount'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Check Number'] = snum;
	    	}
    	}else {
	    	if(itemAmount != '' && itemAmountTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = itemAmountRelationalOperator;
	    		searchParameterDto.parameterCsv = itemAmount + "," + itemAmountTo;
	    		$scope.currentFilter.searchParametersMap['Item Amount'] = searchParameterDto;	
	    	}
    	} 
    	
    	//Exception Create Date
    	var exCreateDateRelationalOperator = $( "#exCreateDateSearchOption option:selected" ).text();
    	var exCreateDateFrom = $( "#exCreateDateFrom" ).val();    	
    	if(exCreateDateRelationalOperator != 'is between') {
	    	if(exCreateDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = exCreateDateRelationalOperator;
	    		searchParameterDto.parameterCsv = exCreateDateFrom;
	    		$scope.currentFilter.searchParametersMap['Exception Create Date'] = searchParameterDto;
	    	}
    	} else {
        	var exCreateDateTo = $( "#exCreateDateTo" ).val(); 
	    	if(exCreateDateFrom != '' && exCreateDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = exCreateDateRelationalOperator;
	    		searchParameterDto.parameterCsv = exCreateDateFrom + "," + exCreateDateTo;
	    		$scope.currentFilter.searchParametersMap['Exception Create Date'] = searchParameterDto;	
	    	}
    	} 
    	
    	//Item Date
    	var itemDateRelationalOperator = $( "#itemDateSearchOption option:selected" ).text();
    	var itemDateFrom = $( "#itemDateFrom" ).val();    	
    	if(itemDateRelationalOperator != 'is between') {
	    	if(itemDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = itemDateRelationalOperator;
	    		searchParameterDto.parameterCsv = itemDateFrom;
	    		$scope.currentFilter.searchParametersMap['Item Date'] = searchParameterDto;
	    	}
    	} else {
        	var itemDateTo = $( "#itemDateTo" ).val(); 
	    	if(itemDateFrom != '' && itemDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = itemDateRelationalOperator;
	    		searchParameterDto.parameterCsv = itemDateFrom + "," + itemDateTo;
	    		$scope.currentFilter.searchParametersMap['Item Date'] = searchParameterDto;	
	    	}
    	}
   	
    	//Create Date
    	var createDateRelationalOperator = $( "#createDateSearchOption option:selected" ).text();
    	var createDateFrom = $( "#createDateFrom" ).val();    	
    	if(createDateRelationalOperator != 'is between') {
	    	if(createDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = createDateRelationalOperator;
	    		searchParameterDto.parameterCsv = createDateFrom;
	    		$scope.currentFilter.searchParametersMap['Created Date'] = searchParameterDto;
	    	}
    	} else {
        	var createDateTo = $( "#createDateTo" ).val(); 
	    	if(createDateFrom != '' && createDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = createDateRelationalOperator;
	    		searchParameterDto.parameterCsv = createDateFrom + "," + createDateTo;
	    		$scope.currentFilter.searchParametersMap['Created Date'] = searchParameterDto;	
	    	}
    	}    	
    	
    	var issueAmtRelationalOperator = $( "#issueAmtSearchOption option:selected" ).text();
    	var issueAmt = $( "#issueAmt" ).val();
    	var issueAmtTo = $( "#issueAmtTo" ).val();
    	if(issueAmtRelationalOperator != 'is between') {
	    	if(issueAmt != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = issueAmtRelationalOperator;
	    		searchParameterDto.parameterCsv = issueAmt;
	    		$scope.currentFilter.searchParametersMap['Issued Amount'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Issued Amount'] = issueAmt;
	    	}
    	}else {
	    	if(issueAmt != '' && issueAmtTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = issueAmtRelationalOperator;
	    		searchParameterDto.parameterCsv = issueAmt + "," + issueAmtTo;
	    		$scope.currentFilter.searchParametersMap['Issued Amount'] = searchParameterDto;	
	    	}
    	}
    	
    	//Issue Date
    	var issueDateRelationalOperator = $( "#issueDateSearchOption option:selected" ).text();
    	var issueDateFrom = $( "#issueDateFrom" ).val();    	
    	if(issueDateRelationalOperator != 'is between') {
	    	if(issueDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = issueDateRelationalOperator;
	    		searchParameterDto.parameterCsv = issueDateFrom;
	    		$scope.currentFilter.searchParametersMap['Issued Date'] = searchParameterDto;
	    	}
    	} else {
        	var issueDateTo = $( "#issueDateTo" ).val(); 
	    	if(issueDateFrom != '' && issueDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = issueDateRelationalOperator;
	    		searchParameterDto.parameterCsv = issueDateFrom + "," + issueDateTo;
	    		$scope.currentFilter.searchParametersMap['Issued Date'] = searchParameterDto;	
	    	}
    	}
    	var paidAmtRelationalOperator = $( "#paidAmtSearchOption option:selected" ).text();
    	var paidAmt = $( "#paidAmt" ).val();
    	var paidAmtTo = $( "#paidAmtTo" ).val();
    	if(paidAmtRelationalOperator != 'is between') {
	    	if(paidAmt != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = paidAmtRelationalOperator;
	    		searchParameterDto.parameterCsv = paidAmt;
	    		$scope.currentFilter.searchParametersMap['Paid Amount'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Paid Amount'] = paidAmt;
	    	}
    	}else {
	    	if(paidAmt != '' && paidAmtTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = paidAmtRelationalOperator;
	    		searchParameterDto.parameterCsv = paidAmt + "," + paidAmtTo;
	    		$scope.currentFilter.searchParametersMap['Paid Amount'] = searchParameterDto;	
	    	}
    	}
    	
    	//Paid Date
    	var paidDateRelationalOperator = $( "#paidDateSearchOption option:selected" ).text();
    	var paidDateFrom = $( "#paidDateFrom" ).val();    	
    	if(paidDateRelationalOperator != 'is between') {
	    	if(paidDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = paidDateRelationalOperator;
	    		searchParameterDto.parameterCsv = paidDateFrom;
	    		$scope.currentFilter.searchParametersMap['Paid Date'] = searchParameterDto;
	    	}
    	} else {
        	var paidDateTo = $( "#paidDateTo" ).val(); 
	    	if(paidDateFrom != '' && paidDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = paidDateRelationalOperator;
	    		searchParameterDto.parameterCsv = paidDateFrom + "," + paidDateTo;
	    		$scope.currentFilter.searchParametersMap['Paid Date'] = searchParameterDto;	
	    	}
    	}
    	
    	//Stop Date
    	var stopDateRelationalOperator = $( "#stopDateSearchOption option:selected" ).text();
    	var stopDateFrom = $( "#stopDateFrom" ).val();    	
    	if(stopDateRelationalOperator != 'is between') {
	    	if(stopDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = stopDateRelationalOperator;
	    		searchParameterDto.parameterCsv = stopDateFrom;
	    		$scope.currentFilter.searchParametersMap['Stop Date'] = searchParameterDto;
	    	}
    	} else {
        	var stopDateTo = $( "#stopDateTo" ).val(); 
	    	if(stopDateFrom != '' && stopDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = stopDateRelationalOperator;
	    		searchParameterDto.parameterCsv = stopDateFrom + "," + stopDateTo;
	    		$scope.currentFilter.searchParametersMap['Stop Date'] = searchParameterDto;	
	    	}
    	}
    	
    	
    	//Stop Exp Date
    	var stopExpDateRelationalOperator = $( "#stopExpDateSearchOption option:selected" ).text();
    	var stopExpDateFrom = $( "#stopExpDateFrom" ).val();    	
    	if(stopExpDateRelationalOperator != 'is between') {
	    	if(stopExpDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = stopExpDateRelationalOperator;
	    		searchParameterDto.parameterCsv = stopExpDateFrom;
	    		$scope.currentFilter.searchParametersMap['Stop Expiration Date'] = searchParameterDto;
	    	}
    	} else {
        	var stopExpDateTo = $( "#stopExpDateTo" ).val(); 
	    	if(stopExpDateFrom != '' && stopExpDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = stopExpDateRelationalOperator;
	    		searchParameterDto.parameterCsv = stopExpDateFrom + "," + stopExpDateTo;
	    		$scope.currentFilter.searchParametersMap['Stop Expiration Date'] = searchParameterDto;	
	    	}
    	}	
    	
    	var stopAmtRelationalOperator = $( "#stopAmtSearchOption option:selected" ).text();
    	var stopAmt = $( "#stopAmt" ).val();
    	var stopAmtTo = $( "#stopAmtTo" ).val();
    	if(stopAmtRelationalOperator != 'is between') {
	    	if(stopAmt != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = stopAmtRelationalOperator
	    		searchParameterDto.parameterCsv = stopAmt;
	    		$scope.currentFilter.searchParametersMap['Stop Amount'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Stop Amount'] = stopAmt;
	    	}
    	}else {
	    	if(stopAmt != '' && stopAmtTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = stopAmtRelationalOperator;
	    		searchParameterDto.parameterCsv = stopAmt + "," + stopAmtTo;
	    		$scope.currentFilter.searchParametersMap['Stop Amount'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Void Date'] = voidDate;
	    	}
    	}   
    	
    	//Void Date
    	var voidDateRelationalOperator = $( "#voidDateSearchOption option:selected" ).text();
    	var voidDateFrom = $( "#voidDateFrom" ).val();    	
    	if(voidDateRelationalOperator != 'is between') {
	    	if(voidDateFrom != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = voidDateRelationalOperator;
	    		searchParameterDto.parameterCsv = voidDateFrom;
	    		$scope.currentFilter.searchParametersMap['Void Date'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Void Date'] = voidDate;
	    	}
    	} else {
    		var voidDateTo = $( "#voidDateTo" ).val(); 
	    	if(voidDateFrom != '' && voidDateTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = voidDateRelationalOperator;
	    		searchParameterDto.parameterCsv = voidDateFrom + "," + voidDateTo;
	    		$scope.currentFilter.searchParametersMap['Void Date'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Void Date'] = voidDate;
	    	}
    	}   	
    	
    	var traceNumRelationalOperator = $( "#traceNumSearchOption option:selected" ).text();
    	var traceNum = $( "#traceNum" ).val();
    	var traceNumTo = $( "#traceNumTo" ).val();
    	if(traceNumRelationalOperator != 'is between') {
	    	if(traceNum != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = traceNumRelationalOperator;
	    		searchParameterDto.parameterCsv = traceNum;
	    		$scope.currentFilter.searchParametersMap['Trace Number'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Trace Number'] = traceNum;
	    	}
    	}else {
    		if(traceNum != '' && traceNumTo != '') {
	    		var searchParameterDto = {relationalOperator : '', parameterCsv : ''};
	    		searchParameterDto.relationalOperator = traceNumRelationalOperator;
	    		searchParameterDto.parameterCsv = traceNum + "," + traceNumTo;
	    		$scope.currentFilter.searchParametersMap['Trace Number'] = searchParameterDto;
	    		//$scope.currentFilter.searchParametersMap['Void Date'] = voidDate;
	    	}
    	}
    	/*var refNum = $( "refNum" ).val();
    	if(refNum != '') {
    		$scope.currentFilter.searchParametersMap['Reference Number'] = refNum
    	}*/
    	$scope.currentFilter.searchCriteria = $("#lookFor").val();
    	if(typeof(window.localStorage)!=="undefined") {
    		window.localStorage.setItem("paymentsFilter", JSON.stringify($scope.currentFilter));
    	}
    };
    
    $scope.fetchFilters = function() {
    	 $scope.waitfilter = true;
        $http({
            url: $scope.baseUrl + "/user/findallfilters",
            params: { 'reqtime': $.now()},
            dataType: "json",
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
            $scope.paymentFilters = response;
            var paymentsData = window.localStorage.getItem("showpaymentsdata");
            //clear everything when page is loaded, but if upDateFilter is clicked do not clear the paymentFilter from local storage 
            if(paymentsData == "true") {
            	 $scope.clearFields(false);
            } else {
            	$scope.clearFields(true);
            	$scope.addSearchForField();
            }
            $scope.populateFields();
            $scope.waitfilter = false;
        }).error(function(error) {
            $scope.error = error;
            $scope.waitfilter = false;
        });
    };
    
    $scope.clearFields = function(clearMap) {    	
	 	$("#bank").val([]);
	    $("#bank").val("");
	    $("#bank").multiselect("refresh");
	    
	 	$("#comp").val([]);
	    $("#comp").val("");
	    $("#comp").multiselect("refresh");
	    
	 	$("#accnt").val([]);
	    $("#accnt").val("");
	    $("#accnt").multiselect("refresh");
	    
	 	$("#pymntStatus").val([]);
	    $("#pymntStatus").val("");
	    $("#pymntStatus").multiselect("refresh");
	    
	    $("#itemType").val([]);
	    $("#itemType").val("");
	    $("#itemType").multiselect("refresh");
	    
	    $("#resAction").val([]);
	    $("#resAction").val("");
	    $("#resAction").multiselect("refresh");
	    
	    $("#createMethod").val([]);
	    $("#createMethod").val("");
	    $("#createMethod").multiselect("refresh");
	    
	 	$("#matchStatus").val([]);
	    $("#matchStatus").val("");
	    $("#matchStatus").multiselect("refresh");
	    
	 	$("#exStatus").val([]);
	    $("#exStatus").val("");
	    $("#exStatus").multiselect("refresh");
	    
	 	$("#exType").val([]);
	    $("#exType").val("");
	    $("#exType").multiselect("refresh");
	    
	    $scope.itemTypes = "";
	    $scope.bankNames = "";
	    $scope.compNames = "";
	    $scope.accntNames = "";
	    $scope.paymentStatus = "";
	    $scope.resActions = "";
	    $scope.createdMethods = "";
	    $scope.matchStatusName = "";
	    $scope.exTypes = "";
	    $scope.exStatusName = "";
    	
    	$scope.bank = 0;
        $scope.comp = 0;
        $scope.accnt = 0;
        $scope.voidAmt = '';
        $scope.snum = '';
        $scope.snumTo = '';
        $scope.itemAmount = '';
        $scope.itemAmountTo = '';
        $scope.pymntStatus = 0;
        $scope.itemType = 0;
        $scope.createMethod = 0;
        //$scope.payAmt = '';
        //$scope.payDate = '';
        $scope.matchStatus = 0;
        $scope.exType = 0;
        $scope.exStatus = 0;
        $scope.exCreateDateFrom = '';
        $scope.exCreateDateTo = '';
        $scope.itemDateFrom = '';
        $scope.itemDateTo = '';
        $scope.createDateFrom = '';
        $scope.createDateTo = '';
        $scope.resAction = 0;
        $scope.issueAmt = '';
        $scope.issueAmtTo = '';
        $scope.issueDateFrom = '';
        $scope.issueDateTo = '';
        $scope.paidAmt = '';
        $scope.paidAmtTo = '';
        $scope.paidDateFrom = '';
        $scope.paidDateTo = '';
        $scope.stopDateFrom = '';
        $scope.stopDateTo = '';
        $scope.stopExpDateFrom = '';
        $scope.stopExpDateTo = '';
        $scope.stopAmt = '';
        $scope.stopAmtTo = '';
        $scope.voidDateFrom = '';
        $scope.voidDateTo = '';
        $scope.voidAmt = '';
        $scope.voidAmtTo = '';
        $scope.traceNum = '';
        $scope.traceNumTo = '';
        //$scope.refNum = '';
        if(clearMap == true) {
        	window.localStorage.setItem("paymentsFilter","");
        	$scope.selectedfilter = "";
        	window.localStorage.setItem("selectedFilterVal", "");
        }
    };
    
    $scope.addSearchForField = function() {
    	if($scope.currentFilter == []) {
    		$scope.currentFilter = {searchParametersMap : {}, filterName  : '', filterDescription : '', searchCriteria : ''};
    	}
    	$scope.currentFilter.searchCriteria = $("#lookFor").val();
    	if(typeof(window.localStorage)!=="undefined") {
    		window.localStorage.setItem("paymentsFilter", JSON.stringify($scope.currentFilter));
    	}
    };
    
    $scope.search = function() {
    	this.addSelectionToFilterMap();
    	window.location.href = globalBaseURL + "/user/paymentsSearch";
    };
    
    $scope.saveNewFilter = function() {
    	this.addSelectionToFilterMap();
    	$('#saveFilterPopup').modal();
    };

    $scope.saveFilter = function() {
    	var paymentsFilter = window.localStorage.getItem("paymentsFilter");
    	var currentFilterObj = jQuery.parseJSON(paymentsFilter);
    	var filterName = $( "#filterName" ).val();
    	var filterDescription = $( "#filterDescription" ).val();
    	if(filterName == "" || filterDescription == "") {
    		$("#ajax-loader-savefilter").html('<div class="infobox"> Name and Description Can not be empty!!</div>');
    		return;
    	}
    	currentFilterObj.filterName = filterName;
    	currentFilterObj.filterDescription = filterDescription;
    	$.ajax({
            type: "POST",
            url: globalBaseURL + "/user/saveFilter",
            data: JSON.stringify(currentFilterObj),
            dataType: "json",
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
                $("#ajax-loader-savefilter").html('<div class="infobox"><img src="'+globalBaseURL+'/static/positivepay/images/ajax/ajax-loader.gif" /> Saving filter, please wait...</div>');
            }, 
            complete: function() {
            	$("#ajax-loader-savefilter").empty();
            },
            success: function(paymentsJson) {
            	$('#saveFilterPopup').modal('hide');
            	//Show success message
                $("#filterSaveMessage").notify("Filter saved successfully!", {className: "warn", arrowShow: false, autoHideDelay: 3000});
                window.localStorage.setItem("paymentsFilter", "");
            	angular.element('#paymentsAndItemsController').scope().fetchFilters();
            },
            error: function(jqXHR, textStatus, errorThrown) {
            	$('#saveFilterPopup').modal('hide');
            	$("#filterSaveMessage").notify("Error saving filter!", {className: "error", arrowShow: false, autoHideDelay: 3000});
            }
        });
    }
    
   
    $scope.getBank();
	//$scope.getCompany();
    $scope.getAllPaymentStatus();
    $scope.getAllItemTypes();
    $scope.getAllCreatedMethods();
    $scope.getAllMatchStatus();
    $scope.getAllExceptionTypes();
    $scope.getAllExceptionStatus();
	$scope.getResolutionActions();
    $scope.fetchFilters();
}

function displaySearchFilters(lookFor) {
	if(lookFor == 'Payments') {
		$("#filterTbl").show();
		$('.itemListCls').hide();
		$('.paymentListCls').show();
		window.localStorage.setItem("lookfor", "Payments");
	} else if(lookFor == 'Items') {
		$("#filterTbl").show();
		$('.paymentListCls').hide();
		$('.itemListCls').show();
		window.localStorage.setItem("lookfor", "Items");
	} else {
		$("#filterTbl").hide();
	}
}

function toggleDate (dateFieldName){
	var relationalOperator = $( "#"+dateFieldName+"SearchOption option:selected" ).text();  	
	if(relationalOperator == "is between") {
		$( "#"+dateFieldName+"ToDiv" ).removeClass("hidden");
	} else {
		$( "#"+dateFieldName+"ToDiv" ).addClass("hidden");	
	}
}

function toggleNumericFields(fieldName){
	var relationalOperator = $( "#"+fieldName+"SearchOption option:selected" ).text();  	
	if(relationalOperator == "is between") {
		$( "#"+fieldName+"To" ).removeClass("hidden");
	} else {
		$( "#"+fieldName+"To" ).addClass("hidden");	
	}
}

