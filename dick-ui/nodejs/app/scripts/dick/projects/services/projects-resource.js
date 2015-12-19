'use strict';

angular.module('dick.projects')
        .factory('ProjectsResource', ['$resource',
            function ($resource) {
                return $resource('/api/projects');
            }
        ]);

