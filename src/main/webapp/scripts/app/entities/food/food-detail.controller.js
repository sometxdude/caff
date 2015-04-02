'use strict';

angular.module('cafftrackApp')
    .controller('FoodDetailController', function ($scope, $stateParams, Food) {
        $scope.food = {};
        $scope.load = function (id) {
            Food.get({id: id}, function(result) {
              $scope.food = result;
            });
        };
        $scope.load($stateParams.id);
    });
