#!/usr/bin/env bash

REGISTRY_URI=localhost:5000/

docker tag model-generator ${REGISTRY_URI}model-generator
docker push ${REGISTRY_URI}model-generator

docker tag kubernetesmonitor ${REGISTRY_URI}kubernetesmonitor
docker push ${REGISTRY_URI}kubernetesmonitor
