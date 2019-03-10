# Kubernetes Monitor

## Kubernetes API
see this [medium article](https://medium.com/programming-kubernetes/building-stuff-with-the-kubernetes-api-part-2-using-java-ceb8a5ff7920) 
for an example on how to use the kubernetes java client.

## client API documentation 
* https://github.com/kubernetes-client/java/tree/master/kubernetes/docs

## Docker
Kubernetes-Monitor docker image is build based on Dockerfile.
* base image is jdk-alpine (smaller jdk image)
* nodejs is added to run the frontend
* supervisor added to run backend and frontend detached in parallel

can be build with
```
./gradlew docker
```

## find out env
* via deploymentSpec > PodTemplateSpec > PodSpec > Container > CMD