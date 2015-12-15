module.exports = {
    min: {
        options: {
            collapseWhitespace: true,
            collapseBooleanAttributes: true,
            removeCommentsFromCDATA: true,
            removeOptionalTags: true
        },
        files: [{
            expand: true,
            cwd: 'app/views/',
            src: ['*.html', '**/*.html'],
            dest: '../target/dist/views/',
            ext: '.html',
            extDot: 'first'
        }]
    }
};
