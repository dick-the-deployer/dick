'use strict';

angular.module('dick.nicescroll')
        .directive('infiniteScroll', function () {
            return function (scope, elm, attr) {
                var raw = angular.element($('.scrollable'))[0];
                angular.element($('.scrollable')).bind('scroll', function () {
                    if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight - 50) {
                        scope.$apply(attr.infiniteScroll);
                    }
                });
            };
        });
