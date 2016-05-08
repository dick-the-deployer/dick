'use strict';

angular.module('dick.errors')
        .controller('AuthController', ['$scope', '$http', '$state', '$rootScope', 'toaster',
            function ($scope, $http, $state, $rootScope, toaster) {
                var authenticate = function(credentials, successCallback, failureCallback) {

                    var headers = {authorization : "Basic "
                        + btoa(credentials.username + ":" + credentials.password)
                    };

                    $http.get('/api/user', {headers : headers}).then(function(response) {
                        $rootScope.user = response.data;
                        successCallback && successCallback();
                    }, function() {
                        failureCallback && failureCallback();
                    });
                };

                $scope.credentials = {};
                $scope.login = function() {
                    authenticate($scope.credentials, function() {
                        $state.go('dick.projects');
                    }, function() {
                        toaster.add({
                            type: 'danger',
                            message: 'Wrong login or password'
                        });
                    });
                };
            }
        ]);
