'use strict';

angular.module('dick.builds')
    .filter('jobStatus', function () {
        return function (input) {
            if (input === 'STOPPED') {
                return 'Stopped';
            } else if (input === 'DEPLOYED') {
                return 'Deployed';
            } else if (input === 'WAITING') {
                return 'Waiting';
            } else if (input === 'IN_PROGRESS') {
                return 'In Progress';
            } else if (input === 'FAILED') {
                return 'Failed';
            } else if (input === 'READY') {
                return 'Ready';
            } else {
                return input;
            }
        };
    });