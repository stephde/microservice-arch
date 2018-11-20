# Distributed Monitoring setup

## Prerequisites
* git
* docker, docker-compose
* kubernetes cli
* kompose (docker-compose wrapper for kubernetes)

## Run Locally
* create docker images with
```
./kubernetes-monitor/gradlew dockerBuildImage
./model-generator/gradlew dockerBuildImage
```
* simply run `docker-compose up`
* verify by running `curl localhost:8002/model`

## Run in kubernetes
* start a local docker registry by `docker run -d -p 5000:5000 --restart=always --name registry registry:2`
* tag & push images to registry:
```
docker tag spring-model-generator localhost:5000/spring-model-generator
docker push localhost:5000/spring-model-generator
docker tag spring-kubernetes-monitor localhost:5000/spring-kubernetes-monitor
docker push localhost:5000/spring-kubernetes-monitor
```
* deploy to kubernetes with `kompose up`
* verify pods are running with `kubectl get pods`