var app = {
    initialize: function () {
        app.menuState();
    },
    menuState: function () {
        if (window.localStorage.getItem("_navigation") === "0") {
            app.openMenuState();
        } else {
            app.closeMenuState();
        }
    },
    closeMenuState: function () {
        $(".app").addClass("small-sidebar");
        $(".toggle-sidebar  i").removeClass("fa-angle-left").addClass("fa-angle-right");
    },
    openMenuState: function () {
        $(".app").removeClass("small-sidebar");
        $(".toggle-sidebar i").removeClass("fa-angle-right").addClass("fa-angle-left");
    }
};


!function ($) {
    "use strict";

    $(document).on("click", ".toggle-sidebar ", function (e) {
        e.preventDefault();

        if ($(".app").hasClass("small-sidebar")) {
            app.openMenuState();
            window.localStorage.setItem("_navigation", "0");
        } else {
            app.closeMenuState();
            window.localStorage.setItem("_navigation", "1");
        }
    });

    $(function () {
        app.initialize();
    });

}(window.jQuery);
