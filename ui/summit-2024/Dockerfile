FROM node:18.16.0-alpine AS build

# Add a work directory
WORKDIR /app
# Cache and Install dependencies
COPY package.json .
RUN npm install --production
# Copy app files
COPY . .
# Build the app
RUN npm run build