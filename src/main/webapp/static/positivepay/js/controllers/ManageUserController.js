/**
 * UserController is responsible for modifying all the business logic on the
 * Find User Page
 * 
 * @Author Sameer Shukla
 * @constructor
 * 
 */
var ManageUserController = function($scope, $window, $http) {
	$scope.waitProcessMessage = '';
	$scope.findUserCriteria = {username : '',accountNo : '',bankId : '',companyId : '',archivedUser : false};
	$scope.findUserResponse = {}; 
	$scope.manageUsers = {};
	$scope.manageUserError = '';
	$scope.manageUserMessage = '';
    $scope.editMode = false;
    $scope.companies = $window.companies;
    $scope.banks = $window.banks;
    $scope.reactivate = false;
    $scope.archive = false;
    $scope.baseUrl = globalBaseURL;
    $scope.userPermission = {};
	$scope.availablePermissionIds = [];//availablePermissionIds contains the array of permission ids which logged users can manage
    $scope.selectAllPermissions = false;
    $scope.newRoleFlag = false;
    $scope.userPermissionWaitMsg = '';
    $scope.permissionsError = '';
    $scope.activityListStartIndex = 0;
    /**
     * All the global variable declarations
     */
    $scope.clearAllMessagesAndWaitProcess = function() {
    	$scope.waitProcessMessage = '';
    	$scope.manageUserError = '';
    	$scope.manageUserMessage = '';
    	$scope.userPermissionWaitMsg = '';
    	$scope.permissionsError = '';
    };

    /**
     * Filter search company dropdown based on bank selection
     */
    $scope.searchCompanyFilter = function(bankId) {
    	return function(company) {
    		if(!bankId || bankId == '') {
    			return true;
    		}else if(company.bankId == bankId) {
    			return true;
    		}
    		return false;
    	};
    };

    /**
     * Search Users for specified criteria
     */
    $scope.findUsersBySearchCriteria = function() {
    	window.scrollTo(0,0);
    	$scope.clearAllMessagesAndWaitProcess();
        $scope.reactivate = false;
        $scope.archive = false;
    	
    	if($scope.findUserCriteria.accountNo && !$.isNumeric($scope.findUserCriteria.accountNo)) {
    		$scope.manageUserError = 'Please enter a numeric value for Account No.';
    		return;
    	}
    	$scope.findUserResponse = {};
    	$scope.manageUsers = {};
    	$scope.waitProcessMessage = 'Searching for user(s). please wait...';
    	$http({
            url: $scope.baseUrl + "/user/manageUser/find",
            method: "POST",
            data: $scope.findUserCriteria,
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
        	if(response.length == 0) {
        		 $scope.manageUserError = "No record found.";
        	}else{
        		$scope.findUserResponse = response;
        		$scope.manageUsers = $.extend(true,[],response);
        	}
        	$scope.waitProcessMessage = '';
        }).error(function(error) {
            $scope.manageUserError = error;
            $scope.waitProcessMessage = '';
        });
    };
    
    /**
     * Method to save users
     */
    $scope.saveManageUsers = function() {
    	window.scrollTo(0,0);
    	$scope.clearAllMessagesAndWaitProcess();
    	var modifiedUsers = [];
    	var newActiveStatus = null; 
    	
    	if($scope.reactivate)
    		newActiveStatus = true;
    	else if($scope.archive)
    		newActiveStatus = false;

    	//Check if all Banks and Company are selected.
    	var unselectedBankOrCompany=false;
    	$.each($scope.manageUsers, function(index, manageUser) {
    		if(manageUser.bankId == null || manageUser.companyId == null) {
    			$scope.manageUserError = 'Please select highlighted bank and/or company.';
    			unselectedBankOrCompany=true;
    			return false; //break loop
    		}
    	});
    	if(unselectedBankOrCompany)
    		return false;
    	
    	//Create filtered modified users list.
    	$.each($scope.manageUsers, function(index, manageUser) {
    		var modifiedUser = null;
    		//check if company id is changed
    		if($scope.findUserResponse[index].companyId != manageUser.companyId) {
    			modifiedUser = $.extend(true,{},manageUser);
    			modifiedUser['bankId'] = manageUser.bankId;
    			modifiedUser['companyId'] = manageUser.companyId;
    			modifiedUser['companyChanged'] = true;
    		}
    		if(newActiveStatus != null && manageUser.active != newActiveStatus) {//check if user status is changed
    			if(!modifiedUser)
    				modifiedUser = $.extend(true,{},manageUser);
    			modifiedUser['active'] = newActiveStatus;
    		}
    		
    		if(modifiedUser)
    			modifiedUsers.push(modifiedUser);
    	});
    	
    	//Check atleast one user needs has to be updated to call save.
    	if(modifiedUsers.length > 0) {
    		$scope.waitProcessMessage = 'Updating user(s). please wait...';
    		$http({
                url: $scope.baseUrl + "/user/manageUser/save",
                method: "POST",
                data: modifiedUsers,
                headers: {
                    "Content-Type": "application/json"
                }
            }).success(function() {
            	if(newActiveStatus != null) {//Update the new status of the users if it's changed
                	$.each($scope.manageUsers, function(index, manageUser) {
                		if(manageUser.active != newActiveStatus)
                			manageUser['active'] = newActiveStatus;
                	});
            	}
            	$scope.findUserResponse = $.extend(true,{},$scope.manageUsers);//Update original search result to new modified users
            	$scope.manageUserMessage ="Users updated succesfully";
            	$scope.waitProcessMessage = '';
            }).error(function(error) {
                $scope.manageUserError = error;
                $scope.waitProcessMessage = '';
            });
    	}else{
    		$scope.manageUserError = "No changes to save";
    	}
    };
    
    /**
     * Function used to fetch activities of the users.
     * The function arguments userid, userName, resetStartIndex will be passed when user clicks on search user list eye symbol
     * 
     */
    $scope.fetchUserActivity = function(userId, userName, resetStartIndex) {
    	var maxResult = 5;
        if(resetStartIndex){//Reset start index if user clicks on search user list.
        	$scope.activityListStartIndex = 0;
        	$('#activity').empty();
        }
        
        if(userId){ //Use userId passed from user search list
        	$('#latestActivityModal').data('userId', userId);
        	$('#latestActivityModal').data('userName', userName);
        }else{
        	userId = $('#latestActivityModal').data('userId');
        }
        
        var url = $scope.baseUrl + "/user/history/get/" + userId;
        var queryParam = "startIndex="+$scope.activityListStartIndex+"&maxResult="+maxResult;
        
        $.ajax({
            url: url+ "?" + queryParam,
            type: "GET",
            datatype: "json",
            beforeSend:function(){
            	$('#error').hide();
            	$('#info').hide();
            	$('#noMoreDataToShow').hide();
            },
            success: function(userDtoList) {
                if (userDtoList.length > 0) {
                    for (var i = 0; i < userDtoList.length; i++){
                        var userDto = userDtoList[i];
                        id = userDto.userId;
                        if (i <= 4) {
                            var userActivityRecord = '<tr>'
                                    + '<td>'+ userDto.userActivityTime + '</td>'
                                    + '<td>'+ userDto.userActivityDate+ '</td>'
                                    + '<td>'+ userDto.userActivityName+ '</td>'
                                    + '<td>'+ userDto.userSystemComments+ '</td>'
                                    + '</tr>';
                            $('#activity').append(userActivityRecord);
                        }
                    }
                    $scope.activityListStartIndex = $scope.activityListStartIndex + userDtoList.length;
                    $('#latestActivityBtnContainer').show();
                    $('#info').html('Users latest activity retrieved successfully.');
                    $('#info').show();
                }
                
                if(userDtoList.length < maxResult ){
                	$('#latestActivityBtnContainer').hide();
                	if(resetStartIndex && userDtoList.length == 0){
                		$('#error').html('There is no user activity to show.');
                		$('#error').show();
                	}else{
                		$('#noMoreDataToShow').html('No more data to show.');
                		$('#noMoreDataToShow').show();
                	}
                }
                $('#latestActivityModal').modal('show');
            },
            error: function(response) {
                $('#error').html('Problem occured, users latest activity not retrieved.');
                $('#error').show();
                $('#latestActivityModal').modal('show');
            },
            async: false
        });
        event.preventDefault();
    };

    $scope.print = function() {
    	var w = window.open('', 'Latest Activity', 'height=400,width=600');
        w.document.write('<html><head><title>Latest Activity</title>');
        w.document.write('<link rel="stylesheet" href="' + globalBaseURL + '/static/thirdparty/bootstrap/css/bootstrap.min.css" type="text/css"/>');
        w.document.write('<link rel="stylesheet" href="' + globalBaseURL + '/static/positivepay/css/positivepay.css" type="text/css" media="print" />');
        // This code is require for firefox browser
        var cssStyle = '.table{font-size:12px;margin-bottom:0;}.table-bordered{border:1px solid #DDDDDD;}.table{margin-bottom:20px;width:100%;}table{background-color:rgba(0,0,0,0);max-width:100%;}table{border-collapse:collapse;border-spacing:0;}';
        cssStyle = cssStyle+' .table > thead:first-child > tr:first-child > th {border: 1px solid #006B87;}';
        cssStyle = cssStyle+' .table > caption + thead > tr:first-child > th, .table > colgroup + thead > tr:first-child > th, .table > thead:first-child > tr:first-child > th, .table > caption + thead > tr:first-child > td, .table > colgroup + thead > tr:first-child > td, .table > thead:first-child > tr:first-child > td {border-top: 0 none;}';
        cssStyle = cssStyle+' .table > thead > tr > th {background-color: #006B87;border: 1px solid #006B87;color: #FFFFFF;font-weight: normal;}';
        cssStyle = cssStyle+' .table-bordered > thead > tr > th, .table-bordered > thead > tr > td {border-bottom-width: 2px;}';
        cssStyle = cssStyle+' .table-bordered > thead > tr > th, .table-bordered > tbody > tr > th, .table-bordered > tfoot > tr > th, .table-bordered > thead > tr > td, .table-bordered > tbody > tr > td, .table-bordered > tfoot > tr > td {border: 1px solid #DDDDDD;}';
        cssStyle = cssStyle+' .table-condensed > thead > tr > th, .table-condensed > tbody > tr > th, .table-condensed > tfoot > tr > th, .table-condensed > thead > tr > td, .table-condensed > tbody > tr > td, .table-condensed > tfoot > tr > td {padding: 5px;}';
        cssStyle = cssStyle+' .table > thead > tr > th {border-bottom: 2px solid #DDDDDD;vertical-align: bottom;}';
        cssStyle = cssStyle+' .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {';
        cssStyle = cssStyle+'  border-top: 1px solid #DDDDDD;line-height: 1.42857;padding: 8px;vertical-align: top;}';
        cssStyle = cssStyle+' th {text-align: left;}';
        w.document.write('<style type="text/css"> @media print{#auditTrail { display:block;}} '+cssStyle+'</style>');
        // End of firefox browser code
        w.document.write('</head><body>');
        w.document.write('<div class="pp-width-full"><label class="control-label">Latest Activity : '+ $('#latestActivityModal').data('userName')+'</label></div>');
        w.document.write($("#auditTrail").html());
        w.document.write('</body></html>');
       	w.document.close();
        w.focus();
        w.print();
        w.close();
    };

    /*
     * On change of role on permission page
     */
    $scope.onRoleChange = function(roleId) {
    	$scope.userPermission.permissions = {};
    	$.each( $scope.userPermission.roles, function( index, role ) {
    	    if(role.id == roleId) {
    	    	$.each( role.permissionIds, function( index, permissionId ) {
    	    		if($.inArray(Number(permissionId), $scope.availablePermissionIds) > -1) {//only available permissions should be made true
    	    			$scope.userPermission.permissions[Number(permissionId)] = true;
    	    		}
    	    	});
    	    	return;
    	    }
    	});
    };

    /**
     * Select all permissions 
     */
    $scope.onSelectAllPermissionChange = function() {
    	$.each( $scope.availablePermissionIds, function( index, permissionId ) {
    		$scope.userPermission.permissions[Number(permissionId)] = $scope.selectAllPermissions;
    	});
    };

    /**
     * Get all permissions For Edit user
     */
    $scope.fetchPermissions = function(userName) {
    	$scope.clearAllMessagesAndWaitProcess();
        $scope.newRoleFlag = false;
        $scope.selectAllPermissions = false;
        $scope.userPermission = {};
        $scope.userPermissionWaitMsg = 'Loading data. Please wait...';
        $http({
            url: $scope.baseUrl + "/user/" + userName + "/permission",
            params: { 'reqtime': $.now() },
            dataType: "json",
            method: "GET"
        }).success(function(response) {
            $scope.userPermission = response;
            $.each( $scope.userPermission.permissions, function( key, value ) {
	    		if($.inArray(Number(key), $scope.availablePermissionIds) > -1) {//Permissions which are not part of available permission ids should be discarded
	    			$scope.userPermission.permissions[Number(key)] = value;
	    		}else{
	    			$scope.userPermission.permissions[Number(key)] = false;
	    		}
	    	});
            $scope.userPermissionWaitMsg = '';
        }).error(function(error) {
            $scope.permissionsError = error;
            $scope.userPermissionWaitMsg = '';
        });

        $('#permissionModal').modal('show');
    };

    /**
     * Save permissions For Edit user
     */
    $scope.savePermissions = function() {
    	$scope.clearAllMessagesAndWaitProcess();
    	$scope.userPermissionWaitMsg = 'Saving. Please wait...';
        if (!$scope.newRoleFlag) {
            $scope.userPermission.newRoleName = null;
        }
        $http({
            url: $scope.baseUrl + "/user/" + $scope.userPermission.userName + "/permission",
            method: "POST",
            data: $scope.userPermission,
            headers: {
                "Content-Type": "application/json"
            }
        }).success(function(response) {
        	$scope.userPermissionWaitMsg = '';
            $('#permissionModal').modal('hide');
        }).error(function(error) {
            $scope.permissionsError = error;
            $scope.userPermissionWaitMsg = '';
        });

    };

};
