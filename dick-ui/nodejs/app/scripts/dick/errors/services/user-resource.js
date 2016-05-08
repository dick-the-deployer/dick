'use strict';

angular.module('dick.errors')
        .factory('UserResource', ['$resource',
            function ($resource) {
                return $resource('/api/user');
            }
        ]);
