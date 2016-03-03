'use strict';

angular.module('dick.groups')
        .controller('GroupEditController', ['$window', '$scope', '$stateParams', '$location', 'MetadataService',
            'GroupsResource', '$state', 'toaster', '$rootScope', '$uibModal',
            function ($window, $scope, $stateParams, $location, metadataService, groupsResource,
                    $state, toaster, $rootScope, $uibModal) {
                if ($window.angular.isUndefined($stateParams.name) ||
                        $stateParams.name === '') {
                    $location.path('/');
                    return;
                }
                var name = $stateParams.name;
                metadataService.setTitle(name + ' - Settings');
                metadataService.setPageTitle(name + ' - Settings');

                groupsResource.get({name: name}).$promise.then(function (data) {
                    $scope.group = data;
                    $rootScope.group = data;
                });

                $scope.edit = function () {
                    groupsResource.update($scope.group)
                            .$promise.then(function () {
                                toaster.add({
                                    type: 'success',
                                    message: 'Group \'' + $scope.group.name + '\' was successfully saved.'
                                });
                                $state.go('dick.groups');
                            });
                };

                $scope.confirm = function () {
                    $uibModal.open({
                        templateUrl: '/views/parts/modal/remove-group.html',
                        controller: 'ConfirmGroupRemoveModal',
                        resolve: {
                            group: function () {
                                return $scope.group;
                            }
                        }
                    });
                };
            }
        ]);
