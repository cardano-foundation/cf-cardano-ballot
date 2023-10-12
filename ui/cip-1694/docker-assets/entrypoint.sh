#!/bin/bash
if [ -z "${REACT_APP_VERSION}" ]
then
  export REACT_APP_VERSION=$(grep -m 1 "version" package.json | awk '{print $2}' | sed 's/[",]//g')
fi
envsubst < env.global.tmp.js >/usr/share/nginx/html/static/js/env.global.js
nginx -g 'daemon off;'
