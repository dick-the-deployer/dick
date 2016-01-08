'use strict';

angular.module('dick.groups')
        .controller('NewProjectController', ['ProjectsResource', '$scope', 'MetadataService', 'toaster', 'statusCode', '$state', 'NamespacesResource',
            function (projectsResource, $scope, metadataService, toaster, statusCode, $state, namespacesResource) {
                metadataService.setTitle('New Project');
                metadataService.setPageTitle('New Project');
                $scope.prefix = document.location.origin;
                $scope.model = {environmentVariables: []};

                namespacesResource.get().$promise.then(function (data) {
                    $scope.namespaces = data.namespaces;
                });

                $scope.addVariable = function () {
                    $scope.model.environmentVariables.push({});
                };
                $scope.removeVariable = function (variable) {
                    $scope.model.environmentVariables.splice($scope.model.environmentVariables.indexOf(variable), 1);
                };

                $scope.create = function () {
                    projectsResource.save($scope.model, function () {
                        toaster.add({
                            type: 'success',
                            message: 'Project \'' + $scope.model.name + '\' was successfully created.'
                        });
                        $state.go('dick.projects');
                    }, function (response) {
                        if (response.status === statusCode.preconditionFailed) {
                            toaster.add({
                                type: 'danger',
                                message: 'Name has already been taken'
                            });
                        } else if (response.status === statusCode.notAcceptable) {
                            toaster.add({
                                type: 'danger',
                                message: 'Cannot clone repository ' + $scope.model.repository
                            });
                        }
                    });
                };
            }
        ]);
