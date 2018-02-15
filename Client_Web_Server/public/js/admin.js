var app = angular.module('admin', ['ngRoute']);

app.config(['$routeProvider', '$locationProvider',
    function ($routeProvider, $locationProvider) {
        $routeProvider.
        when('/reset', {
            templateUrl: 'templates/reset.html',
            controller: "reset_controller"
        }).
        when('/cancel_train', {
            templateUrl: 'templates/cancel_train.html',
            controller: "cancel_train_controller"
        }).
        when('/system_reports', {
            templateUrl: 'templates/system_reports.html',
            controller: "system_reports_controller"
        }).otherwise({
            redirect: '/'
        });
    }]);

app.controller("admin_controller", function ($scope, $http, $window) {
    console.log("Reporting from admin controller");
    var base_url="http://10.0.0.73:8080";
    $scope.signin=function(){
        console.log("Reporting from admin sign in ");
        console.log("Username=",$scope.username);
        console.log("password=",$scope.password);
        var url=base_url+"/admin/login?username="+$scope.username+"&password="+$scope.password;

        $http({
            url: url,
            method: "POST"
        }).success(function (data) {
            console.log("Successful login", data);
            $window.location.href = "/admin_home";
        }).error(function (data) {
            console.log("error");
            //$window.location.href = "/admin_home";
        });


    }
});

app.controller("reset_controller", function ($scope, $http, $window) {
    $scope.update_capacity_success=true;
    $scope.reset=true;
    console.log("Reporting from reset controller");
    var base_url="http://10.0.0.73:8080";

    $scope.reset=function(){
        console.log("Reporting from reset function");
        var url=base_url+"/admin/reset"
        $http({
            url: url,
            method: "POST"
        }).success(function (data) {
            console.log("Reset Successful", data);
            $scope.reset=false;
        }).error(function (data) {
            console.log("error");
        });
    };

    $scope.update_train_capacity=function(){
        console.log("Reporting from update train capacity",$scope.train_capacity);
        var url=base_url+"/admin/updateTrainCapacity?capacity="+$scope.train_capacity;
        $http({
            url: url,
            method: "POST"
        }).success(function (data) {
            console.log("Train Capacity Updated Successfully", data);
            $scope.update_capacity_success=false;
        }).error(function (data) {
            console.log("error");
        });

    };
    
    $scope.logout = function(){
        console.log("Reporting from log out function");
        $window.localStorage.removeItem("user");
        $window.location.href="/";
    }
});

app.controller("cancel_train_controller", function ($scope, $http, $window) {
    console.log("Reporting from cancel_train controller");
    $scope.update_capacity_success=true;
    var base_url="http://10.0.0.73:8080";
    $scope.cancel_train=function(){
        var date = $scope.date.toString();
        var return_month = ($scope.date.getMonth() + 1);
        var return_date = (date.split(' ')[2]);
        var return_year = (date.split(' ')[3]);
        var journey_date=return_year+"-"+return_month+"-"+return_date;
        var url=base_url+"/admin/getTrainId?trainName="+$scope.train_name+"&dateOfJourney="+journey_date;

        $http({
            url: url,
            method: "POST"
        }).success(function (data) {
            console.log("Train Capacity Updated Successfully", data);
            $scope.update_capacity_success=false;
        }).error(function (data) {
            console.log("error");
        });


    };
    
    $scope.logout = function(){
        console.log("Reporting from log out function");
        $window.localStorage.removeItem("user");
        $window.location.href="/";
    }
    
});

app.controller("system_reports_controller", function ($scope, $http) {
    console.log("Reporting from system_reports controller");
});