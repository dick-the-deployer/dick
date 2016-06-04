'use strict';

angular.module('dick')
    .controller('LoaderCtrl', ['$rootScope', 'UserResource', '$state', 'rx',
        function ($rootScope, UserResource, $state, rx) {
            
            UserResource.get().$promise.then(function(data) {
                $rootScope.user = data;
                rx.Observable.just().delay(800).subscribe(function() {
                    $state.go('dick.projects');
                });
            });
        }
    ]);
