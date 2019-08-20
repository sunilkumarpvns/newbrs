var app = angular.module('Search-Service', []);
app.controller('Search_Service_Controller', function($http, $scope, $window) {

	$scope.message = "";
	$scope.SearchService = function() {
		console.log($scope.source + " " + $scope.destination + " " + $scope.doj
				+ " " + $scope.dorj);
		$http({
			url : "http://192.168.0.72:8081/BusService/bus/searchBus",
			data : {
				"source" : $scope.source,
				"destination" : $scope.destination,
				"doj" : $scope.doj,
				"dorj" : $scope.dorj,

			},
			method : "post"
		}).then(function(result) {
			console.log("success");
			$scope.response = result.data;
			if ($scope.response.status == "FAILURE") {
				$scope.message = $scope.response.msg;
			} else if ($scope.response.status == "SUCCESS") {
				$scope.message = $scope.response.msg;
				$scope.userId = $scope.response.data;

			}
		}, function(result) {
			$scope.message = "!Please never try this again";
		});
	}
});
// commited by vira badhra- plz contact-9000683844