FROM openjdk:11-jre-slim

ADD . /app

WORKDIR /app

ENTRYPOINT ["bin/basen"]