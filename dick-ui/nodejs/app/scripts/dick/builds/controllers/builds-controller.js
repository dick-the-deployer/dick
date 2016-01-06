'use strict';

angular.module('dick.builds')
    .controller('BuildsController', ['MetadataService', '$scope', 'toaster', 'BuildsResource', '$window', '$stateParams',
        '$location', 'ProjectsResource', 'rx', 'settings',
        function (metadataService, $scope, toaster, buildsResource, $window, $stateParams, $location, projectsResource, rx, settings) {
            if ($window.angular.isUndefined($stateParams.namespace) ||
                $stateParams.namespace === '' || $window.angular.isUndefined($stateParams.name) ||
                $stateParams.name === '') {
                $location.path('/');
                return;
            }
            var name = $stateParams.name,
                namespace = $stateParams.namespace;
            var page = 0, size = 10;

            metadataService.setTitle(
                '<a href="/groups/' + namespace + '">' + namespace + '</a>' + '</a> / ' +
                '<a href="/' + namespace + '/' + name + '">' + name + '</a>'
            );
            metadataService.setPageTitle(namespace + ' / ' + name);

            projectsResource.get({namespace: namespace, name: name})
                .$promise.then(function (data) {
                $scope.project = data;
            });


            projectsResource.builds({
                namespace: namespace,
                name: name,
                page: page,
                size: size
            }).$promise.then(function (data) {
                $scope.builds = data;
                if (data.length !== 0) {
                    page++;
                }
            });
            var deferred;
            var subscriber = rx.Observable.interval(settings.interval)
                .filter(function () {
                    return !deferred || deferred.$resolved;
                })
                .safeApply($scope, function () {
                    deferred = projectsResource.builds({
                        namespace: namespace,
                        name: name,
                        page: 0,
                        size: $scope.builds.length > size ? $scope.builds.length : size
                    });
                    deferred.$promise.then(function (data) {
                        $scope.builds = data;
                    });
                })
                .subscribe();

            $scope.$on("$destroy", function () {
                subscriber.dispose();
            });

            $scope.loadMore = function () {
                projectsResource.builds({
                    namespace: namespace,
                    name: name,
                    page: page,
                    size: size
                }).$promise.then(function (data) {
                    $scope.builds = $scope.builds.concat(data);
                    if (data.length !== 0) {
                        page++;
                    }
                });
            };
        }
    ]);
