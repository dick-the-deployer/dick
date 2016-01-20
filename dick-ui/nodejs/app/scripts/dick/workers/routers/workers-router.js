'use strict';

angular.module('dick.workers')
    .config(['$urlRouterProvider', function ($urlRouterProvider) {

        $urlRouterProvider.when('/workers/:name', function ($state, $match) {
            $state.go('dick.workers-details', $match);
        })
    }])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('dick.workers', {
                url: '/workers',
                controller: 'WorkersController',
                templateUrl: '/views/workers/workers.html'
            })
            .state('dick.workers-details', {
                url: '/workers/:name',
                controller: 'WorkerController',
                templateUrl: '/views/workers/worker.html'
            });
    }
    ]);
