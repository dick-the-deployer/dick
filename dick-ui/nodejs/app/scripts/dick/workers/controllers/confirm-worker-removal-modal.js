'use strict';

angular.module('dick.groups')
    .controller('ConfirmWorkerRemoveModal', ['$scope', '$uibModalInstance', 'worker', 'WorkersResource', 'toaster', '$location', 'statusCode',
        function ($scope, $uibModalInstance, worker, workersResource, toaster, $location, statusCode) {

            $scope.worker = worker;
            $scope.regexp = worker.name;

            $scope.remove = function () {
                workersResource.remove({name: $scope.worker.name})
                    .$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Worker \'' + $scope.worker.name + '\' was removed.'
                    });
                    $uibModalInstance.close();
                    $location.path('/');
                }, function (response) {
                    if (response.status === statusCode.preconditionFailed) {
                        toaster.add({
                            type: 'warning',
                            message: 'Cannot remove BUSY worker'
                        });
                    }
                    $uibModalInstance.close();
                })
            }
        }
    ]);