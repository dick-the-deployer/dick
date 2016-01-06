'use strict';

angular.module('dick.projects')
    .config(['$urlRouterProvider', function ($urlRouterProvider) {

        $urlRouterProvider.when('/projects/new', function ($state) {
            $state.go('dick.projects-new');
        })
    }])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('dick.projects', {
                url: '/projects',
                controller: 'ProjectsController',
                templateUrl: '/views/projects/projects.html'
            })
            .state('dick.projects-new', {
                url: '/projects/new',
                controller: 'NewProjectController',
                templateUrl: '/views/projects/new.html'
            });
    }]);
