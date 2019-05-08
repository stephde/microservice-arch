#!/usr/bin/env bash

REGISTRY_URI=stephde/
TAG=:latest

docker tag model-maintainer ${REGISTRY_URI}model-maintainer${TAG}
docker push ${REGISTRY_URI}model-maintainer${TAG}

docker tag kubernetes-consumer ${REGISTRY_URI}kubernetes-consumer${TAG}
docker push ${REGISTRY_URI}kubernetes-consumer${TAG}

docker tag tracing-example ${REGISTRY_URI}tracing-example${TAG}
docker push ${REGISTRY_URI}tracing-example${TAG}

docker tag zipkin-consumer ${REGISTRY_URI}zipkin-consumer${TAG}
docker push ${REGISTRY_URI}zipkin-consumer${TAG}

docker tag custom-consumer ${REGISTRY_URI}custom-consumer${TAG}
docker push ${REGISTRY_URI}custom-consumer${TAG}

docker build -t gateway ./gateway
docker tag gateway ${REGISTRY_URI}gateway
docker push ${REGISTRY_URI}gateway

docker build -t dm-frontend ./frontend
docker tag dm-frontend ${REGISTRY_URI}dm-frontend${TAG}
docker push ${REGISTRY_URI}dm-frontend${TAG}