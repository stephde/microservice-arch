#!/usr/bin/env bash

REGISTRY_URI=localhost:5000/

docker tag spring-model-generator ${REGISTRY_URI}spring-model-generator
docker push ${REGISTRY_URI}spring-model-generator

docker tag spring-kubernetes-monitor ${REGISTRY_URI}spring-kubernetes-monitor
docker push ${REGISTRY_URI}spring-kubernetes-monitor
