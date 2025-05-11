const { createProxyMiddleware } = require("http-proxy-middleware");

const proxyHost = process.env.REACT_APP_PROXY_HOST;

module.exports = function(app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: proxyHost ? proxyHost + "/api" : "http://localhost:8080/api",
            changeOrigin: true,
        })
    )
}