'use strict';

angular.module('dick.metadata')
        .run(['$rootScope', 'MetadataService',
            function ($rootScope, metadataService) {
                $rootScope.$on('$stateChangeSuccess', function (event, toState) {
                    metadataService.reset();
                });
            }
        ]);
