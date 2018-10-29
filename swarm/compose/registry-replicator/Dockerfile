FROM alpine

RUN apk --no-cache add rsync

ADD replicator.sh /

ENTRYPOINT ["/replicator.sh"]
