/**
 * CompanyController is responsible for modifying all the business logic for
 * Company Setup
 * 
 * @Author Umedh Meshram
 * 
 * @constructor
 * 
 */
var CompanyController = function($scope, $http) {
	$scope.waitprocess = false;
	$scope.accountwaitprocess = false;
	$scope.showArchievedAccounts = false;
	$scope.deleteAccountWaitprocess = false;
	$scope.isCompanyActive = false;
	$scope.baseUrl = globalBaseURL;
	$scope.company = {};
	$scope.userList = [];
	$scope.selectAllUsers = false;
	$scope.account = null;
	$scope.selectedAccountId = null;
	$scope.maskedPhone = null;
	$scope.maskedFax = null;
	$scope.companyInfoMessage = null;
	$scope.companySetupError = null;
	$scope.us_states = {AL: 'Alabama', AK: 'Alaska', AZ: 'Arizona', AR: 'Arkansas', CA: 'California', CO: 'Colorado', CT: 'Connecticut', DE: 'Delaware', DC: 'District of Columbia', FL: 'Florida', GA: 'Georgia', HI: 'Hawaii', ID: 'Idaho', IL: 'Illinois', IN: 'Indiana', IA: 'Iowa', KS: 'Kansas', KY: 'Kentucky', LA: 'Louisiana', ME: 'Maine', MD: 'Maryland', MA: 'Massachusetts', MI: 'Michigan', MN: 'Minnesota', MS: 'Mississippi', MO: 'Missouri', MT: 'Montana', NE: 'Nebraska', NV: 'Nevada', NH: 'New Hampshire', NJ: 'New Jersey', NM: 'New Mexico', NY: 'New York', NC: 'North Carolina', ND: 'North Dakota', OH: 'Ohio', OK: 'Oklahoma', OR: 'Oregon', PA: 'Pennsylvania', RI: 'Rhode Island', SC: 'South Carolina', SD: 'South Dakota', TN: 'Tennessee', TX: 'Texas', UT: 'Utah', VT: 'Vermont', VA: 'Virginia', WA: 'Washington', WV: 'West Virginia', WI: 'Wisconsin', WY: 'Wyoming'};
	$scope.timezone = { "US/Pacific":"Pacific", "US/Mountain":"Mountain", "US/Central":"Central", "US/Eastern":"Eastern" };
	
	/**
     * Get Company Data
     */
    $scope.populateCompanySetupData = function() {
    	if($('#companyId').val()) {
    		$scope.waitprocess = true;
    		$http({
				url : $scope.baseUrl + "/user/company/" + $('#companyId').val(),
				params: { 'reqtime': $.now() },
				dataType : "json",
				method : "GET"
			}).success(function(response) {
				$scope.company = response;
				$scope.isCompanyActive = $scope.company.active;
				$scope.waitprocess = false;
			}).error(function(error) {
				$scope.companySetupError = error;
				$scope.waitprocess = false;
			});
    	} else if($('#bankId').val()) {
    		$scope.company = {bankId:$("#bankId").val(), active:true};
    		$scope.isCompanyActive = true;
    	} else {
    		$scope.company = {active:true};
    		$scope.isCompanyActive = true;
    	}
    };
    
    $scope.checkEmail = function(email) {
		 var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		return re.test(email);
	};
    
	/*
	 * Function to validate input
	 */
    $scope.validateInput  = function() {
    	var errors = new Array();
    	if(!$scope.company.bankId || $scope.company.bankId == '')
    		errors.push('Please select a bank. ');
    	
    	if(!$scope.company.name || $scope.company.name == '')
    		errors.push('Company name is required. ');
    	
    	if(!$scope.company.mainContact || $scope.company.mainContact == '')
    		errors.push('Main contact is required. ');
		
    	if($scope.company.email && $scope.company.email != '') {
			if(!$scope.checkEmail($scope.company.email)) {
				errors.push('Please enter a valid email address. ');
			}
		}
    	
    	if($scope.company.phone != null && $scope.company.phone.length < 14) {
				errors.push('Phone number cannot be less than 10 characters.');
		}

    	if($scope.company.fax != null && $scope.company.fax.length < 14) {
			errors.push('Fax cannot be less than 10 characters.');
    	}
    	
    	if(!$scope.company.timeZone || $scope.company.timeZone == '')
    		errors.push('Please select a time zone. ');

		//TODO: Add more validations
    	if(errors.length > 0) {
    		$scope.companySetupError = errors.toString();
    		return false;
    	}
    	return true;
	};
    
	/**
	 * 
	 * This method submit the JSON Array to the controller.
	 */
	$scope.saveCompany = function() {
		$scope.companySetupError = null;
		$scope.companyInfoMessage = null;
		if($scope.validateInput()) {
			$scope.waitprocess = true;
			var url = $scope.baseUrl + "/user/company/setup";
			$http({
				url : url,
				dataType : "json",
				data : $scope.company,
				method : "POST",
				headers : {
					"Content-Type" : "application/json"
				}
			}).success(function(companyId) {
				$scope.company.id = companyId;
				$scope.companyInfoMessage = "Company saved succesfully";
				$scope.waitprocess = false;
				if($scope.isCompanyActive && !$scope.company.active) {//If company was active earlier and if it is made inactive 
					$scope.populateCompanySetupData();				//then call here to refetch company data
				}
				$scope.isCompanyActive = $scope.company.active;
	        }).error(function(error) {
	            $scope.companySetupError = error;
	            $scope.waitprocess = false;
	        });
		}
	};
	
    /**
     * Get Company Users
     */
    $scope.populateCompanyUsersinAccountPopop = function() {
    	$scope.accountwaitprocess = true;
    	$http({
			url : $scope.baseUrl + "/user/company/" + $('#companyId').val() + "/users" ,
			params: { 'reqtime': $.now()},
			dataType : "json",
			method : "GET"
		}).success(function(response) {
			$scope.userList = response;
			$scope.accountwaitprocess = false;
	    }).error(function(error) {
	        $scope.accountSetupError = error;
	        $scope.accountwaitprocess = false;
	    });
    };
	
	/**
     * Get all permissions For Edit user
     */
    $scope.addAccount = function(companyId) {
    	$scope.accountSetupError = null;
    	$scope.companySetupError = null;
    	$scope.companyInfoMessage = null;
    	$scope.account = {};
    	$scope.selectAllUsers = false;
    	if(!companyId)
    		$scope.companySetupError = "Company needs to be saved before creating account.";
    	else {
    		$scope.account = {'companyId':companyId,'active':true,'staleDays':180};
    		$('#accountSetupModal').modal('show');
    		if($scope.userList.length == 0) {
    			$scope.populateCompanyUsersinAccountPopop();
    		}
    	}
    };
    
    /**
	 * 
	 * This method submit the JSON Array to the controller.
	 */
	$scope.editAccount = function(accountId) {
		$scope.accountSetupError = null;
		$scope.companySetupError = null;
		$scope.companyInfoMessage = null;
		$scope.selectAllUsers = false;
		$scope.account = {};
		$scope.accountwaitprocess = true;
		$('#accountSetupModal').modal('show');
		var url = $scope.baseUrl + "/user/company/" + $scope.company.id + "/account/" + accountId;
		$http({
			url : url,
			params: { 'reqtime': $.now()},
			dataType : "json",
			method : "GET"
		}).success(function(account) {
			$scope.account = account;
			$scope.accountwaitprocess = false;
			if($scope.userList.length == 0) {
    			$scope.populateCompanyUsersinAccountPopop();
    		}
	    }).error(function(error) {
	        $scope.accountSetupError = error;
	        $scope.accountwaitprocess = false;
	    });
		
	};
	
	/**
	 * Open delete Account confirmation.
	 */
	$scope.openDeleteConfirmationModal = function(accountId) {
		$scope.deleteAccountWaitprocess = false;
		$scope.selectedAccountId = accountId;
		$('#deleteAccountModal').modal('show');
	};
	
	/**
	 * 
	 * Delete account.
	 */
	$scope.deleteAccount = function(accountId) {
		$scope.deleteAccountWaitprocess = true;
		$scope.companySetupError = null;
		$scope.companyInfoMessage = null;
		var url = $scope.baseUrl + "/user/company/" + $scope.company.id + "/account/" + accountId;
		$http({
			url : url,
			params: { 'reqtime': $.now()},
			method : "DELETE"
		}).success(function(account) {
			$scope.companyInfoMessage = "Account has been deleted succesfully";
			for (var i = 0; i < $scope.company.accounts.length; i++) {
				if($scope.company.accounts[i].id == accountId) {
					$scope.company.accounts[i].active = false;
					break;
				}
			}
			$('#deleteAccountModal').modal('hide');
	    }).error(function(error) {
	        $scope.companySetupError = error;
	        $('#deleteAccountModal').modal('hide');
	    });
	};
	
	/*
	 * Function to validate input
	 */
    $scope.validateAccountInput  = function() {
    	
    	var errors = new Array();
    	
    	if(!$scope.account.accountNumber || $scope.account.accountNumber == '')
    		errors.push('Please enter account number. ');
    	
    	if(!$scope.account.accountName || $scope.account.accountName == '')
    		errors.push('Please enter account name. ');
    	
    	if(!$scope.account.staleDays || $scope.account.staleDays == '')
    		errors.push('Please enter stale days. ');
    	
    	if(!$scope.account.ppDecision || $scope.account.ppDecision == '')
    		errors.push('Please select a Positive Pay Default Decision. ');
    	
    	if(errors.length > 0) {
    		$scope.accountSetupError = errors.toString();
    		return false;
    	}
    	return true;
	};
    
    /**
	 * 
	 * This method submit the JSON Array to the controller.
	 */
	$scope.saveAccount = function() {
		$scope.accountSetupError = null;
		
		if($scope.validateAccountInput()) { //Validate Account Input
			
			$scope.accountwaitprocess = true;
			var url = $scope.baseUrl + "/user/company/" + $scope.company.id + "/account";
			$http({
				url : url,
				dataType : "json",
				data : $scope.account,
				method : "POST",
				headers : {
					"Content-Type" : "application/json"
				}
			}).success(function(accountId) {
				if(!$scope.account.id) {
					$scope.account.id = accountId;
					if(!$scope.company.accounts) {
						$scope.company['accounts'] = [];
					}
					$scope.company.accounts.push($scope.account);
				} else {
					for (var i = 0; i < $scope.company.accounts.length; i++) {
						if($scope.company.accounts[i].id == accountId) {
							$scope.company.accounts[i] = $scope.account;
							break;
						}
					}
				}
				$scope.companyInfoMessage = "Account saved succesfully.";
				$('#accountSetupModal').modal('hide');
				$scope.accountwaitprocess = false;
	        }).error(function(error) {
	            $scope.accountSetupError = error;
	            $scope.accountwaitprocess = false;
	        });
		}
		
	};

	
	$scope.onSelectAllUserChange = function() {
		if($scope.selectAllUsers) {
			$scope.account.selectedUserIds = [];
			$.each($scope.userList, function(index, user) {
				$scope.account.selectedUserIds.push(user.userId);
			});
		}
	};
	
	$scope.onAccountUserCheckboxClicked = function(userId) {
		var index = $scope.account.selectedUserIds.indexOf(userId);
		if(index > -1) {
			$scope.account.selectedUserIds.splice(index,1);
		} else {
			$scope.account.selectedUserIds.push(userId);
		}
	};
	
    $scope.populateCompanySetupData();
    $('#contactPhone').mask('(000) 000-0000');
    $('#contactFax').mask('(000) 000-0000');
};