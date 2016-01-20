'use strict';

angular.module('dick.groups')
    .controller('SearchWorkspaceController', ['$scope', '$rootScope', '$state',
        function ($scope, $rootScope, $state) {
            $scope.search = function () {
                $rootScope.filterName = $scope.name;
                $state.go('dick.projects');
            }
        }
    ]);
