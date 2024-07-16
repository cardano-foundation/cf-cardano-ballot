#!/bin/bash

CONFIG_DIR="${VERIFIER_CONFIG_DIR:-$(pwd)}"
STORE_DIR="${VERIFIER_STORE_DIR:-$(pwd)/store}"
INTERNAL_HOST=${VERIFIER_INTERNAL_HOST:-localhost}
EXTERNAL_HOST=${VERIFIER_EXTERNAL_HOST:-localhost}
URL="${VERIFIER_URL:-http://$EXTERNAL_HOST}"
PORT="${VERIFIER_PORT:-5666}"
ADMIN_PORT="${VERIFIER_ADMIN_PORT:-5667}"
if [[ -z "${VERIFIER_SALT}" ]]; then
  SALT=""
else
  SALT="--salt ${VERIFIER_SALT}"
fi

mkdir -p $CONFIG_DIR/keri/cf
cat > $CONFIG_DIR/keri/cf/verifier.json <<EOF
{
  "verifier": {
    "dt": "$(date -u +"%Y-%m-%dT%H:%M:%S.000000+00:00")",
    "curls": ["tcp://${INTERNAL_HOST}:$((PORT-1))/", "${URL}:${PORT}"]
  },
  "dt": "$(date -u +"%Y-%m-%dT%H:%M:%S.000000+00:00")",
  "iurls": [
  ]
}
EOF

cat > $CONFIG_DIR/verifier_cfg.json <<EOF
{
  "transferable": false,
  "wits": [],
  "icount": 1,
  "ncount": 1,
  "isith": "1",
  "nsith": "1"
}
EOF

kli init --name verifier --nopasscode  --config-dir $CONFIG_DIR --config-file verifier --base $STORE_DIR $SALT

kli incept --name verifier --alias verifier --config $CONFIG_DIR --file verifier_cfg.json --base $STORE_DIR

verifier start --name verifier --alias verifier -P $PORT -A $ADMIN_PORT --base $STORE_DIR
