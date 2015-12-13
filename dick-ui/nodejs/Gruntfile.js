'use strict';

module.exports = function (grunt) {
	
    var gtx = require('gruntfile-gtx').wrap(grunt);
    gtx.loadAuto();

    var gruntConfig = require('./grunt');
    gruntConfig.package = require('./package.json');
    gtx.config(gruntConfig);

    gtx.alias('build:dev', ['bower-install-simple']);
    gtx.alias('serve', ['build:dev', 'configureProxies', 'connect:livereload', 'watch']);
    gtx.alias('build:dist', ['clean:all',
        'copy:dist',
        'useminPrepare',
        'concat',
        'cssmin',
        'uglify',
        'filerev',
        'usemin',
        'htmlmin'
    ]);
    gtx.alias('release', ['build:dev', 'build:dist']);

    gtx.finalise();
};
