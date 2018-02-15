/**  @author arunabh.shrivastava */

angular.module('MainCtrl', []).controller('MainController',
    function($scope, $http) {
        $scope.makeTransaction = function () {
            var data = {};
            var userId = 1;

            var data = {
                userId : userId,
                passengers : [1,2],
                ticketSet : ticketSet
            }

            $http({
                method : "POST",
                url : '/api/transaction',
            }).then(function(data) {
                console.log(data.data);
                $scope.tracks = data.data;
            });
        }
    });