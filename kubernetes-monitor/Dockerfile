FROM openjdk:latest

# install nodejs
RUN apt-get install --yes curl
RUN curl --silent --location https://deb.nodesource.com/setup_11.x | bash -
RUN apt-get install --yes nodejs
RUN apt-get install --yes build-essential
RUN npm install -g yarn

# install supervisor
RUN apt-get install -y supervisor

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

# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
ENTRYPOINT ["/usr/bin/supervisord"]
EXPOSE 8080
EXPOSE 8081