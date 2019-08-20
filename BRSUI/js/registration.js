var app = angular.module('BRS_Registration', ['ngRoute']);

app.config(['$routeProvider',

    function($routeProvider){
        $routeProvider.
        when('/',{
            templateUrl : 'passengerRegistration.html',
            controller : 'Passenger_Registration_Controller1'
        })  .
        when('/otpForm',{
            templateUrl : 'otpForm.html',
            controller: 'otpController'
        }).
        when("/loginForm", {
            templateUrl: 'loginForm.html',
            controller: 'loginController'
        });
    }]);
app.controller('Passenger_Registration_Controller1',
    function($http, $scope,$location,otpService) {
        $scope.message="";




        $scope.passengerRegistration = function() {
            console.log($scope.name + " " + $scope.email + " " + $scope.mobile
                + " " + $scope.gender + " " + $scope.dob + " "
                + $scope.password);
            $http({
                url:"http://127.0.0.1:8081/UserService/users/registerPassenger",
                data:{
                    "name":	$scope.name,
                    "email":$scope.email,
                    "mobile":$scope.mobile,
                    "password":$scope.password,
                    "dob":$scope.dob,
                    "gender":$scope.gender
                },
                method:"post"
            }).then(function(result){
                console.log("success");
                $scope.response=result.data;
                if($scope.response.status==="FAILURE"){
                    $scope.message=$scope.response.msg;
                }else if($scope.response.status==="SUCCESS"){
                    otpService.setOtpStatus($scope.response.msg);
                    otpService.setUserId($scope.response.data);
                    $location.path("/otpForm");
                }
            },function(){
                $scope.message="Unable to process your request!Please try again";
            });
        }

    });


app.service('otpService', function(){
    var otpStatus;
    var userId;
    this.setOtpStatus = function(msg){
        this.otpStatus = msg;
    }
    this.getOtpStatus = function(){
        return this.otpStatus;
    }
    this.setUserId = function(uid){
        this.userId = uid;
    }
    this.getUserId = function(){
        return this.userId;
    }
});

app.controller('otpController', function($scope,$http,$window,$location,otpService){
    $scope.userId=otpService.getUserId();
    $scope.otpStatus=otpService.getOtpStatus();
    console.log($scope.userId);
    $scope.verifyOTP=function() {
        $http({
            method:"get",
            url:"http://127.0.0.1:8081/UserService/users/validateOTP/"+$scope.userId+"/"+$scope.otp,
        }).then(function(result){

            $scope.response=result.data;
            if($scope.response.status==="FAILURE"){
                $scope.otpStatus=$scope.response.msg;
                $scope.otp="";
            }else if($scope.response.status==="SUCCESS"){
              $window.alert($scope.response.msg);
                $location.path("/loginForm");
            }

        },function(result){
          $scope.otpStatus="Unable to process your request";
        });
    }
});
app.controller('login_Controller',
    function($http, $scope) {

    });













