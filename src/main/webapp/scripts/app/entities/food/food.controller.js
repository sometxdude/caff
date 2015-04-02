'use strict';

angular.module('cafftrackApp')
    .controller('FoodController', function ($scope, Food, ParseLinks) {
        $scope.foods = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Food.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.foods = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Food.update($scope.food,
                function () {
                    $scope.loadAll();
                    $('#saveFoodModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Food.get({id: id}, function(result) {
                $scope.food = result;
                $('#saveFoodModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Food.get({id: id}, function(result) {
                $scope.food = result;
                $('#deleteFoodConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Food.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFoodConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.food = {name: null, caffeine: null, description: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
