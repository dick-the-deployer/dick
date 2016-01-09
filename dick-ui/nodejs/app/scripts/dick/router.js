'use strict';

angular.module('dick')
        .config(['$stateProvider', function ($stateProvider) {
                $stateProvider
                        .state('dick', {
                            templateUrl: '/views/layout.html'
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
