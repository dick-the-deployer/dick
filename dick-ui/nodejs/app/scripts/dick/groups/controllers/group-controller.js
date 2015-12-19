'use strict';

angular.module('dick.groups')
        .controller('GroupController', ['$window', '$scope', '$stateParams', '$location', 'MetadataService', 'GroupsResource',
            function ($window, $scope, $stateParams, $location, metadataService, groupsResource) {
                if ($window.angular.isUndefined($stateParams.name) ||
                        $stateParams.name === '') {
                    $location.path('/');
                    return;
                }
                var name = $stateParams.name;
                metadataService.setTitle(name);

                groupsResource.get({name: name}).$promise.then(function (data) {
                    $scope.group = data;
                });

            }
        ]);
