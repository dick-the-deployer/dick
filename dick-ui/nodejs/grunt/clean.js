module.exports = {
    options: {
        force: true
    },
    all: {
        files: [{
            dot: true,
            src: [
                '.tmp',
                'app/.tmp',
                '../target/dist*'
            ]
        }]
    }
};
