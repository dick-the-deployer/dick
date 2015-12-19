'use strict';

angular.module('dick.projects')
        .factory('NamespacesResource', ['$resource',
            function ($resource) {
                return $resource('/api/namespaces');
            }
        ]);

