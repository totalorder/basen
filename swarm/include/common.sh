#!/usr/bin/env bash
cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}"))")/.." || exit

function get_nodes() {
    cat nodelist.txt
}

function on_node() {
    NODE="$1"
    shift
    (DOCKER_HOST=tcp://${NODE} docker "$@")
}

function random_node() {
    NODES=(`get_nodes`)
    echo ${NODES[$RANDOM % ${#NODES[@]} ]}
}

function in_swarm() {
    NODE=`random_node`
    on_node ${NODE} ${@}
}