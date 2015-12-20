'use strict';

angular.module('dick.builds')
        .controller('HookController', ['HooksResource', '$scope', 'toaster',
            function (hooksResource, $scope, toaster) {
                $scope.startBuild = function (project) {

                    hooksResource.save({ref: project.ref, name: project.name, sha: 'HEAD'}).$promise.then(function () {
                        toaster.add({
                            type: 'success',
                            message: 'Build was successfully queued.'
                        });
                    });
                };

                $scope.checkProgress = function () {
                    if (project.lastBuild.stages.indexOf(stage) < project.lastBuild.stages.indexOf(project.lastBuild.currentStage)) {
                        '100%';
                    }
                }
            }
        ]);
