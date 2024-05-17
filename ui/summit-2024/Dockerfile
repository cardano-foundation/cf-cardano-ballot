FROM node:18-alpine AS builder

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
WORKDIR /app
ENV NODE_ENV="production" \
    REACT_APP_VOTING_APP_SERVER_URL=${REACT_APP_VOTING_APP_SERVER_URL} \
    REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL=${REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL} \
    REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL=${REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL} \
    REACT_APP_USER_VERIFICATION_SERVER_URL=${REACT_APP_USER_VERIFICATION_SERVER_URL} \
    REACT_APP_TARGET_NETWORK="mainnet" \
    REACT_APP_USING_FIXTURES=1 \
    REACT_APP_EVENT_ID="CF_SUMMIT_2023_025E" \
    REACT_APP_COMMIT_HASH="INJECT_COMMIT_HASH" \
    REACT_APP_DISCORD_CHANNEL_URL="https://discord.gg/FeCbA2wYF8" \
    REACT_APP_DISCORD_BOT_URL="https://discord.gg/65Hq3gqFwE" \
    REACT_APP_DISCORD_SUPPORT_CHANNEL_URL="https://discord.gg/svAcdYjMXx" \
    REACT_APP_SUPPORTED_WALLETS="flint,eternl,nami,typhoncip30,yoroi,nufi,gerowallet,lace"

# Copy built assets from builder
COPY --from=builder /app/build /usr/share/nginx/html
# Add your nginx.conf
COPY docker-assets/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker-assets/bin/entrypoint.sh /app/entrypoint.sh
COPY env.global.tmp.js /app/env.global.tmp.js
COPY package.json /app/
# Expose port
EXPOSE 80
# Start nginx
CMD ["sh", "/app/entrypoint.sh"]
