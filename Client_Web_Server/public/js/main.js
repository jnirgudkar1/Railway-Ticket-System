var app = angular.module('main', ['ngRoute']);
app.config(['$routeProvider', '$locationProvider',
     function ($routeProvider, $locationProvider) {
        $routeProvider.
        when('/search', {
            templateUrl: 'templates/search.html',
            controller: "search_controller"
        }).
        when('/bookings', {
            templateUrl: 'templates/bookings.html',
            controller: "bookings_controller"
        }).when('/admin', {
            templateUrl: 'templates/admin.html',
            controller: "admin_controller"
        }).otherwise({
            redirect: '/'
        });
    }]);

app.controller("search_controller", function ($scope, $http, $filter, $window) {
    var base_url='http://10.0.0.68:8080';

    $scope.isExact = false;

    $scope.date = new Date().toISOString();

    console.log("Reporting from Search controller");
    $scope.bookingsuccessful = true;
    console.log($scope.numberOfPassengers);
    $scope.search = function (req,res) {
        console.log("Reporting from search function");

        console.log($window.localStorage.getItem("user"));

        console.log($scope.passengers);
        console.log($scope.departure_time);
        console.log($scope.from_station);
        console.log($scope.to_station);
        console.log($scope.ticket_type);
        console.log($scope.connections);
        console.log($scope.roundtrip);
        console.log($scope.return_time);
        $scope.name = {};
        $scope.train_data;
        //$scope.bookingsuccessful=true;

        if ($scope.departure_time == undefined || $scope.from_station == undefined || $scope.to_station == undefined || $scope.ticket_type == undefined || $scope.connections == undefined)
            return null;
        $scope.numberOfPassengers = [];
        for (var i = 0; i < $scope.passengers; i++) {
            $scope.numberOfPassengers.push(i);
        }
        var departureDate = $scope.departure_time.toString();
        var departure_month = ($scope.departure_time.getMonth() + 1);
        var departure_date = (departureDate.split(' ')[2]);
        var departure_year = (departureDate.split(' ')[3]);

        var dept = $scope.departure_time.getTime("hh:mm");



        var departure_time = (departureDate.split(' ')[4]);

        var dTime = departure_time.toString();
        departure_time = (dTime.substring(0,5));

        console.log($scope.isExact);

        if ($scope.roundtrip == undefined)
            var URL = base_url+"/api/search?departureTime=" + departure_time + "&fromStation=" + $scope.from_station + "&toStation=" + $scope.to_station +
                "&ticketType=" + $scope.ticket_type+"&connection=" + $scope.connections + "&dateOfJourney=" + departure_year + "-" + departure_month + "-" + departure_date+"&exactTime="+$scope.isExact;

        else {
            var returnDate = $scope.return_time.toString();
            var return_month = ($scope.return_time.getMonth() + 1);
            var return_date = (returnDate.split(' ')[2]);
            var return_year = (returnDate.split(' ')[3]);
            var return_time = (returnDate.split(' ')[4]);
            var URL = base_url+"/api/search?departureTime=" + departure_time + "&fromStation=" + $scope.from_station + "&toStation=" + $scope.to_station +
                "&connection=" + $scope.connections + "&dateOfJourney=" + departure_year + "-" + departure_month + "-" + departure_date +
                "ticketType=" + $scope.ticket_type + "&roundTrip=" + $scope.roundtrip + "&returnDate=" + return_year + "-" + return_month + "-" + return_date + "&returnTime=" + return_time+"&exactTime"+$scope.isExact;

        }
        console.log("final check");
        console.log(URL);
        $http({
            url: URL,
            method: "POST"

        }).success(function (data) {
            console.log(data);
            if(data.length == 0)
            {
                alert("No trains found. Please try again");
            }
            $scope.result = null;
            $scope.result = data;
            console.log("Successful login");
        })
    }






    $scope.book = function (data) {
        $scope.train_data = data;
        console.log($scope.train_data);
        //$scope.train_data.listOfPassengers = [];
    }


    $scope.confirmBooking = function () {
        console.log("phh", $scope.train_data);
        console.log("hello", $scope.name);
        console.log("hello", $scope.name.length);
        $scope.train_data.listOfPassengers = [];
        for(var x in $scope.train_data.tickets)
        {
            if($scope.train_data.tickets.hasOwnProperty(x))
            {
                console.log($scope.train_data.tickets[x].numberOfPassengers);
                $scope.train_data.tickets[x].numberOfPassengers = $scope.numberOfPassengers.length;
                console.log($scope.train_data.tickets[x].numberOfPassengers);
            }

            x.numberOfPassengers= $scope.numberOfPassengers.length;
        }

        for (var key in $scope.name) {
            if ($scope.name.hasOwnProperty(key)) {
                console.log("test", $scope.name[key]);
                $scope.train_data.listOfPassengers.push($scope.name[key]);

            }
        }


        var URL = base_url+"/api/transaction?userId="+$window.localStorage.getItem("user");
        $http({
            url: URL,
            method: "POST",
            data: $scope.train_data
            }).success(function (data) {
                console.log("Booking successful");
                $scope.bookingsuccessful = false;
            }).error(function (data) {
                console.log("Booking unsuccessful");
            });
    }



    $scope.searchRoute = function () {
        console.log("Rerouting to search page");
        $window.location.reload();
    }
    
        $scope.logout = function(){
        console.log("Reporting from log out function");
        $window.localStorage.removeItem("user");
        $window.location.href="/";
    }
});

app.controller("bookings_controller", function ($scope, $http, $window) {
    console.log("Reporting from bookings controller");

    var base_url='http://10.0.0.68:8080';

    var URL =  base_url+"/api/getTransaction?userId="+$window.localStorage.getItem("user");

    $http({
        url: URL,
        method: "POST"
    }).success(function (data) {
        console.log(data);
        $scope.myBookings = null;
        $scope.myBookings = data;
        console.log("Successful login");
    })


    $scope.cancel = function (data) {
        console.log("Rerouting to search page");
        console.log(data);

        /*for(var x in data.tickets)
            {
                if(data.tickets.hasOwnProperty(x))
                {
                  /!*  console.log(data.tickets[x].numberOfPassengers);
                    $scope.train_data.tickets[x].numberOfPassengers = $scope.numberOfPassengers.length;
                    console.log(data.tickets[x].numberOfPassengers);
*!/
                    var date = new Date().toISOString();

                    if(data.tickets[x].dateOfJourney.toISOString() > date)
                    {

                    }
                }

                x.numberOfPassengers= $scope.numberOfPassengers.length;
            }*/


        var URL = base_url+"/api/deleteTransaction?transactionId="+data.id+"&userId="+$window.localStorage.getItem("user");
        $http({
            url: URL,
            method: "POST"
        }).success(function (data) {
            console.log("Booking successful");
            $scope.bookingsuccessful = false;
            $window.location.reload();
        }).error(function (data) {
            console.log("Booking unsuccessful");
        });

    }
    
    $scope.logout = function(){
        console.log("Reporting from log out function");
        $window.localStorage.removeItem("user");
        $window.location.href="/";
    }

});

app.controller("admin_controller", function ($scope, $http) {
    console.log("Reporting from admin controller");
});