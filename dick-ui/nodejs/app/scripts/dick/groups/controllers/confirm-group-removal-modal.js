'use strict';

angular.module('dick.groups')
    .controller('ConfirmGroupRemoveModal', ['$scope', '$uibModalInstance', 'group', 'GroupsResource', 'toaster', '$location',
        function ($scope, $uibModalInstance, group, groupsResource, toaster, $location) {

            $scope.group = group;
            $scope.regexp = group.name;

            $scope.remove = function () {
                groupsResource.remove({id: $scope.group.id})
                    .$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Group \'' + $scope.group.name + '\' was removed.'
                    });
                    $uibModalInstance.close();
                    $location.path('/');
                }, function () {
                    $uibModalInstance.close();
                })
            }
        }
    ]);