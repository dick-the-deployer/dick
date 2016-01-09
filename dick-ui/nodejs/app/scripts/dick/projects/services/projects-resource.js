'use strict';

angular.module('dick.projects')
        .factory('ProjectsResource', ['$resource',
            function ($resource) {
                return $resource('/api/projects/:namespace/:name', {}, {
                    query: {method: 'GET', isArray: true, url: '/api/projects'},
                    update: {method: 'PUT', url: '/api/projects/:id', params: {id: '@id'}},
                    remove: {method: 'DELETE', url: '/api/projects/:id', params: {id: '@id'}},
                    rename: {method: 'PUT', url: '/api/projects/:id/name', params: {id: '@id'}},
                    transfer: {
                        method: 'PUT',
                        url: '/api/projects/:id/namespace',
                        params: {id: '@id'}
                    },
                    allSilently: {method: 'GET', isArray: true, url: '/api/projects/all'},
                    builds: {method: 'GET', isArray: true, url: '/api/projects/:namespace/:name/builds'}
                });
            }
        ]);

