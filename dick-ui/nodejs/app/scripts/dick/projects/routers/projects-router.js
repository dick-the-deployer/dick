'use strict';

angular.module('dick.projects')
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
            }
        ]);
