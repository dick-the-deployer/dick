'use strict';

angular.module('dick.groups')
    .controller('ProjectController', ['ProjectsResource', '$scope', 'MetadataService', '$stateParams', '$window',
        'toaster', 'statusCode', '$location', 'NamespacesResource', '$uibModal', '$rootScope',
        function (projectsResource, $scope, metadataService, $stateParams, $window, toaster, statusCode,
                  $location, namespacesResource, $uibModal, $rootScope) {

            if ($window.angular.isUndefined($stateParams.namespace) ||
                $stateParams.namespace === '' || $window.angular.isUndefined($stateParams.name) ||
                $stateParams.name === '') {
                $location.path('/');
                return;
            }
            var name = $stateParams.name,
                namespace = $stateParams.namespace;

            metadataService.setTitle(
                '<a href="/groups/' + namespace + '">' + namespace + '</a>' + '</a> / ' +
                '<a href="/' + namespace + '/' + name + '">' + name + '</a> - Settings'
            );
            metadataService.setPageTitle(namespace + ' / ' + name + ' - Settings');

            projectsResource.get({namespace: namespace, name: name})
                .$promise.then(function (data) {
                $scope.project = data;
                $rootScope.project = data;
            });

            $scope.addVariable = function () {
                $scope.project.environmentVariables.push({});
            };
            $scope.removeVariable = function (variable) {
                $scope.project.environmentVariables.splice($scope.project.environmentVariables.indexOf(variable), 1);
            };

            $scope.edit = function () {
                projectsResource.update($scope.project)
                    .$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Project \'' + $scope.project.name + '\' was successfully saved.'
                    });
                })
            }

            $scope.rename = function () {
                projectsResource.rename($scope.project)
                    .$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Project \'' + $scope.project.name + '\' name was changed.'
                    });
                    $location.path('/' + $scope.project.namespace + '/' + $scope.project.name + '/edit')
                }, function (response) {
                    if (response.status === statusCode.preconditionFailed) {
                        toaster.add({
                            type: 'warning',
                            message: 'Name has already been taken'
                        });
                    }
                })
            }

            namespacesResource.get().$promise.then(function (data) {
                $scope.namespaces = data.namespaces;
            });
            $scope.transfer = function () {
                projectsResource.transfer($scope.project)
                    .$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Project \'' + $scope.project.name + '\' name was moved.'
                    });
                    $location.path('/' + $scope.project.namespace + '/' + $scope.project.name + '/edit')
                }, function (response) {
                    if (response.status === statusCode.preconditionFailed) {
                        toaster.add({
                            type: 'warning',
                            message: 'Name in chosen namespace has already been taken'
                        });
                    }
                })
            }

            $scope.confirm = function () {
                $uibModal.open({
                    templateUrl: '/views/parts/modal/remove-project.html',
                    controller: 'ConfirmProjectRemoveModal',
                    resolve: {
                        project: function () {
                            return $scope.project;
                        }
                    }
                });
            };
        }
    ]);
