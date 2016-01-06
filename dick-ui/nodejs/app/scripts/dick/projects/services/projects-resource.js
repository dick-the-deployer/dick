'use strict';

angular.module('dick.projects')
        .factory('ProjectsResource', ['$resource',
            function ($resource) {
                return $resource('/api/projects/:namespace/:name', {}, {
                    allSilently: {method: 'GET', isArray: true, url: '/api/projects/all'},
                    builds: {method: 'GET', isArray: true, url: '/api/projects/:namespace/:name/builds'}
                });
            }
        ]);

