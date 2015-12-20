'use strict';

angular.module('dick.builds')
        .factory('HooksResource', ['$resource',
            function ($resource) {
                return $resource('/api/hooks');
            }
        ]);

