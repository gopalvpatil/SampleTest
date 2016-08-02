/**
 * JOBController is responsible for modifying all the business logic on the
 * Create Job Page.
 * 
 * @Author Sameer Shukla
 * @constructor
 * 
 */
var JobController = function($scope, $window, $http) {
	$scope.editMode = false;

	/**
	 * All the global variable declarations
	 */
	$scope.jobs = [];
	$scope.jobSteps = "";
	$scope.jobStepName = "";
	$scope.jobStepDesc = "";
	$scope.jobActionType = "";
	$scope.jobTypeId = 1;
	$scope.jobBankCriteria = "";
	$scope.jobCustomerCriteria = "";
	$scope.jobAccountCriteria = "";
	$scope.combos = "";
	$scope.radios = "";
	$scope.combosubarray = "";
	$scope.value = 'foo';
	$scope.banks = "Select Bank";
	$scope.selBanks = [];
	$scope.companies = "Select Company";
	$scope.fiteredCompany = [];
	$scope.accounts = "";
	$scope.jobName = '';
	$scope.jobFrequency = '';
	$scope.jobStartDate = '';
	$scope.jobEndDate = '';
	$scope.indefinitely = '';
	$scope.jobRunDay = '';
	$scope.jobRunTime = '';
	$scope.jobEndRunTime = '';
	$scope.timezone = '';
	$scope.intervalTime = '';
	$scope.thresholdTime = '';
	$scope.respmessage = '';
	$scope.jobDescription = '';
	$scope.count = $scope.jobs.length;
	$scope.compId = '';
	$scope.jobTypeSelected = '';
	$scope.jobActionTypeSelected = '';
	$scope.editMode = false;
	$scope.totalSteps = 1;
	$scope.jobSteps = [];
	$scope.jobStepId = 0;
	$scope.bank = '';
	$scope.isNewStep = 0;
	$scope.value = 0;

	$scope.editJob = function() {
		$scope.editMode = document.getElementById('edit').value;

		if ($scope.editMode) {
			var jobId = document.getElementById('id').value;
			$scope.jobSteps = JSON.parse($window.jobSteps);
			if ($scope.jobSteps.length > 0) {
				var jobStep = $scope.jobSteps[0];
				$scope.jobStepId = jobStep.jobStepId;
				$scope.options.value = jobStep.jobTypeId;
				this.populateCombo(jobStep.jobTypeId);
				$scope.jobActionTypeSelected = jobStep.jobActionTypeId;
				$scope.jobStepName = jobStep.jobStepName;
				$scope.jobStepDesc = jobStep.jobStepDescription;
				$scope.jobActionType = $scope.combosubarray[0];
				if (jobStep.jobAccountCriteria != null) {
					$scope.accnt = jobStep.jobAccountCriteria;
					this.editAccountsByCompany(jobStep.jobCustomerCriteria);
				}

				for ( var i = 0; i < $scope.combosubarray.length; i++) {
					if ($scope.combosubarray[i].name == jobStep.jobActionTypeName) {
						$scope.jobActionType = $scope.combosubarray[i];

					}
				}

				if ($scope.banks.length > 0 && jobStep.jobBankCriteria != null) {
					for ( var i = 0; i < $scope.banks.length; i++) {
						if ($scope.banks[i].id == jobStep.jobBankCriteria) {
							$scope.bank = $scope.banks[i];
						}
					}
					this.editCompaniesByBank($scope.bank.id);
				}
				if ($scope.fiteredCompany.length > 0) {
					$scope.comp = $scope.fiteredCompany[0];
					this.editAccountsByCompany($scope.comp.companyId);
				}
				if ($scope.accounts.length > 0) {
					$scope.accnt = $scope.accounts[0];
				}
			}// if
			// }// for
		}
	};

	/**
	 * Needed for radio's to behave properly
	 */
	$scope.options = {
		value : '-'
	};

	/**
	 * Once the user is done with 'Add Another Step' this method takes care of
	 * Save And Continue Step.
	 * 
	 * This method submit the JSON Array to the controller.
	 */
	$scope.submitArrayOfJobs = function() {
		var edit = false;
		if (document.getElementById('edit') != null)
			edit = document.getElementById('edit').value;

		var url = '';
		if (edit) {
			var id = document.getElementById('id').value;

			var editjobs = new function() {
				this.jobName = document.getElementById('jobName').value;
				this.jobDescription = document.getElementById('jobDescription').value;
				this.jobFrequency = document.getElementById('jobFrequency').value;
				this.jobStartDate = document.getElementById('jobStartDate').value;
				this.jobEndDate = document.getElementById('jobEndDate').value;
				this.indefinitely = document.getElementById('indefinitely').value;
				this.jobRunTime = document.getElementById('jobRunTime').value;
				this.jobEndRunTime = document.getElementById('jobEndRunTime').value;
				this.timezone = document.getElementById('timezone').value;
				this.weekly = document.getElementById('weekly').value;
				this.jobRunDay = document.getElementById('jobRunDay').value;
				this.intervalTime = document.getElementById('intervalTime').value;
				this.thresholdTime = document.getElementById('thresholdTime').value;
				this.jobStepName = document.getElementById('jobStepName').value;
				this.jobStepDescription = $scope.jobStepDesc;
				this.jobActionTypeId = $scope.jobActionType.id;
				this.jobTypeId = $scope.options.value;
				if ($scope.isNewStep == 1) {
					this.jobStepId = 0;
					url = "successjob" + "/" + edit + "/" + id + "/" + 0;
				} else {
					this.jobStepId = $scope.jobStepId;
					url = "successjob" + "/" + edit + "/" + id + "/"
							+ this.jobStepId;
				}
				if ($scope.bank != null && typeof ($scope.bank) != "undefined") {
					this.jobBankCriteria = $scope.bank.id;
				}
				if ($scope.comp != null && typeof ($scope.comp) != "undefined") {
					this.jobCustomerCriteria = $scope.comp.companyId;
				}
				if ($scope.accnt != null
						&& typeof ($scope.accnt) != "undefined") {
					this.jobAccountCriteria = $scope.accnt;
				}
			}
			if ($scope.isNewStep != 1) {
				$scope.jobs.push(editjobs);
			}

		}

		else {
			url = "successjob" + "/" + false + "/0" + "/0";
			if ($scope.jobs.length == 0) {
				this.appendJobInfo();
			}
		}
		var json = angular.toJson($scope.jobs);
		$http({
			url : url,
			dataType : "json",
			method : "POST",
			data : json,
			headers : {
				"Content-Type" : "application/json"
			}
		}).success(function(response) {
			$scope.respmessage = response;
			if (edit) {
				window.localStorage.setItem('jobresponse', "editsuccess");
			} else {
				window.localStorage.setItem('jobresponse', "success");
			}
			document.getElementById("savejob-form").action = "createjob";
			document.getElementById("savejob-form").submit();
		}).error(function(error) {
			$scope.respmessage = error;
			if (edit) {
				window.localStorage.setItem('jobresponse', "editerror");
			} else {
				window.localStorage.setItem('jobresponse', "error");
			}
			document.getElementById("savejob-form").action = "createjob";
			document.getElementById("savejob-form").submit();
		});
		$scope.isNewStep = 0;
	};

	/**
	 * This method treats entire page as JSON object, so when the user clicks
	 * 'Add another Step' this json object will be added in the JSON array, this
	 * approach avoids headache of session management on server and it's clean
	 * as well
	 * 
	 * Add another Step button
	 */
	$scope.appendJobInfo = function() {
		$scope.isNewStep = 1;
		var newjobs = new function() {

			this.jobName = document.getElementById('jobName').value;
			this.jobDescription = document.getElementById('jobDescription').value;
			this.jobFrequency = document.getElementById('jobFrequency').value;
			this.jobStartDate = document.getElementById('jobStartDate').value;
			this.jobEndDate = document.getElementById('jobEndDate').value;
			this.indefinitely = document.getElementById('indefinitely').value;
			this.jobRunTime = document.getElementById('jobRunTime').value;
			this.jobEndRunTime = document.getElementById('jobEndRunTime').value;
			this.timezone = document.getElementById('timezone').value;
			this.weekly = document.getElementById('weekly').value;
			this.jobRunDay = document.getElementById('jobRunDay').value;
			this.intervalTime = document.getElementById('intervalTime').value;
			this.thresholdTime = document.getElementById('thresholdTime').value;
			this.jobStepName = $scope.jobStepName;
			this.jobStepDescription = $scope.jobStepDesc;
			this.jobActionTypeId = $scope.jobActionType.id;
			this.jobTypeId = $scope.options.value;
			if ($scope.bank != null && typeof ($scope.bank) != "undefined") {
				this.jobBankCriteria = $scope.bank.id;
			}
			if ($scope.comp != null && typeof ($scope.comp) != "undefined") {
				this.jobCustomerCriteria = $scope.comp.companyId;
			}
			if ($scope.accnt != null && typeof ($scope.accnt) != "undefined") {
				this.jobAccountCriteria = $scope.accnt;
			}

		}
		$scope.jobs.push(newjobs);

		$scope.newjob.jobStepDescription = '';
		$scope.jobActionType = 0;
		$scope.bank = 0;
		$scope.comp = 0;
		$scope.accnt = 0
		$scope.jobform.$setPristine();
	};

	/**
	 * Fetch the mapping of JobType and JobActionType required. If there are 6
	 * rows and if valid mapping exists in JobActionType table then 6 radio
	 * button will be displayed. Filtering of data is taken care at <select>
	 * statement in SaveJob.jsp
	 * 
	 * JobType radio buttons
	 */
	$scope.getJobType = function() {
		/*
		 * $http({ url: "fetchJobType", dataType: "json", method: "GET",
		 * headers: { "Content-Type": "application/json" }
		 * }).success(function(response) { $scope.radios = response;
		 * }).error(function(error) { $scope.error = error; });
		 */

		$scope.radios = JSON.parse($window.jobsType);
	};

	/**
	 * Filtering criteria of JobType and JobActionType.
	 * 
	 * JobActionType Combo-box
	 */
	$scope.populateCombo = function(value) {
		try {
			$scope.value = value;
			$scope.combos = $scope.radios;
			for ( var key in $scope.combos) {
				if ($scope.combos.hasOwnProperty(key)) {
					var item = $scope.combos[key];
					if (item.id == value) {
						$scope.combosubarray = item.jobActionTypes;
					}
				}// if
			}// for
		} catch (e) {
			alert(e);
		}
	};

	/**
	 * Fetch the Bank List for displaying bank names in Bank Select combo
	 * 
	 * Bank combo-box
	 */
	$scope.getBank = function() {
		$http({
			url : "bank",
			params: { 'reqtime': $.now()},
			dataType : "json",
			method : "GET",
			headers : {
				"Content-Type" : "application/json"
			}
		}).success(function(response) {
			$scope.banks = response;
			if ($scope.banks.length > 0) {
				$scope.editJob();
			}
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
	/*
	 * $scope.getCompany = function() { $http({ url: "company", dataType:
	 * "json", method: "GET", headers: { "Content-Type": "application/json" }
	 * }).success(function(response) { $scope.companies = response;
	 * }).error(function(error) { $scope.error = error; }); };
	 */

	$scope.fetchBankCompanyDecisionWindow = function() {
		var baseUrl = globalBaseURL;
		$http({
			url : baseUrl + "/decisionwindow/bankCompanyDecisionWindow",
			params: { 'reqtime': $.now()},
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

	$scope.editCompaniesByBank = function(id) {
		var bankId = id;
		this.filterCompany(bankId);
	};

	$scope.getCompaniesByBank = function(bank) {
		var bankId = $scope.bank.id;
		if (!bank.$pristine) {
			this.filterCompany(bankId);
		}
	};

	/**
	 * This method expects BankId and CompanyId to fetch Account numbers from
	 * DB. Logic to send request if the user clicks both select boxes and a
	 * 
	 * Accounts Combo-Box
	 */
	$scope.getAccountsByCompany = function(comp) {
		if (typeof ($scope.comp) != "undefined") {
			// this.jobAccountCriteria = $scope.accnt;
			this.compId = $scope.comp.companyId;
		}
		if (!comp.$pristine) {
			$http({
				url : "account/" + this.compId,
				params: { 'reqtime': $.now()},
				dataType : "json",
				method : "GET",
				headers : {
					"Content-Type" : "application/json"
				}
			}).success(function(response) {
				$scope.accounts = response;
			}).error(function(error) {
				$scope.error = error;
			});
		}
	};

	$scope.editAccountsByCompany = function(id) {
		$http({
			url : "account/" + id,
			params: { 'reqtime': $.now()},
			dataType : "json",
			method : "GET",
			headers : {
				"Content-Type" : "application/json"
			}
		}).success(function(response) {
			$scope.accounts = response;
		}).error(function(error) {
			$scope.error = error;
		});
	};

	/**
	 * Filter company when BankId is passed and publish bank details to a new
	 * filtered array and then ng-repeat : Sameer Shukla
	 * 
	 */
	/*
	 * $scope.filterCompany = function(bankId) { $scope.fiteredCompany = []; for
	 * (var key in $scope.companies) { if (bankId ==
	 * $scope.companies[key].bank.id) {
	 * $scope.fiteredCompany.push($scope.companies[key]); } }
	 *  }
	 */

	$scope.filterCompany = function(bankId) {
		$scope.fiteredCompany = [];
		for ( var key in $scope.companies) {
			if (bankId == $scope.companies[key].bankId) {
				$scope.fiteredCompany.push($scope.companies[key]);
			}
		}

	}

	/**
	 * Cancel Operation
	 */
	$scope.cancelOperation = function() {
		$scope.newjob.jobStepDescription = '';
		$scope.newjob.jobStepName = '';
		$scope.jobActionTypeId = 0;
		$scope.bank = 0;
		$scope.comp = 0;
		$scope.accnt = 0
		$scope.jobform.$setPristine();
	}

	$scope.nextEdit = function() {
		if ($scope.jobSteps.length == 0
				|| $scope.totalSteps >= $scope.jobSteps.length) {
			this.error("JobSteps Not Found!!!");
			return false;
		}
		$scope.jobStep = $scope.jobSteps[$scope.totalSteps];
		$scope.jobStepId = $scope.jobStep.jobStepId;
		$scope.jobStepName = $scope.jobStep.jobStepName;
		$scope.jobStepDesc = $scope.jobStep.jobStepDescription;
		$scope.options.value = $scope.jobStep.jobTypeId;
		this.populateCombo($scope.jobStep.jobTypeId);
		if ($scope.jobStep.jobAccountCriteria != null) {
			$scope.accnt = $scope.jobStep.jobAccountCriteria;
			this.editAccountsByCompany($scope.jobStep.jobCustomerCriteria);
		}

		for ( var i = 0; i < $scope.combosubarray.length; i++) {
			if ($scope.combosubarray[i].name == $scope.jobStep.jobActionTypeName) {
				$scope.jobActionType = $scope.combosubarray[i];

			}
		}

		if ($scope.banks.length > 0) {
			if ($scope.jobStep.jobBankCriteria != null) {
				for ( var i = 0; i < $scope.banks.length; i++) {
					if ($scope.banks[i].id == $scope.jobStep.jobBankCriteria) {
						$scope.bank = $scope.banks[i];
					}
				}
			}
			this.editCompaniesByBank($scope.bank.id);
		}

		if ($scope.fiteredCompany.length > 0) {
			// $scope.comp = $scope.fiteredCompany[0];
			//
			if ($scope.jobStep.jobCustomerCriteria != null) {
				for ( var i = 0; i < $scope.fiteredCompany.length; i++) {
					if ($scope.fiteredCompany[i].companyId == $scope.jobStep.jobCustomerCriteria) {
						$scope.comp = $scope.fiteredCompany[i];
					}
				}
				// this.editAccountsByCompany($scope.comp.companyId);

			}

		}

		// this.populateBankCompanyAccountsOnEdit();
		$scope.totalSteps++;
	};

	// };

	/**
	 * Reseting radio's Must method
	 */
	$scope.newValue = function(value) {
		// $scope.options.value = value; // not needed, unless you want to do
		// more work on a change
	}

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
	}

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
	}
	$scope.getBank();
	$scope.getJobType();
	$scope.fetchBankCompanyDecisionWindow();
	$scope.newjob = {};

};