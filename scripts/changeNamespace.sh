#!/usr/bin/env bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data 'true' \
  http://fb14srv7.hpi.uni-potsdam.de:1800/kube-consumer/api/watchers/NAMESPACE_WATCHER