FROM postgres:10.5-alpine

# localedef not available on alpine
# RUN localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8
ENV LANG en_US.utf8
ENV POSTGRES_DB basen
ENV PGDATA /var/lib/postgresql-tmp/data
COPY create-db.sql /docker-entrypoint-initdb.d/create-db.sql
CMD docker-entrypoint.sh \
      -c 'log_statement=all' \
      -c 'timezone=UTC' \
      -c 'shared_preload_libraries=pg_stat_statements' \
      -c 'pg_stat_statements.max=10000' \
      -c 'pg_stat_statements.track=all'