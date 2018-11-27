# Distributed Monitoring setup

## Prerequisites
* git
* docker, docker-compose
* kubectl *(kubernetes cli)*
* kompose *(docker-compose wrapper for kubernetes)*

## Run Locally
* create docker images with
```
./kubernetes-monitor/gradlew dockerBuildImage
./model-generator/gradlew dockerBuildImage
```
* simply run `docker-compose -f docker-compose.local.yml up`
* verify by running `curl localhost:8002/model`

## Run in kubernetes
* start a local docker registry with: 
```
docker run -d -p 5000:5000 --restart=always --name registry registry:2
```
* tag & push images to registry:
```
scripts/tagAndPushDockerImages.sh
```
* deploy to kubernetes with `kompose up`
* verify pods are running with `kubectl get pods`
* check out the [local dashboard](http://127.0.0.1:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy)
(you need to have kubectl proxy running `kubectl proxy` and might need to adjust the port)

run with docker stack:
```
docker stack deploy --compose-file docker-compose.yml dm
```
remove stack with docker stack:
```
docker stack rm dm
```