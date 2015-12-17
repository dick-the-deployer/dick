'use strict';

angular.module('dick.groups')
        .controller('NewGroupController', ['GroupsResource', '$scope', 'MetadataService', 'toaster', 'statusCode', '$state',
            function (groupsResource, $scope, metadataService, toaster, statusCode, $state) {
                metadataService.setTitle('New group');
                $scope.prefix = document.location.origin;
                $scope.model = {};

                $scope.createGroup = function () {
                    groupsResource.save($scope.model, function () {
                        toaster.add({
                            type: 'success',
                            message: 'Group \'' + $scope.model.name + '\' was successfully created.'
                        });
                        $state.go('dick.groups');
                    }, function (response) {
                        if (response.status === statusCode.preconditionFailed) {
                            toaster.add({
                                type: 'danger',
                                message: 'Name has already been taken'
                            });
                        }
                    });
                };
            }
        ]);
