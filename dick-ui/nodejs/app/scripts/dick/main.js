'use strict';

angular.module('dick')
    .controller('MainCtrl', ['$scope',
        function ($scope) {
            // config
            $scope.app = {
                name: 'dick',
                version: '1.0.0'
            };
        }
    ])
    .constant('statusCode', {
        notAcceptable: 406,
        forbidden: 403,
        serverError: 500,
        notFound: 404,
        preconditionFailed: 412,
        unauthorized: 403,
        ok: 200
    });
