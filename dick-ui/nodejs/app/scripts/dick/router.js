'use strict';

angular.module('dick')
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('dick', {
                templateUrl: '/views/layout.html'
            })
            .state('dick.welcome', {
                url: '/',
                views: {
                    '': {
                        templateUrl: '/views/welcome.html',
                    },
                    'cta': {templateUrl: '/views/parts/landing-cta.html'}
                },
            });
    }
    ])
    .config(['$urlRouterProvider', '$locationProvider',
        function ($urlRouterProvider, $locationProvider) {

            if (window.history && window.history.pushState) {
                $locationProvider.html5Mode({
                    enabled: true,
                    requireBase: false
                });
            }

            $urlRouterProvider.otherwise('/');
        }
    ])
    .run(['$rootScope', '$state', '$stateParams',
        function ($rootScope, $state, $stateParams) {
            $rootScope.$state = $state;
            $rootScope.$stateParams = $stateParams;
        }
    ]);
