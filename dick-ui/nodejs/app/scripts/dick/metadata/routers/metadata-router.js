'use strict';

angular.module('dick.metadata')
    .run(['$rootScope', 'MetadataService',
        function ($rootScope, metadataService) {
            $rootScope.$on('$stateChangeSuccess', function (event, toState) {
                metadataService.reset();
            });
        }
    ])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('dick.help', {
                url: '/help',
                controller: 'HelpController',
                templateUrl: '/views/metadata/help.html'
            });
    }
    ]);
