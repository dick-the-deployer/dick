'use strict';

angular.module('dick.groups')
        .controller('GroupsController', ['GroupsResource', '$scope', 'MetadataService', 'toaster', 'statusCode',
            function (groupsResource, $scope, metadataService, toaster, statusCode) {
                metadataService.setTitle('Groups');
                var page = 0, size = 20;

                groupsResource.get({page: page, size: size}).$promise.then(function (data) {
                    $scope.groups = (data.content);
                    if (data.content.length !== 0) {
                        page++;
                    }
                });

                $scope.loadMore = function () {
                    groupsResource.get({page: page, size: size}).$promise.then(function (data) {
                        $scope.groups = $scope.groups.concat(data.content);
                        if (data.content.length !== 0) {
                            page++;
                        }
                    });
                };



            }
        ]);
