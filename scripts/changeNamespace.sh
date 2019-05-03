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

curl --header "Content-Type: application/json" \
  --request POST \
  --data "{\"value\":\"999\"}" \
  http://localhost:1800/metrics


curl --header "Content-Type: application/json" \
  --request POST \
  --data '20' \
  http://fb14srv7.hpi.uni-potsdam.de:1800/aspect/spam/stop

curl --header "Content-Type: application/json" \
  --request POST \
  --data '20' \
  http://localhost:8080/start

curl --header "Content-Type: application/json" \
  --request POST \
  --data "http://zipkin:9411" \
  http://localhost:8005/zipkinurl


scp sdetje@fb14srv7.hpi.uni-potsdam.de:~/mRUBiS/workloadEmulator/scenarios/tmp/*.txt \
    ~/git/uni/master-thesis/experiments/property-scale/exp-1

docker run -d -p 9411:9411 \
  -e JAVA_OPTS=-Dlogging.level.zipkin=DEBUG -Dlogging.level.zipkin2=DEBUG \
  openzipkin/zipkin