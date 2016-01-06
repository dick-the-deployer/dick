'use strict';

angular.module('dick.metadata')
        .service('MetadataService', function () {
            return {
                title: 'Dashboard',
                pageTitle: 'Dick the Deployer',
                getTitle: function () {
                    return this.title;
                },
                getPageTitle: function () {
                    return this.pageTitle;
                },
                setTitle: function (newTitle) {
                    this.title = newTitle;
                },
                setPageTitle: function (newTitle) {
                    this.pageTitle = newTitle + ' | Dick the Deployer';
                },
                reset: function () {
                    this.title = 'Dashboard';
                    this.pageTitle = 'Dick the Deployer';
                }
            };
        });
