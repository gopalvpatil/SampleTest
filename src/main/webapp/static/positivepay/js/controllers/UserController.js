/**
 * Add User functionality
 */
var AddUserController = function($scope,$window, $http) {
   $scope.companies = $window.companies;
   $scope.banks = $window.banks;
   $scope.roles = $window.roles;
   $scope.baseUrl = globalBaseURL;
   $scope.user = {};
   $scope.hasAddRolePermission = true;
   
   /**
    * Add Users 
    */
	$scope.addUser = function() {
		$("#addUserInfo").hide();
		$("#addUserError").hide();
		if(!$scope.validateInput()) {
			return;
		}
		$("#addUserWait").show();
		$http({
            url: globalBaseURL + "/user/addUser",
            method: "POST",
            data: $scope.user,
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function() {
        	$("#addUserWait").hide();
        	$("#addUserInfo").show();
			$("#addUserInfo").html("User added successfully.");
        }).error(function(error) {
        	$("#addUserWait").hide();
        	$("#addUserError").show();
			$("#addUserError").html(error);
        });
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
    	if(!$scope.user.userName || $scope.user.userName == '')
    		errors.push('Please enter user name. ');
    	
    	if(!$scope.user.email || $scope.user.email == '') {
    		errors.push('Please enter email address. ');
    	} else if(!$scope.checkEmail($scope.user.email)) {
			errors.push('Please enter a valid email address. ');
		}
    	
    	if(!$scope.user.firstName || $scope.user.firstName == '')
    		errors.push('Please enter first name. ');
    	
    	if(!$scope.user.lastName || $scope.user.lastName == '')
    		errors.push('Please enter last name. ');
    	
    	if($scope.hasAddRolePermission && (!$scope.user.roleId || $scope.user.roleId == '')) {
    		errors.push('Please select user role. ');
    	} else if($scope.user.roleId != '1') {
        	
    		if(!$scope.user.bankId || $scope.user.bankId == '')
        		errors.push('Please select bank. ');
        
    		if($scope.user.bankId && (!$scope.user.companyId || $scope.user.companyId == ''))
        		errors.push('Please select company. ');
    	}
    	
    	if(errors.length > 0) {
    		$("#addUserError").show();
			$("#addUserError").html(errors.toString());
    		return false;
    	}
    	
    	return true;
	};
};
