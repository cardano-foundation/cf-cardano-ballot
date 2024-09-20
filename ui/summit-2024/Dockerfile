FROM node:18 AS build
ARG VITE_VERSION=0.0.1
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
EXPOSE 80
WORKDIR /app

COPY --from=build /app/dist /usr/share/nginx/html

RUN rm -rf /etc/nginx/conf.d
COPY docker-assets/etc/nginx/conf.d /etc/nginx/conf.d
COPY docker-assets/bin/entrypoint.sh /app/entrypoint.sh
COPY package.json /app/

CMD ["sh", "/app/entrypoint.sh"]
