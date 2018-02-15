/**
 * Created by arunabh.shrivastava on 12/1/2017.
 */

var app = angular.module('App', ['ngRoute', 'appRoutes', 'MainCtrl']);
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);