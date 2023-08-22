FROM node:18-alpine AS builder
ENV NODE_ENV production

# Add a work directory
WORKDIR /app
# Cache and Install dependencies
COPY package.json .
RUN npm install --quiet --production
# Copy app files
COPY . .
# Build the app
RUN npm run build

# Bundle static assets with nginx
FROM nginx:1.25.2-alpine as production
ENV NODE_ENV production
# Copy built assets from builder
COPY --from=builder /app/build /usr/share/nginx/html
# Add your nginx.conf
COPY docker-assets/nginx.conf /etc/nginx/conf.d/default.conf
# Expose port
EXPOSE 80
# Start nginx
CMD ["nginx", "-g", "daemon off;"]
