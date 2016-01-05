'use strict';

angular.module('dick.groups')
        .factory('GroupsResource', ['$resource',
            function ($resource) {
                return $resource('/api/groups/:name', {}, {
                    getSilently: {method: 'GET', ignoreLoadingBar: true}
                });
            }
        ]);

