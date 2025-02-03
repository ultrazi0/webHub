FROM --platform=$BUILDPLATFORM node:current-alpine AS dev
WORKDIR /app

COPY package.json /app/package.json
COPY package-lock.json /app/package-lock.json

RUN npm ci

COPY public /app/public
COPY src /app/src

ENV CI=true
ENV PORT=3000

CMD [ "npm", "start" ]

FROM dev AS build

RUN [ "npm", "run", "build" ]

FROM nginx:1.13-alpine

COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build /usr/share/nginx/html
