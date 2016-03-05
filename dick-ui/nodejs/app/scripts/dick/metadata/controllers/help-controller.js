'use strict';

angular.module('dick.metadata')
    .controller('HelpController', ['MetadataService', '$scope', 'InfoResource',
        function (metadataService, $scope, InfoResource) {
            metadataService.setPageTitle("Dick the Deployer");
            metadataService.setTitle("Help");

            InfoResource.get().$promise.then(
                function (data) {
                    $scope.info = data;
                }
            );
        }
    ]);
