FROM frolvlad/alpine-glibc:alpine-3.8

RUN apk add --no-cache libstdc++

ADD . /app

WORKDIR /app

ENTRYPOINT ["bin/basen"]