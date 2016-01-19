'use strict';

angular.module('dick.builds')
    .controller('BuildController', ['MetadataService', '$scope', 'toaster', 'BuildsResource', '$window', '$stateParams',
        '$location', 'JobBuildsResource', 'rx', 'settings', '$rootScope', '$anchorScroll', '$timeout',
        function (metadataService, $scope, toaster, buildsResource, $window, $stateParams, $location, jobBuildsResource,
                  rx, settings, $rootScope, $anchorScroll, $timeout) {
            if ($window.angular.isUndefined($stateParams.id) ||
                $stateParams.id === '' || $window.angular.isUndefined($stateParams.namespace) ||
                $stateParams.namespace === '' || $window.angular.isUndefined($stateParams.name) ||
                $stateParams.name === '' || $window.angular.isUndefined($stateParams.stage) ||
                $stateParams.stage === '') {
                $location.path('/');
                return;
            }
            var id = $stateParams.id, name = $stateParams.name,
                namespace = $stateParams.namespace, stage = $stateParams.stage;
            metadataService.setTitle(
                '<a href="/groups/' + namespace + '">' + namespace + '</a>' + '</a> / ' +
                '<a href="/' + namespace + '/' + name + '">' + name + '</a> - ' + stage
            );
            metadataService.setPageTitle(namespace + ' / ' + name + ' - ' + stage);

            $scope.selectJob = function (newJob) {
                $scope.job = newJob;
            }

            $scope.followLog = function () {
                $scope.following = true;
                $location.hash('bottom');
                $anchorScroll();
            }

            var deferred;
            var subscriber = rx.Observable.interval(settings.interval)
                .filter(function () {
                    return !deferred || deferred.$resolved;
                })
                .safeApply($scope, function () {
                    deferred = buildsResource.getSilently({id: id});
                    deferred.$promise.then(function (data) {
                        $scope.build = data;
                        $scope.stage = data.stages.find(function (candidate) {
                            return candidate.name === stage;
                        });
                        var newJob = $scope.stage.jobBuilds.find(function (candidate) {
                            return candidate.name === $scope.job.name;
                        });
                        if (newJob.status !== $scope.job.status) {
                            $scope.job = newJob;
                        }
                    });
                })
                .subscribe();

            $scope.$on("$destroy", function () {
                subscriber.dispose();
            });

            var outputSubscriber;

            function processOutput() {
                if (outputSubscriber) {
                    outputSubscriber.dispose();
                }
                if ($scope.job && $scope.job.status !== 'READY' && $scope.job.status !== 'WAITING') {
                    if ($scope.job.status === 'IN_PROGRESS') {
                        jobBuildsResource.chunks({id: $scope.job.id}).$promise.then(function (data) {
                            $scope.output = data;
                        });

                        outputSubscriber = rx.Observable.interval(settings.interval)
                            .safeApply($scope, function () {
                                var creationDate = $scope.output.slice(-1).pop() ? $scope.output.slice(-1).pop().creationDate : null;
                                jobBuildsResource.chunksSilently({
                                    id: $scope.job.id,
                                    creationDate: creationDate
                                }).$promise.then(function (data) {
                                    $scope.output = $scope.output.concat(data);
                                    if ($scope.following) {
                                        $timeout(function () {
                                            $location.hash('bottom');
                                            $anchorScroll();
                                        }, 100);
                                    }
                                });
                            })
                            .subscribe();

                        $scope.$on("$destroy", function () {
                            outputSubscriber.dispose();
                        });
                    } else {
                        jobBuildsResource.output({id: $scope.job.id}).$promise.then(function (data) {
                            $scope.output = data;
                        });
                    }
                }
            }

            $scope.$watch('job', function () {
                processOutput();
            });
            $scope.$watch('job.status', function () {
                processOutput();
            });

            buildsResource.get({id: id}).$promise.then(function (data) {
                $scope.build = data;
                $rootScope.project = data.project;
                $scope.stage = data.stages.find(function (candidate) {
                    return candidate.name === stage;
                });
                $scope.job = $scope.stage.jobBuilds[0];
            });
        }
    ]);
