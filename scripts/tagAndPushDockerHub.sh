#!/usr/bin/env bash

REGISTRY_URI=stephde/
TAG=:v1

docker tag model-maintainer ${REGISTRY_URI}model-maintainer${TAG}
docker push ${REGISTRY_URI}model-maintainer${TAG}

docker tag kubernetes-consumer ${REGISTRY_URI}kubernetes-consumer${TAG}
docker push ${REGISTRY_URI}kubernetes-consumer${TAG}

docker tag tracing-example ${REGISTRY_URI}tracing-example${TAG}
docker push ${REGISTRY_URI}tracing-example${TAG}

docker tag zipkin-consumer ${REGISTRY_URI}zipkin-consumer${TAG}
docker push ${REGISTRY_URI}zipkin-consumer${TAG}

docker tag gateway ${REGISTRY_URI}gateway${TAG}
docker push ${REGISTRY_URI}gateway${TAG}