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
  }
};
