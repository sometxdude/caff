'use strict';

angular.module('cafftrackApp')
    .factory('LogEntry', function ($resource) {
        return $resource('api/logEntrys/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.logTime = new Date(data.logTime);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
