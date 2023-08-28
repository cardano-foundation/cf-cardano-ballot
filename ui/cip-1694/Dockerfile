FROM node:18-alpine AS builder
ENV NODE_ENV="production" \
    REACT_VOTING_APP_SERVER_URL="https://api.dev.cf-cip1694-preprod.eu-west-1.metadata.dev.cf-deployments.org" \
    REACT_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL="https://follower-api.dev.cf-cip1694-preprod.eu-west-1.metadata.dev.cf-deployments.org" \
    REACT_VOTING_VERIFICATION_APP_SERVER_URL="https://verification-api.dev.cf-cip1694-preprod.eu-west-1.metadata.dev.cf-deployments.org" \
    REACT_APP_TARGET_NETWORK="PREPROD" \
    REACT_APP_EVENT_ID="CIP-1694_Pre_Ratification_4619" \
    REACT_APP_CATEGORY_ID="CIP-1694_Pre_Ratification_4619" \
    REACT_APP_SUPPORTED_WALLETS="flint,eternl,nami,typhon,yoroi,nufi,gerowallet,lace"

RUN env

# Add a work directory
WORKDIR /app
# Cache and Install dependencies
COPY package.json .
RUN npm install --quiet --production
# Copy app files
COPY . .
# Build the app
RUN npm run react-inject-env && npm run build

# Bundle static assets with nginx
FROM nginx:1.25.2-alpine as production
ENV NODE_ENV="production"
# Copy built assets from builder
COPY --from=builder /app/build /usr/share/nginx/html
# Add your nginx.conf
COPY docker-assets/nginx.conf /etc/nginx/conf.d/default.conf
# Expose port
EXPOSE 80
# Start nginx
CMD ["nginx", "-g", "daemon off;"]
