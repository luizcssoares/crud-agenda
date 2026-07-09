FROM openjdk:17-alpine
COPY target/agenda-crud-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","app.jar"]

FROM jenkins/jenkins:lts-jdk21
USER root
RUN apt-get update && \
    apt-get install -y \
    docker.io
USER jenkins