'use strict';

angular.module('dick.groups')
    .config(['$urlRouterProvider', function ($urlRouterProvider) {

        $urlRouterProvider.when('/groups/new', function ($state) {
            $state.go('dick.groups-new');
        })
    }])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('dick.groups', {
                url: '/groups',
                controller: 'GroupsController',
                templateUrl: '/views/groups/groups.html'
            })
            .state('dick.groups-new', {
                url: '/groups/new',
                controller: 'NewGroupController',
                templateUrl: '/views/groups/new.html'
            })
            .state('dick.group-details', {
                url: '/groups/:name',
                controller: 'GroupController',
                templateUrl: '/views/groups/group.html'
            })
            .state('dick.group-edit', {
                url: '/groups/:name/edit',
                controller: 'GroupEditController',
                templateUrl: '/views/groups/group-edit.html'
            });
    }
    ]);
