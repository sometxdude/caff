'use strict';

angular.module('cafftrackApp')
    .controller('LogEntryDetailController', function ($scope, $stateParams, LogEntry, Food) {
        $scope.logEntry = {};
        $scope.load = function (id) {
            LogEntry.get({id: id}, function(result) {
              $scope.logEntry = result;
            });
        };
        $scope.load($stateParams.id);
    });
