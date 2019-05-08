# Kubernetes Monitor

## Kubernetes API
see this [medium article](https://medium.com/programming-kubernetes/building-stuff-with-the-kubernetes-api-part-2-using-java-ceb8a5ff7920) 
for an example on how to use the kubernetes java client.

## client API documentation 
* https://github.com/kubernetes-client/java/tree/master/kubernetes/docs

## Configuration
* requires access rights to kubernetes API server
* authentication is configured in `.kube_config`
* the spring service automatically loads this config
* credentials can be extracted from kubectl config on host
* you might need to grant additional rights to the default user for this to work, e.g. apply `scripts/admin-role.yml` with kubectl
* a better way is to create a serviceAccount with read access and use these credentials

* default namespace to monitor is set in `application.properties` but can be changed via the API 

## Docker
Kubernetes-Monitor docker image is build based on Dockerfile.
* base image is jdk-alpine (smaller jdk image)
* build image with`./gradlew docker`
