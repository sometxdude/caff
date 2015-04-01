'use strict';

angular.module('cafftrackApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
