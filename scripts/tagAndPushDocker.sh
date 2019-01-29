#!/usr/bin/env bash

REGISTRY_URI=localhost:5000/

docker tag model-generator ${REGISTRY_URI}model-generator
docker push ${REGISTRY_URI}model-generator

docker tag kubernetes-monitor ${REGISTRY_URI}kubernetes-monitor
docker push ${REGISTRY_URI}kubernetes-monitor

docker tag tracing-example ${REGISTRY_URI}tracing-example
docker push ${REGISTRY_URI}tracing-example

docker tag zipkin-consumer ${REGISTRY_URI}zipkin-consumer
docker push ${REGISTRY_URI}zipkin-consumer
