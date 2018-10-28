#!/usr/bin/env bash

function on_node() {
    NODE="$1"
    shift
    (DOCKER_HOST=tcp://${NODE} docker "$@")
}

function in_swarm() {
    NODE=$(./get_node)
    on_node ${NODE} "$@"
}

function tinytools() {
    NODE=$1
    shift
    on_node ${NODE} exec -it $(on_node ${NODE} ps -q --filter name=tinytools) "$@"
}

function get_port() {
    PORTS=$(docker inspect $(docker-compose -f compose/swarmhost.yml -f compose/swarmhost-node.yml ps -q | head -n 1) | jq '.[].NetworkSettings.Ports')
    echo "$PORTS" | jq -r 2> /dev/null ".[\"$1/tcp\"][].HostPort"
}