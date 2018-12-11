#!/usr/bin/env bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data 'newnamespace' \
  http://localhost:8080/namespace