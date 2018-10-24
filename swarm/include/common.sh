#!/usr/bin/env bash

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
    on_node ${NODE} "$@"
}

function tinytools() {
    NODE=`random_node`
    on_node ${NODE} exec -it $(on_node ${NODE} ps -q --filter name=tinytools) "$@"
}