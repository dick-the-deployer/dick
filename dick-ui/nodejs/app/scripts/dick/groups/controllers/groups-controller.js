'use strict';

angular.module('dick.groups')
        .controller('GroupsController', ['GroupsResource', '$scope', 'MetadataService', 'toaster', 'statusCode',
            function (groupsResource, $scope, metadataService, toaster, statusCode) {
                metadataService.setTitle('Groups');
                var page = 0;

                groupsResource.get({page: page, size: 5}).$promise.then(function (data) {
                    $scope.groups = (data.content);
                });

                $scope.loadMore = function () {
                    page++;
                    groupsResource.get({page: page, size: 5}).$promise.then(function (data) {
                        $scope.groups = $scope.groups.concat(data.content);
                    });
                };



            }
        ]);
