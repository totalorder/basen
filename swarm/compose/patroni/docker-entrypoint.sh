#!/bin/bash

DOCKER_IP=$(hostname --ip-address)
PATRONI_SCOPE=${PATRONI_SCOPE:-batman}

export PATRONI_SCOPE
export PATRONI_NAME="${PATRONI_NAME:-${HOSTNAME}}"
export PATRONI_RESTAPI_CONNECT_ADDRESS="${DOCKER_IP}:8008"
export PATRONI_RESTAPI_LISTEN="0.0.0.0:8008"
export PATRONI_admin_PASSWORD="${PATRONI_admin_PASSWORD:=admin}"
export PATRONI_admin_OPTIONS="${PATRONI_admin_OPTIONS:-createdb, createrole}"
export PATRONI_POSTGRESQL_CONNECT_ADDRESS="${DOCKER_IP}:5432"
export PATRONI_POSTGRESQL_LISTEN="0.0.0.0:5432"
export PATRONI_POSTGRESQL_DATA_DIR="data/${PATRONI_SCOPE}"
export PATRONI_REPLICATION_USERNAME="${PATRONI_REPLICATION_USERNAME:-replicator}"
export PATRONI_REPLICATION_PASSWORD="${PATRONI_REPLICATION_PASSWORD:-abcd}"
export PATRONI_SUPERUSER_USERNAME="${PATRONI_SUPERUSER_USERNAME:-postgres}"
export PATRONI_SUPERUSER_PASSWORD="${PATRONI_SUPERUSER_PASSWORD:-postgres}"
export PATRONI_POSTGRESQL_PGPASS="$HOME/.pgpass"

cat > /patroni.yml <<__EOF__
bootstrap:
  dcs:
    postgresql:
      use_pg_rewind: true

  pg_hba:
  - host all all 0.0.0.0/0 md5
  - host replication replicator ${DOCKER_IP}/16    md5
__EOF__

exec patroni /patroni.yml
