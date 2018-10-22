FROM haproxy:1.8.14
RUN apt update
RUN apt install -y dnsutils
RUN apt install -y gettext
RUN apt install -y procps
COPY docker-entrypoint-override.sh /
COPY confgen.sh /
COPY server.part /
COPY haproxy.part /
RUN cat haproxy.part | envsubst > /usr/local/etc/haproxy/haproxy.cfg
ENTRYPOINT ["/docker-entrypoint-override.sh"]
CMD ["haproxy", "-f", "/usr/local/etc/haproxy/haproxy.cfg"]