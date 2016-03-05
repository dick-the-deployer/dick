'use strict';

angular.module('dick.metadata')
    .factory('InfoResource', ['$resource',
        function ($resource) {
            return $resource('/api/info');
        }
    ]);

