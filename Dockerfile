FROM openjdk:10.0.2-jre-slim

ADD . /app

WORKDIR /app

ENTRYPOINT ["bin/basen"]