'use strict';

angular.module('cafftrackApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('food', {
                parent: 'entity',
                url: '/food',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cafftrackApp.food.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/food/foods.html',
                        controller: 'FoodController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('food');
                        return $translate.refresh();
                    }]
                }
            })
            .state('foodDetail', {
                parent: 'entity',
                url: '/food/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cafftrackApp.food.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/food/food-detail.html',
                        controller: 'FoodDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('food');
                        return $translate.refresh();
                    }]
                }
            });
    });
