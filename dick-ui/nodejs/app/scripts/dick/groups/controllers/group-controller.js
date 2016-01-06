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
            metadataService.setPageTitle(name);

            groupsResource.get({name: name}).$promise.then(function (data) {
                $scope.group = data;
            });
            var deferred;
            var subscriber = rx.Observable.interval(2000)
                .filter(function () {
                    return !deferred || deferred.$resolved;
                })
                .safeApply($scope, function () {
                    deferred = groupsResource.getSilently({name: name});
                    deferred.$promise.then(function (data) {
                        $scope.group = data;
                    });
                })
                .subscribe();

            $scope.$on("$destroy", function () {
                subscriber.dispose();
            });

        }
    ]);
