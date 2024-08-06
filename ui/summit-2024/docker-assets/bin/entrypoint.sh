#!/bin/sh
ENV_FILE_PATH=/usr/share/nginx/html/envfile.js
export VITE_VERSION=$(grep -m 1 "version" package.json | awk '{print $2}' | sed 's/[",]//g')

echo "window._env_ = {" > $ENV_FILE_PATH
for var in $(env | grep ^VITE_); do
  echo "  \"${var%%=*}\": \"${var#*=}\"," >> $ENV_FILE_PATH
done
echo "};" >> $ENV_FILE_PATH

exec nginx -g "daemon off;" $@
