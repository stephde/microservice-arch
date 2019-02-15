#!/usr/bin/env bash

REGISTRY_URI=fb14srv7:6000/

docker tag model-maintainer ${REGISTRY_URI}model-maintainer
docker push ${REGISTRY_URI}model-maintainer

docker tag kubernetes-consumer ${REGISTRY_URI}kubernetes-consumer
docker push ${REGISTRY_URI}kubernetes-consumer

docker tag tracing-example ${REGISTRY_URI}tracing-example
docker push ${REGISTRY_URI}tracing-example

docker tag zipkin-consumer ${REGISTRY_URI}zipkin-consumer
docker push ${REGISTRY_URI}zipkin-consumer
