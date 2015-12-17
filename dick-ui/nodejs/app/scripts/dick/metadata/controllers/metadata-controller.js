'use strict';

angular.module('dick.metadata')
        .controller('MetadataCtrl', ['MetadataService', '$scope',
            function (metadataService, $scope) {
                $scope.metadata = metadataService;
            }
        ]);
