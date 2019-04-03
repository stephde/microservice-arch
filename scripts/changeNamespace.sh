#!/usr/bin/env bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data 'true' \
  http://fb14srv7.hpi.uni-potsdam.de:1800/kube-consumer/api/watchers/NAMESPACE_WATCHER

curl --header "Content-Type: application/json" \
  --request POST \
  --data 'false' \
  http://localhost:8080/services/authenticationservice

curl --header "Content-Type: application/json" \
  --request POST \
  --data "{\"nickname\":\"LiCraft990\",\"password\":\"hfawpftjqevznx\"}" \
  http://localhost:8110/authenticationservice/authenticate


curl --header "Content-Type: application/json" \
  --request POST \
  --data "{\"id\":\"999\"}" \
  http://localhost:8112/inventoryItemService/checkAvailabilityOfItem