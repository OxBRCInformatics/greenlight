app = angular.module('greenlight', [])

app.controller('ExampleController', ['$scope', function ($scope) {
	$scope.consent = {};
	$scope.consent.patient = {
		firstName: "AA",
		lastName: "BB",
		dateOfBirth: {
			year: "2010"
		}
	};

	$scope.nhsNumberPattern = /^\d{10}$/

	$scope.validYears = []
	$scope.init = function () {

		var currentYear = new Date().getUTCFullYear();
		var startYear = currentYear - 100

		//if in edit mode, get start year based on patient dateOfBirth
		if($scope.consent.patient && $scope.consent.patient.dateOfBirth && $scope.consent.patient.dateOfBirth.year){
			startYear = $scope.consent.patient.dateOfBirth.year - 100
		}

		for (var year = startYear; year <= currentYear; year++) {
				$scope.validYears.push(year)
		}
	}


}]);

app.directive('consentFormId', function($q, $timeout) {
	return {
		require: 'ngModel',
		link: function(scope, elm, attrs, ctrl) {
			var usernames = ['Jim', 'John', 'Jill', 'Jackie'];

			ctrl.$asyncValidators.myValidator = function(modelValue, viewValue) {

				if (ctrl.$isEmpty(modelValue)) {
					// consider empty model valid
					return $q.when();
				}

				var def = $q.defer();

				$timeout(function() {
					// Mock a delayed response

//					if (usernames.indexOf(modelValue) === -1) {
						if(1!=1){
						// The username is available

							scope.$parent.foundFormId  ="LINK"
						def.resolve();
					} else {
						def.reject();
					}

				}, 500);

				return def.promise;
			};
		}
	};
});




