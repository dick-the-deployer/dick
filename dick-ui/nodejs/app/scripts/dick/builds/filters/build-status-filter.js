'use strict';

angular.module('dick.builds')
    .filter('buildStatus', function () {
        return function (input) {
            if (input === 'STOPPED') {
                return 'Stopped';
            } else if (input === 'DEPLOYED') {
                return 'Deployed';
            } else if (input === 'DEPLOYED_STAGE') {
                return 'Deployed Stage';
            } else if (input === 'IN_PROGRESS') {
                return 'In progress';
            } else if (input === 'FAILED') {
                return 'Failed';
            } else {
                return input;
            }
        };
    });