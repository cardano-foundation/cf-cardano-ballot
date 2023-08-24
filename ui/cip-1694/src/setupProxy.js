const { createProxyMiddleware } = require('http-proxy-middleware');



module.exports = function (app) {
  app.use(
    '/api/verification/verify-vote',
    createProxyMiddleware({
      target: 'https://verification-api.dev.cf-cip1694-preprod.eu-west-1.metadata.dev.cf-deployments.org',
      changeOrigin: true,
    })
  );
  app.use(
    '/api/vote/**',
    createProxyMiddleware({
      target: 'https://api.dev.cf-cip1694-preprod.eu-west-1.metadata.dev.cf-deployments.org',
      changeOrigin: true,
    })
  );
  app.use(
    '/api/leaderboard/**',
    createProxyMiddleware({
      target: 'https://follower-api.dev.cf-cip1694-preprod.eu-west-1.metadata.dev.cf-deployments.org',
      changeOrigin: true,
    })
  );
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'https://follower-api.dev.cf-cip1694-preprod.eu-west-1.metadata.dev.cf-deployments.org',
      changeOrigin: true,
    })
  );
};
