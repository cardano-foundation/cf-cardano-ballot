FROM node:18-alpine AS builder

ARG REACT_APP_VERSION
ARG REACT_APP_VOTING_APP_SERVER_URL=http://localhost:3000
ARG REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL=http://localhost:3000
ARG REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL=http://localhost:3000
ARG REACT_APP_USER_VERIFICATION_SERVER_URL=http://localhost:3000

ENV NODE_ENV="production" \
    REACT_APP_VOTING_APP_SERVER_URL=${REACT_APP_VOTING_APP_SERVER_URL} \
    REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL=${REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL} \
    REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL=${REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL} \
    REACT_APP_USER_VERIFICATION_SERVER_URL=${REACT_APP_USER_VERIFICATION_SERVER_URL} \
    REACT_APP_TARGET_NETWORK="PREPROD" \
    REACT_APP_EVENT_ID="CF_SUMMIT_2023_24DC" \
    REACT_APP_SUPPORTED_WALLETS="flint,eternl,nami,typhon,yoroi,nufi,gerowallet,lace"

# Add a work directory
WORKDIR /app
# Cache and Install dependencies
COPY package.json .
RUN npm install --production
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
