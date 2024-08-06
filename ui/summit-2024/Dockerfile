FROM node:18 AS build

# Add a work directory
WORKDIR /app
# Cache and Install dependencies
COPY package.json .
RUN npm install
# Copy app files
COPY . .
# Build the app
RUN npm run build

# Bundle static assets with nginx
FROM nginx:alpine as production
WORKDIR /app

# Copy built assets from builder
#COPY --from=builder /app/build /usr/share/nginx/html
## Add your nginx.conf
#COPY docker-assets/nginx.conf /etc/nginx/conf.d/default.conf
#COPY docker-assets/bin/entrypoint.sh /app/entrypoint.sh
#COPY env.global.tmp.js /app/env.global.tmp.js
#COPY package.json /app/
## Expose port
#EXPOSE 80
## Start nginx
#CMD ["sh", "/app/entrypoint.sh"]
