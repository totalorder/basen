#!/usr/bin/env sh
/rsync.sh &
/entrypoint.sh $@ 2>&1 > /dev/null
