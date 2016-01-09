'use strict';

angular.module('dick.groups')
    .controller('ConfirmProjectRemoveModal', ['$scope', '$uibModalInstance', 'project', 'ProjectsResource', 'toaster', '$location',
        function ($scope, $uibModalInstance, project, projectsResource, toaster, $location) {

            $scope.project = project;
            $scope.regexp = project.name;

            $scope.remove = function () {
                projectsResource.remove({id: $scope.project.id})
                    .$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Project \'' + $scope.project.name + '\' was removed.'
                    });
                    $uibModalInstance.close();
                    $location.path('/');
                }, function () {
                    $uibModalInstance.close();
                })
            }
        }
    ]);