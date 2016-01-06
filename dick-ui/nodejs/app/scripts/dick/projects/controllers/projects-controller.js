'use strict';

angular.module('dick.groups')
    .controller('ProjectsController', ['ProjectsResource', '$scope', 'MetadataService', 'rx', 'settings',
        function (projectsResource, $scope, metadataService, rx, settings) {
            metadataService.setTitle('Projects');
            metadataService.setPageTitle('Projects');
            var page = 0, size = 20;
            var load = function (filter) {
                projectsResource.query(filter).$promise.then(function (data) {
                    $scope.projects = (data);
                    if (data.length !== 0) {
                        page++;
                    }
                });
            };

            $scope.$watch('name', function (value) {
                page = 0;
                load({page: page, size: size, name: value});
            });

            load({page: page, size: size});

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
