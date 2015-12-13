module.exports = {
    javascript: {
        files: [
            'app/scripts/**/*.js',
            'test/**/*.js'
        ],
        options: {
            livereload: true
        }
    },
    gruntfile: {
        files: ['Gruntfile.js'],
    },
    html: {
        options: {
            livereload: true
        },
        files: [
            'app/index.html',
            'app/views/**/*.html'
        ]

    },
    livereload: {
        options: {
            livereload: true
        },
        files: [
            'app/images/**/*.{jpg,jpeg,gif,png}'
        ]

    }
};
