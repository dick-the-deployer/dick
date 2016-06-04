'use strict';

angular.module('dick')
        .config(['$stateProvider', function ($stateProvider) {
                $stateProvider
                        .state('dick', {
                            templateUrl: '/views/layout.html'
                        })
                        .state('loader', {
                            url: '/',
                            templateUrl: '/views/loader.html',
                            controller: 'LoaderCtrl'
                        });
            }
        ])
        .config(['$urlRouterProvider', '$locationProvider', '$httpProvider',
            function ($urlRouterProvider, $locationProvider, $httpProvider) {

                if (window.history && window.history.pushState) {
                    $locationProvider.html5Mode({
                        enabled: true,
                        requireBase: false
                    });
                }
                
                $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
                $urlRouterProvider.otherwise('/projects');
            }
        ])
        .run(['$rootScope', '$state', '$stateParams',
            function ($rootScope, $state, $stateParams) {
                $rootScope.$state = $state;
                $rootScope.$stateParams = $stateParams;
                $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
                    $rootScope.project = null;
                    $rootScope.group = null;
                });
            }
        ]);
