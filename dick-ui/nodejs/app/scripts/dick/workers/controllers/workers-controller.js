'use strict';

angular.module('dick.workers')
        .controller('WorkersController', ['WorkersResource', '$scope', 'MetadataService',
            function (workersResource, $scope, metadataService) {
                metadataService.setTitle('Workers');
                var page = 0, size = 20;

                workersResource.query({page: page, size: size}).$promise.then(function (data) {
                    $scope.workers = data;
                    if (data.length !== 0) {
                        page++;
                    }
                });

                $scope.loadMore = function () {
                    workersResource.query({page: page, size: size}).$promise.then(function (data) {
                        $scope.workers = $scope.workers.concat(data);
                        if (data.length !== 0) {
                            page++;
                        }
                    });
                };
            }
        ]);
