module.exports = {
    dist: {
        files: [
            {
                expand: true,
                dot: true,
                cwd: 'app',
                dest: '../target/dist/',
                src: [
                    '*.{ico,png,txt}',
                    'index.html',
                    '*.xml',
                    'views/**/*.html',
                    'img/**/*',
                    'styles/images/**/*',
                    'fonts/**/*'
                ]
            }
        ]
    }
};
