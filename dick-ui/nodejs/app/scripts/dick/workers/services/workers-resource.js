'use strict';

angular.module('dick.workers')
        .factory('WorkersResource', ['$resource',
            function ($resource) {
                return $resource('/api/workers/:name', {name: '@name'});
            }
        ]);

