'use strict';

angular.module('dick.builds')
    .controller('HookController', ['HooksResource', '$scope', 'toaster', 'BuildsResource',
        function (hooksResource, $scope, toaster, buildsResource) {
            $scope.startBuild = function (project) {

                hooksResource.save({ref: project.ref, name: project.name, sha: 'HEAD'}).$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Build was successfully queued.'
                    });
                });
            };

            $scope.buildStage = function (project, stage) {
                var nextStageIndex = project.lastBuild.stages.indexOf(stage) + 1;
                buildStage(project, nextStageIndex);
            }

            $scope.buildFirstStage = function (project) {
                buildStage(project, 0);
            }

            $scope.kill = function (project) {
                buildsResource.kill({id: project.lastBuild.id}).$promise.then(function () {
                    toaster.add({
                        type: 'success',
                        message: 'Build stopped'
                    })
                });
            }

            function buildStage(project, nextStageIndex) {
                buildsResource.save({
                    id: project.lastBuild.id,
                    stage: project.lastBuild.stages[nextStageIndex].name
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
