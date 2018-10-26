#!/usr/bin/env sh
echo "Starting rsync..."

echo "user:pass" > /etc/rsyncd.secrets
chmod 0400 /etc/rsyncd.secrets

cat <<EOF > /etc/rsyncd.conf
    pid file = /var/run/rsyncd.pid
    log file = /dev/stdout
    timeout = 300
    max connections = 10
    port = 873
    [registry]
        uid = root
        gid = root
        hosts allow = *
        read only = false
        path = /var/lib/registry/docker/registry
        comment = /var/lib/registry/docker/registry directory
        auth users = user
        secrets file = /etc/rsyncd.secrets
EOF
mkdir -p /var/lib/registry/docker/registry
echo "$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)" > /var/lib/registry/docker/registry/node-id
/usr/bin/rsync --no-detach --daemon --config /etc/rsyncd.conf & # 2>&1 > /dev/null &
