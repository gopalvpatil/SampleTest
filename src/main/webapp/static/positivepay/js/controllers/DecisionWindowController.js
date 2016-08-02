/**
 * UserController is responsible for modifying all the business logic on the
 * Find User Page
 * 
 * @Author Sameer Shukla
 * @constructor
 * 
 */
var DecisionWindowController = function($scope, $window, $http) {
	$scope.decisionwindow = {
		start : '',
		end : '',
		timezone : '',
		companyId : ''
	};
	$scope.baseUrl = globalBaseURL;
	$scope.banks = "Select Bank";
	$scope.companies = [];
	$scope.fiteredCompany = [];
	$scope.startHour = '';
	$scope.startMin = '';
	$scope.startMeridiem = '';
	$scope.endHour = '';
	$scope.endMin = '';
	$scope.endMeridiem = '';
	$scope.timezone = '';
	$scope.decisionWindow = [];
	$scope.filteredCompanyId = [];
	$scope.all = 0;
	$scope.individualCompanyIds = [];
	$scope.individual = 0;
	$scope.date = '';

	/**
	 * Fetch the Bank List for displaying bank names in Bank Select combo
	 * 
	 * Bank combo-box
	 */
	$scope.getBank = function() {
		$http({
			url : $scope.baseUrl + "/job/bank",
			params: { 'reqtime': $.now()},
			dataType : "json",
			method : "GET",
			headers : {
				"Content-Type" : "application/json"
			}
		}).success(function(response) {
			$scope.banks = response;
		}).error(function(error) {
			$scope.error = error;
		});
	};

	/**
	 * Fetch the Company List for displaying customer names in Customer Select
	 * combo
	 * 
	 * Company Combo-Box
	 */
	$scope.fetchBankCompanyDecisionWindow = function() {
		$http({
			url : $scope.baseUrl + "/decisionwindow/bankCompanyDecisionWindow",
			//params: { 'reqtime': $.now()},
			dataType : "json",
			method : "GET",
			headers : {
				"Content-Type" : "application/json"
			}
		}).success(function(response) {
			$scope.companies = response;
		}).error(function(error) {
			$scope.error = error;
		});
	};

	/**
	 * Filter the Company List by bank id combo
	 * 
	 * Company Combo-Box
	 */

	$scope.getCompaniesByBank = function(bank) {
		var bankId = $scope.bank.id;
		this.filterCompany(bankId);
		document.getElementById("selectAllCheck").checked = false;
		var iDiv = document.getElementById('userCreateInfo');
		iDiv.style.display= 'none';
	};

	$scope.selectedCompany = function(id) {
		$scope.individualCompanyIds = [];
		$scope.individualCompanyIds.push(id);
	};

	/**
	 * Filter company when BankId is passed and publish bank details to a new
	 * filtered array and then ng-repeat : Sameer Shukla
	 * 
	 */
	$scope.filterCompany = function(bankId) {
		$scope.fiteredCompany = [];
		for ( var key in $scope.companies) {
			if (bankId == $scope.companies[key].bankId) {
				$scope.fiteredCompany.push($scope.companies[key]);
				$scope.filteredCompanyId.push($scope.companies[key].companyId);
			}
		}
	};

	/**
	 * Filter search company dropdown based on bank selection
	 */
	$scope.searchCompanyFilter = function(bankId) {
		return function(company) {
			if (!bankId || bankId == '') {
				return true;
			} else if (company.bankId == bankId) {
				return true;
			}
			return false;
		};
	};

	/**
	 * Save Decision Window.
	 */
	$scope.save = function() {
		
		var iDiv = document.getElementById('userCreateInfo');
		iDiv.style.display= 'none';
		
		iDiv = document.getElementById('userCreateError');
		iDiv.style.display= 'none';
		
		
		$scope.company = [];
		$scope.decisionWindow = [];
		this.timezone = document.getElementById('timezone').value;
		if (this.timezone == "000") {
			$scope.error("Time zone is required.");
			return false;
		}
		$scope.waitprocess = true;
		var decisions = new function() {
			this.startHour = document.getElementById('jobStartHour').value;
			this.startMin = document.getElementById('jobStartMinute').value;
			this.startMeridiem = document.getElementById('jobStartMeridiem').value;
			this.endHour = document.getElementById('jobEndHour').value;
			this.endMin = document.getElementById('jobEndMinute').value;
			this.endMeridiem = document.getElementById('jobEndMeridiem').value;
			this.timezone = document.getElementById('timezone').value;
			//this.date = document.getElementById('date').value;
			if ($scope.all)
				this.company = $scope.filteredCompanyId;
			else
				this.company = $scope.individualCompanyIds;
		};

		$scope.decisionWindow.push(decisions);
		var decisionWindowData = angular.toJson($scope.decisionWindow);
		$http({
			url : $scope.baseUrl + "/decisionwindow",
			dataType : "json",
			method : "POST",
			data : decisionWindowData,
			headers : {
				"Content-Type" : "application/json"
			}
		}).success(function(response) {
			$scope.success(response.message);
			 $scope.waitprocess = false;

		}).error(function(error) {
			$scope.error(error.message);
			 $scope.waitprocess = false;
		});
	};

	$("#date").datepicker();
	$("#imageStartDate").click(function() {
		$("#date").datepicker("show");
	});

	$scope.getBank();
	$scope.fetchBankCompanyDecisionWindow();

	$scope.success = function(message) {
		var iDiv = document.getElementById('userCreateInfo');
		if (!iDiv) {
			var iDiv = document.createElement('div');
			iDiv.style.display = 'block';
			iDiv.className = 'alert alert-success alert-dismissable';
			iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
			iDiv.innerHTML += message;
			$("#pb").prepend(iDiv);
		} else {
			iDiv.style.display = 'block';
			iDiv.className = 'alert alert-success alert-dismissable';
			iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
			iDiv.innerHTML += message;
			iDiv.style.display = 'block';
		}
	};

	$scope.error = function(message) {
		var iDiv = document.getElementById('userCreateError');
		if (!iDiv) {
			var iDiv = document.createElement('div');
			iDiv.style.display = 'block';
			iDiv.className = 'alert alert-danger alert-dismissable';
			iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
			iDiv.innerHTML += message;
			$("#pb").prepend(iDiv);
		} else {
			iDiv.style.display = 'block';
			iDiv.className = 'alert alert-danger alert-dismissable';
			iDiv.innerHTML = "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
			iDiv.innerHTML += message;
			iDiv.style.display = 'block';
		}
	};
};
