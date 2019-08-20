var app=angular.module('BRS_Login',[]);
app.controller('Passenger_Login_Controller', function($http, $scope,
		$window) {

	$scope.message="";
	$scope.login = function() {
		console.log($scope.userName + " " + $scope.password + " " + $scope.role);
      $http({
        url:"http://127.0.0.1:8081/UserService/users/login",
        data:{
        "userName":	$scope.userName,
        "password":$scope.password,
         "role":$scope.role
        },
        method:"post"
        }).then(function(result){
        	console.log("success");
          $scope.response=result.data;
        if($scope.response.status=="FAILURE"){
        	$scope.message=$scope.response.msg;
        }else if($scope.response.status=="SUCCESS"){
        	$scope.message=$scope.response.msg;
        	$scope.userId=$scope.response.data;
        	$window.location.href = 'E:/project7am/BRSUI/BRS/passengerDashboard.html';
        }
        },function(result){
        $scope.message="Unable to process your request!Please try again";
        });
	}
});
