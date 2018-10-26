FROM registry:2

RUN apk --no-cache add curl jq bind-tools rsync openssh

ADD docker-entrypoint.sh /
ADD rsync.sh /

CMD ["/etc/docker/registry/config.yml"]
ENTRYPOINT ["/docker-entrypoint.sh"]
