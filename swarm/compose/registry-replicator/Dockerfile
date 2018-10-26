FROM alpine

RUN apk --no-cache add curl jq bind-tools rsync openssh

ADD replicator.sh /

ENTRYPOINT ["/replicator.sh"]
