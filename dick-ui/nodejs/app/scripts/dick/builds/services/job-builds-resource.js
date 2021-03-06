'use strict';

angular.module('dick.builds')
    .factory('JobBuildsResource', ['$resource',
        function ($resource) {
            return $resource('/api/job-builds/:id', {id: '@id'}, {
                chunks: {method: 'GET', url: '/api/job-builds/:id/chunks', isArray: true},
                chunksSilently: {
                    method: 'GET',
                    url: '/api/job-builds/:id/chunks',
                    isArray: true,
                    ignoreLoadingBar: true
                },
                output: {method: 'GET', url: '/api/job-builds/:id/output'}
                }
            );
        }
    ]);

