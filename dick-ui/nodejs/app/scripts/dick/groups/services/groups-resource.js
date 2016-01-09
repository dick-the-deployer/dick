'use strict';

angular.module('dick.groups')
        .factory('GroupsResource', ['$resource',
            function ($resource) {
                return $resource('/api/groups/:name', {}, {
                    getSilently: {method: 'GET', ignoreLoadingBar: true},
                    update: {method: 'PUT', url: '/api/groups/:id', params: {id: '@id'}},
                    remove: {method: 'DELETE', url: '/api/groups/:id', params: {id: '@id'}},
                });
            }
        ]);

