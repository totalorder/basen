FROM registry:2

RUN apk --no-cache add rsync

ADD docker-entrypoint.sh /
ADD rsync.sh /

CMD ["/etc/docker/registry/config.yml"]
ENTRYPOINT ["/docker-entrypoint.sh"]
