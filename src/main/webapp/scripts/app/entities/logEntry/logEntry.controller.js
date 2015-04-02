'use strict';

angular.module('cafftrackApp')
    .controller('LogEntryController', function ($scope, LogEntry, Food, ParseLinks) {
        $scope.logEntrys = [];
        $scope.foods = Food.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            LogEntry.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.logEntrys = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            LogEntry.update($scope.logEntry,
                function () {
                    $scope.loadAll();
                    $('#saveLogEntryModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            LogEntry.get({id: id}, function(result) {
                $scope.logEntry = result;
                $('#saveLogEntryModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            LogEntry.get({id: id}, function(result) {
                $scope.logEntry = result;
                $('#deleteLogEntryConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            LogEntry.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteLogEntryConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.logEntry = {logTime: null, quantity: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
