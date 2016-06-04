module.exports = {
    options: {
        port: 9001,
        hostname: 'localhost',
        livereload: 35729
    },
    proxies: [{
        context: ['/api/', '/logout'],
        host: 'localhost',
        port: 8080,
        changeOrigin: true,
        headers: {
            "host": 'localhost'
        }
    }],
    livereload: {
        options: {
            open: true,
            base: [
                'app'
            ],
            middleware: function (connect, options) {

                var middlewares = [];

                if (!Array.isArray(options.base)) {
                    options.base = [options.base];
                }


                // Setup the proxy
                middlewares.push(require('grunt-connect-proxy/lib/utils').proxyRequest);

                middlewares.push(require('connect-modrewrite')([
                    '^/$ /index.html',
                    '^/groups.*$ /index.html',
                    '^/projects.*$ /index.html',
                    '^/workers.*$ /index.html',
                    '^/asd.*$ /index.html',
                    '^/login$ /index.html',
                    '^/help$ /index.html',
                    '^/403$ /index.html',
                    '^/404$ /index.html',
                    '^/500$ /index.html'
                ]));

                var serveStatic = require('serve-static');
                options.base.forEach(function (base) {
                    // Serve static files.
                    middlewares.push(serveStatic(base));
                });

                return middlewares;

            }
        }
    }
};
