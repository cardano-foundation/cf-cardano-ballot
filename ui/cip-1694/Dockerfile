FROM node:18-alpine AS builder

# Add a work directory
WORKDIR /app
# Cache and Install dependencies
COPY package.json .
RUN npm install --quiet --production
# Copy app files
COPY . .
# Build the app
RUN npm run build

ARG REACT_APP_VOTING_APP_SERVER_URL=http://localhost:3000
ARG REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL=http://localhost:3000
ARG REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL=http://localhost:3000
ARG REACT_APP_EVENT_ID=CIP-1694_Pre_Ratification_4619
ARG REACT_APP_CATEGORY_ID=CIP-1694_Pre_Ratification_4619
ARG REACT_APP_GOOGLE_FORM_URL=https://docs.google.com/forms/u/0/d/e/1FAIpQLSe-dSU0-bi8bjk0wiOaRXj02nxYLvgnpKBgc-eYGRpPYO-y5Q
ARG REACT_APP_GOOGLE_FORM_VOTE_CONTEXT_INPUT_NAME=entry.75173622

# Bundle static assets with nginx
FROM nginx:1.25.2-alpine as production
WORKDIR /app
ENV NODE_ENV="production" \
    REACT_APP_VOTING_APP_SERVER_URL=${REACT_APP_VOTING_APP_SERVER_URL} \
    REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL=${REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL} \
    VOTING_VERIFICATION_APP_SERVER_URL=${VOTING_VERIFICATION_APP_SERVER_URL} \
    REACT_APP_TARGET_NETWORK="PREPROD" \
    REACT_APP_EVENT_ID=${REACT_APP_EVENT_ID} \
    REACT_APP_CATEGORY_ID=${REACT_APP_CATEGORY_ID} \
    REACT_APP_GOOGLE_FORM_URL=${REACT_APP_GOOGLE_FORM_URL} \
    REACT_APP_GOOGLE_FORM_VOTE_CONTEXT_INPUT_NAME=${REACT_APP_GOOGLE_FORM_VOTE_CONTEXT_INPUT_NAME} \
    REACT_APP_SUPPORTED_WALLETS="flint,eternl,nami,typhoncip30,yoroi,nufi,gerowallet,lace,begin,cardwallet"


# Copy built assets from builder
COPY --from=builder /app/build /usr/share/nginx/html
# Add your nginx.conf
COPY docker-assets/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker-assets/entrypoint.sh /app/entrypoint.sh
COPY env.global.tmp.js /app/env.global.tmp.js
COPY package.json /app/
# Expose port
EXPOSE 80
# Start nginx
CMD ["sh", "/app/entrypoint.sh"]
