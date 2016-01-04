'use strict';

angular.module('dick.groups')
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('dick.build', {
                url: '/:namespace/:name/:id/:stage',
                controller: 'BuildController',
                templateUrl: '/views/builds/build.html'
            });
    }
    ]);
