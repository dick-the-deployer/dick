'use strict';

angular.module('dick.groups')
    .controller('ProjectsController', ['ProjectsResource', '$scope', 'MetadataService', 'rx', 'settings', 'observeOnScope',
        '$rootScope',
        function (projectsResource, $scope, metadataService, rx, settings, observeOnScope, $rootScope) {
            metadataService.setTitle('Projects');
            metadataService.setPageTitle('Projects');
            if (angular.isDefined($rootScope.filterName)) {
                $scope.name = $rootScope.filterName;
                $rootScope.filterName = null;
            }
            var page = 0, size = 20;
            var load = function (filter) {
                projectsResource.query(filter).$promise.then(function (data) {
                    $scope.projects = (data);
                    if (data.length !== 0) {
                        page++;
                    }
                });
            };
            observeOnScope($rootScope, 'filterName')
                .subscribe(function (change) {
                    $scope.name = change.newValue;
                });

            observeOnScope($scope, 'name')
                .debounce(500)
                .subscribe(function (change) {
                    page = 0;
                    load({page: page, size: size, name: change.newValue});
                });

            if ($scope.name) {
                var filter = {page: page, size: size, name: $scope.name};
            } else {
                var filter = {page: page, size: size};
            }
            load(filter);

            $scope.loadMore = function () {
                if ($scope.data) {
                    if ($scope.name) {
                        var filter = {page: page, size: size, name: $scope.name};
                    } else {
                        var filter = {page: page, size: size};
                    }
                    projectsResource.query(filter).$promise.then(function (data) {
                        $scope.projects = $scope.projects.concat(data);
                        if (data.length !== 0) {
                            page++;
                        }
                    });
                }
            };
            var deferred;
            var subscriber = rx.Observable.interval(settings.interval)
                .filter(function () {
                    return !deferred || deferred.$resolved;
                })
                .safeApply($scope, function () {
                    var ids = $scope.projects.map(function (elem) {
                        return elem.id;
                    });
                    if (ids.length !== 0) {
                        deferred = projectsResource.allSilently({ids: ids});
                        deferred.$promise.then(function (data) {
                            $scope.projects = data;
                        });
                    }
                })
                .subscribe();

            $scope.$on("$destroy", function () {
                subscriber.dispose();
            });
        }
        ]);
