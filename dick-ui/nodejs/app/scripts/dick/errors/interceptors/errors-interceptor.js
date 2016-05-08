angular.module('dick.errors')
    .factory('StaticErrorsInterceptor', ['$location', 'statusCode', '$q', '$log',
        function ($location, statusCode, $q, $log) {

            var interceptor = {
                responseError: function (response) {
                    $log.info("Received response error: " + response.status);
                    if (response.status === statusCode.unauthorized) {
                        $location.path('/login');
                    } else if (response.status === statusCode.forbidden) {
                        $location.path('/403');
                    } else if (response.status === statusCode.notFound) {
                        $location.path('/404');
                    } else if (response.status === statusCode.serverError) {
                        $location.path('/500');
                    }
                    return $q.reject(response);
                }
            };

            return interceptor;
        }
    ]);
