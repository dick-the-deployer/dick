'use strict';

angular.module('dick.groups')
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
                        });
            }
        ]);
