#!/usr/bin/env bash

# Add bot to GUID if given
if [ ! -z "$GUID" ]; then
    usermod -u $GUID bot
fi

# Run arg or supervisord
if [ $# -gt 0 ];then
    exec gosu $UUID "$@"
else
    /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
fi
