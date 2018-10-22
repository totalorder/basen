#!/usr/bin/env bash
echo "confgen started with dns name: ${DNS_NAME}"
while :; do
    SERVERS=""
    for IP in $(dig +short ${DNS_NAME} | sort); do
        SERVER="$(cat server.part | NAME="${IP//./_}" IP=$IP envsubst)"
        SERVERS=$(echo -e "$SERVERS\n$SERVER")
    done

    cat haproxy.part | MASTERS=$SERVERS SLAVES=$SERVERS envsubst > haproxy.cfg

    cmp /usr/local/etc/haproxy/haproxy.cfg haproxy.cfg
    if [[ $? != 0 ]]; then
        echo "Loading new config: "
        cat haproxy.cfg

        cp haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg

        echo -e "\nReloading haproxy..."
        kill -s HUP $(pgrep haproxy)
    fi

    sleep 5
done