'use strict';

angular.module('dick.errors')
    .config(['$httpProvider',
        function ($httpProvider) {
            $httpProvider.interceptors.push('StaticErrorsInterceptor');
        }
    ])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('dick.403', {
                url: '/403',
                templateUrl: '/views/errors/403.html'
            })
            .state('dick.404', {
                url: '/404',
                templateUrl: '/views/errors/404.html'
            })
            .state('dick.500', {
                url: '/500',
                templateUrl: '/views/errors/500.html'
            });
    }
    ]);
