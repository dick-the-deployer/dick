'use strict';

angular.module('dick.groups')
        .controller('GroupsController', ['GroupsResource', '$scope', 'MetadataService', 'toaster', 'statusCode',
            function (groupsResource, $scope, metadataService, toaster, statusCode) {
                metadataService.setTitle('Groups');


                groupsResource.get({page: 0, size: 100}).$promise.then(function (data) {
                    $scope.groups = (data.content);
                });



            }
        ]);
