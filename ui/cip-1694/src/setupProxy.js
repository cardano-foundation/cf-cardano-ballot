const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  if (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') {
    app.use(
      '/api/verification/verify-vote',
      createProxyMiddleware({
        target: 'http://localhost:9092',
        changeOrigin: true,
      })
    );
    app.use(
      '/api/vote/**',
      createProxyMiddleware({
        target: 'http://localhost:9091',
        changeOrigin: true,
      })
    );
    app.use(
      '/api/leaderboard/**',
      createProxyMiddleware({
        target: 'http://localhost:9090',
        changeOrigin: true,
      })
    );
    app.use(
      '/api',
      createProxyMiddleware({
        target: 'http://localhost:9090',
        changeOrigin: true,
      })
    );
  }
};
