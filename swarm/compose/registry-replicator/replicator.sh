#!/usr/bin/env sh
echo "Replicator started..."

touch replicated-id
touch node-id

while true; do
    RSYNC_PASSWORD=pass rsync rsync://user@registry/registry/node-id node-id || echo "" > node-id
    NODE_ID="$(cat node-id)"
    REPLICATED_ID="$(cat replicated-id)"
    echo "Node id: $NODE_ID, replicated id: $REPLICATED_ID"
    if [ ! -z "$NODE_ID" ]; then
        if [ -z "$REPLICATED_ID" ]; then
            echo "Pulling from $NODE_ID..."
            RSYNC_PASSWORD=pass rsync -a rsync://user@registry/registry /replica && echo "$NODE_ID" > replicated-id
            ls /replica/v2/repositories/ && cat /replica/node-id
            echo "Pulling done"
        elif [ "$NODE_ID" != "$REPLICATED_ID" ]; then
            echo "Pushing $REPLICATED_ID to $NODE_ID..."
            ls /replica/v2/repositories/ && cat /replica/node-id
            RSYNC_PASSWORD=pass rsync -a --no-p --no-g -O /replica/ rsync://user@registry/registry
            echo "Pushing done"
        else
            echo "Re-pulling from $NODE_ID..."
            RSYNC_PASSWORD=pass rsync -a rsync://user@registry/registry /replica && echo "$NODE_ID" > replicated-id
            ls /replica/v2/repositories/ && cat /replica/node-id
            echo "Pulling done"
        fi
    fi

    sleep 5
done
