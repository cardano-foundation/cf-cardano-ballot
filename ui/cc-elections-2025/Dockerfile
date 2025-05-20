FROM node:18 AS build

ARG VITE_BASE_URL
ENV VITE_BASE_URL=${VITE_BASE_URL}
ARG VITE_NETWORK_FLAG
ENV VITE_NETWORK_FLAG=${VITE_NETWORK_FLAG}
ARG VITE_IS_DEV
ENV VITE_IS_DEV=${VITE_IS_DEV}
ARG VITE_IPFS_GATEWAY
ENV VITE_IPFS_GATEWAY=${VITE_IPFS_GATEWAY}

WORKDIR /app

COPY package.json . package-lock.json ./

RUN npm install --legacy-peer-deps
RUN npm install --save-dev vitest @types/jest --legacy-peer-deps

COPY . .

RUN npx vite build --mode production


FROM nginx:alpine AS production
WORKDIR /usr/share/nginx/html

ENV NODE_ENV=production

RUN rm -rf ./*

COPY --from=build /app/dist .

COPY default.conf /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]
