'use strict';

angular.module('dick')
    .controller('MainCtrl', ['$scope', '$rootScope', '$state', '$http',
        function ($scope, $rootScope, $state, $http) {
            $scope.logout = function() {
                $http.post('/logout', {}).finally(function() {
                    $rootScope.user = null;
                    $state.go('401');
                });
            };
        }
    ])
    .constant('settings', {
        interval: 2000
    })
    .constant('statusCode', {
        notAcceptable: 406,
        forbidden: 403,
        serverError: 500,
        notFound: 404,
        preconditionFailed: 412,
        unauthorized: 401,
        ok: 200
    });
