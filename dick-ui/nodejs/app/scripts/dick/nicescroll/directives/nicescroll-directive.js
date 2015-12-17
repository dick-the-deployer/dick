'use strict';

angular.module('dick.nicescroll')
    .directive('nicescroll', [
        function () {
            return {
                restrict: 'A',
                link: function (scope, el) {

                    var niceScrollbarConfig = {
                        cursoropacitymin: 0.4,
                        hidecursordelay: 300,
                        zindex: 1050
                    };

                    $(el).niceScroll(niceScrollbarConfig);

                }
            };
        }
    ]);
