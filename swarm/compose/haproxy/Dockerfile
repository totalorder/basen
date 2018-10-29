FROM haproxy:1.8.14-alpine
RUN apk add --no-cache bind-tools gettext procps
COPY docker-entrypoint-override.sh /
COPY confgen.sh /
COPY server.part /
COPY haproxy.part /
RUN cat haproxy.part | envsubst > /usr/local/etc/haproxy/haproxy.cfg
ENTRYPOINT ["/docker-entrypoint-override.sh"]
CMD ["haproxy", "-f", "/usr/local/etc/haproxy/haproxy.cfg"]