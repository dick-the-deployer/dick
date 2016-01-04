'use strict';

angular.module('dick.builds')
    .filter('jobStatusIcon', function () {
        return function (input) {
            if (input === 'STOPPED') {
                return '<i class="fa fa-stop"></i>';
            } else if (input === 'DEPLOYED') {
                return '<i class="fa fa-check-square-o"></i>';
            } else if (input === 'WAITING') {
                return '<i class="fa fa-circle-o"></i>';
            } else if (input === 'IN_PROGRESS') {
                return '<i class="fa fa-spin fa-spinner"></i>';
            } else if (input === 'FAILED') {
                return '<i class="fa fa-exclamation"></i>';
            } else if (input === 'READY') {
                return '<i class="fa fa-spin fa-spinner"></i>';
            } else {
                return input;
            }
        };
    });