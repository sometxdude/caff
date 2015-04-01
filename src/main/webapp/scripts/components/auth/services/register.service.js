'use strict';

angular.module('cafftrackApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


