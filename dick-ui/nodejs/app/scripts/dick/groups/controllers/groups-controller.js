'use strict';

angular.module('dick.groups')
        .controller('GroupsController', ['GroupsResource', '$scope', 'MetadataService',
            function (groupsResource, $scope, metadataService) {
                metadataService.setTitle('Groups');
                metadataService.setPageTitle('Groups');
                var page = 0, size = 20;

                groupsResource.query({page: page, size: size}).$promise.then(function (data) {
                    $scope.groups = data;
                    if (data.length !== 0) {
                        page++;
                    }
                });

                $scope.loadMore = function () {
                    groupsResource.query({page: page, size: size}).$promise.then(function (data) {
                        $scope.groups = $scope.groups.concat(data);
                        if (data.length !== 0) {
                            page++;
                        }
                    });
                };
            }
        ]);
