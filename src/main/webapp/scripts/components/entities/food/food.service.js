'use strict';

angular.module('cafftrackApp')
    .factory('Food', function ($resource) {
        return $resource('api/foods/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
