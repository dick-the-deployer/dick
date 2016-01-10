'use strict';

angular.module('dick.builds')
    .controller('HookController', ['$scope', 'toaster', 'BuildsResource', 'statusCode',
        function ($scope, toaster, buildsResource, statusCode) {
            $scope.startBuild = function (project) {
                buildsResource.save({namespace: project.namespace, name: project.name}).$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Build was successfully queued.'
                    });
                }, function (response) {
                    if (response.status === statusCode.preconditionFailed) {
                        toaster.add({
                            type: 'warning',
                            message: 'Build already queued!'
                        });
                    }
                });
            };

            $scope.buildStage = function (build, stage) {
                var nextStageIndex = build.stages.indexOf(stage) + 1;
                buildStage(build, nextStageIndex);
            }

            $scope.rebuildStage = function (build, stage) {
                var nextStageIndex = build.stages.indexOf(stage);
                buildStage(build, nextStageIndex);
            }

            $scope.buildFirstStage = function (build) {
                buildStage(build, 0);
            }

            $scope.kill = function (build) {
                buildsResource.kill({id: build.id}).$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Build stopped'
                    })
                });
            }

            function buildStage(build, nextStageIndex) {
                buildsResource.save({
                    id: build.id,
                    stage: build.stages[nextStageIndex].name
                }).$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Stage was successfully queued.'
                    })
                });
            }

            $scope.checkProgress = function () {
                if (project.lastBuild.stages.indexOf(stage) < project.lastBuild.stages.indexOf(project.lastBuild.currentStage)) {
                    '100%';
                }
            }
        }
        ]);
