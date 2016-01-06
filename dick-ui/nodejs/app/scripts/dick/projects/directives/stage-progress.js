'use strict';

angular.module('dick.projects')
    .directive('stageProgress', function () {
        return {
            restrict: 'E',
            scope: {
                stage: '=stage',
                project: '=project',
                build: '=build'
            },
            templateUrl: '/views/parts/progress.html'
        };
    });