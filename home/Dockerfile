FROM openjdk:8-jre-alpine

MAINTAINER caidt <caidongtinghust@gmail.com>

VOLUME /tmp

RUN mkdir -p /app

COPY /build/libs/home-1.0-SNAPSHOT.jar /app/home.jar

USER root

WORKDIR /app
ENV MODE 'dev'

EXPOSE 2552
CMD ["java", "-Xms256m", "-Xmx256m", "-Dzkroot=caidt", "-jar", "home.jar"]
