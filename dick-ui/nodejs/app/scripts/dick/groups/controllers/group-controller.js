'use strict';

angular.module('dick.groups')
    .controller('GroupController', ['$window', '$scope', '$stateParams', '$location', 'MetadataService', 'GroupsResource', 'rx',
        function ($window, $scope, $stateParams, $location, metadataService, groupsResource, rx) {
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
            var subscriber = rx.Observable.interval(2000)
                .safeApply($scope, function () {
                    groupsResource.getSilently({name: name}).$promise.then(function (data) {
                        $scope.group = data;
                    });
                })
                .subscribe();

            $scope.$on("$destroy", function () {
                subscriber.dispose();
            });

            }
        ]);
