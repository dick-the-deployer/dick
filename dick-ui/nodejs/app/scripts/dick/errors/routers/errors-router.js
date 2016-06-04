'use strict';

angular.module('dick.errors')
    .config(['$httpProvider',
        function ($httpProvider) {
            $httpProvider.interceptors.push('StaticErrorsInterceptor');
        }
    ])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('401', {
                url: '/login',
                templateUrl: '/views/login.html',
                controller: 'AuthController'
            })
            .state('403', {
                url: '/403',
                templateUrl: '/views/errors/403.html'
            })
            .state('404', {
                url: '/404',
                templateUrl: '/views/errors/404.html'
            })
            .state('500', {
                url: '/500',
                templateUrl: '/views/errors/500.html'
            });
    }
    ]);
