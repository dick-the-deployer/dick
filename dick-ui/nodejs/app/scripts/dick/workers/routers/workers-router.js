'use strict';

angular.module('dick.workers')
        .config(['$stateProvider', function ($stateProvider) {
                $stateProvider
                        .state('dick.workers', {
                            url: '/workers',
                            controller: 'WorkersController',
                            templateUrl: '/views/workers/workers.html'
                        });
            }
        ]);
