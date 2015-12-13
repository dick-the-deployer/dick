'use strict';

angular.module('dick')
    .controller('MainCtrl', ['$scope', '$window', '$timeout', '$rootScope',
        function ($scope, $window, $timeout) {

            // config
            $scope.app = {
                name: 'dick',
                version: '1.0.0'
            };


            $timeout(function () {
                $(document).ready(function () {
                    $scope.time = moment().startOf('day').fromNow();
                });
            }, 1000);
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
