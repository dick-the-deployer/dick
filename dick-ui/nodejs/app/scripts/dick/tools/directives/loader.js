'use strict';

angular.module('dick.tools')
        .directive('loader', function () {
            return {
                restrict: 'E',
                scope: {
                    waitingFor: '=on'
                },
                template: '<div ng-hide="waitingFor" ng-include="\'/views/parts/loader.html\'"></div>'
            };
        });