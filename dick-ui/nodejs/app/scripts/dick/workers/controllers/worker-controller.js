'use strict';

angular.module('dick.workers')
    .controller('WorkerController', ['WorkersResource', '$scope', 'MetadataService', '$window', '$location', '$stateParams', '$uibModal',
        function (workersResource, $scope, metadataService, $window, $location, $stateParams, $uibModal) {
            metadataService.setTitle('Worker');
            metadataService.setPageTitle('Worker');

            if ($window.angular.isUndefined($stateParams.name) ||
                $stateParams.name === '') {
                $location.path('/');
                return;
            }
            var name = $stateParams.name;
            workersResource.get({name: name}).$promise
                .then(function (data) {
                    $scope.worker = data;
                    metadataService.setTitle('Worker - ' + data.name);
                    metadataService.setPageTitle('Worker - ' + data.name);
                })

            $scope.confirm = function () {
                $uibModal.open({
                    templateUrl: '/views/parts/modal/remove-worker.html',
                    controller: 'ConfirmWorkerRemoveModal',
                    resolve: {
                        worker: function () {
                            return $scope.worker;
                        }
                    }
                });
            };


        }
    ]);
