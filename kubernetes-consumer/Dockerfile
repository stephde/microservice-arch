FROM openjdk:8-jdk-alpine

# install dependencies
RUN apk add nodejs nodejs-npm supervisor
RUN npm install -g yarn

VOLUME /tmp
ARG DEPENDENCY=target/dependency
ARG JAR_FILE

# add backend
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ADD ${DEPENDENCY}/jar/${JAR_FILE} app.jar

# add frontend and install dependencies
COPY ${DEPENDENCY}/frontend /app/frontend
RUN cd /app/frontend && yarn

# add supervisor config to run multiple services
ADD ${DEPENDENCY}/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

ENTRYPOINT ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
EXPOSE 8080
EXPOSE 8081