'use strict';

angular.module('cafftrackApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('logEntry', {
                parent: 'entity',
                url: '/logEntry',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cafftrackApp.logEntry.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/logEntry/logEntrys.html',
                        controller: 'LogEntryController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('logEntry');
                        return $translate.refresh();
                    }]
                }
            })
            .state('logEntryDetail', {
                parent: 'entity',
                url: '/logEntry/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'cafftrackApp.logEntry.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/logEntry/logEntry-detail.html',
                        controller: 'LogEntryDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('logEntry');
                        return $translate.refresh();
                    }]
                }
            });
    });
