#!/usr/bin/env bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"message":"xyz"}' \
  http://localhost:8002/message