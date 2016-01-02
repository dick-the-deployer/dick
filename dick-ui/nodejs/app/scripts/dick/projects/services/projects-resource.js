'use strict';

angular.module('dick.projects')
        .factory('ProjectsResource', ['$resource',
            function ($resource) {
                return $resource('/api/projects', {}, {
                    all: {method: 'GET', isArray: true, url: '/api/projects/all'}
                });
            }
        ]);

