'use strict';

angular.module('dick.groups')
        .controller('StartWithOptionsModal', ['$scope', '$uibModalInstance', 'project', 'BuildsResource', 'toaster', 'statusCode',
            function ($scope, $uibModalInstance, project, buildsResource, toaster, statusCode) {

                $scope.project = project;
                $scope.environmentVariables = [];

                $scope.addVariable = function () {
                    $scope.environmentVariables.push({});
                };
                $scope.removeVariable = function (variable) {
                    $scope.environmentVariables.splice($scope.environmentVariables.indexOf(variable), 1);
                };

                $scope.queue = function () {
                    buildsResource.save({
                        namespace: project.namespace,
                        name: project.name,
                        sha: $scope.sha,
                        environmentVariables: $scope.environmentVariables
                    }).$promise.then(function () {
                        toaster.add({
                            type: 'success',
                            message: 'Build was successfully queued.'
                        });
                        $uibModalInstance.close();
                    }, function (response) {
                        if (response.status === statusCode.preconditionFailed) {
                            toaster.add({
                                type: 'warning',
                                message: 'Build already queued!'
                            });
                        } else if (response.status === statusCode.notAcceptable) {
                            toaster.add({
                                type: 'danger',
                                message: 'Unable to find specified commit!'
                            });
                        }
                        $uibModalInstance.close();
                    });
                };
            }
        ]);