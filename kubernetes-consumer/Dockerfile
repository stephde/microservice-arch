FROM openjdk:8-jdk-alpine

VOLUME /tmp
ARG DEPENDENCY=target/dependency
ARG JAR_FILE

# add backend
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ADD ${DEPENDENCY}/jar/${JAR_FILE} app.jar

# add kube config
ADD ${DEPENDENCY}/.kube_config /app/.kube/config
ENV KUBECONFIG /app/.kube/config

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 8080