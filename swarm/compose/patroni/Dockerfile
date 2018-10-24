FROM postgres:11.0
MAINTAINER Anton Blomberg <anton@deadlock.se>

RUN export DEBIAN_FRONTEND=noninteractive \
    && echo 'APT::Install-Recommends "0";\nAPT::Install-Suggests "0";' > /etc/apt/apt.conf.d/01norecommend \
    && apt-get update -y \
    && apt-get upgrade -y

RUN apt-get install -y patroni python3-etcd

RUN localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
    && mkdir -p /home/postgres \
    && chown postgres:postgres /home/postgres

RUN apt-get purge -y libpython2.7-stdlib libpython2.7-minimal \
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/* /root/.cache

RUN mkdir /data/ \
    && touch /pgpass /patroni.yml && chown postgres:postgres -R /data/ /pgpass /patroni.yml /var/run/ /var/lib/ /var/log/

ENV LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8
EXPOSE 2379 5432 8008

ADD docker-entrypoint.sh /patroni-docker-entrypoint.sh

USER postgres
ENTRYPOINT ["/bin/bash", "/patroni-docker-entrypoint.sh"]
