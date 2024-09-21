#!/usr/bin/env bash
# Use this script to test if a given TCP host/port are available!

set -e

TIMEOUT=15
QUIET=0
STRICT=0
HOST=""
PORT=""

usage() {
    echo "Usage: $0 host:port [-s] [-t timeout] [-- command args]"
    echo "  -h HOST | --host=HOST       Host or IP under test"
    echo "  -p PORT | --port=PORT       TCP port under test"
    echo "                              Alternatively, you specify the host and port as host:port"
    echo "  -s | --strict               Only execute subcommand if the test succeeds"
    echo "  -q | --quiet                Don't output any status messages"
    echo "  -t TIMEOUT | --timeout=TIMEOUT"
    echo "                              Timeout in seconds, zero for no timeout"
    echo "  -- COMMAND ARGS             Execute command with args after the test finishes"
    exit 1
}

echoerr() {
    if [ "$QUIET" -ne 1 ]; then echo "$@" 1>&2; fi
}

wait_for() {
    if [ "$TIMEOUT" -gt 0 ]; then
        echoerr "$0: waiting $TIMEOUT seconds for $HOST:$PORT"
    else
        echoerr "$0: waiting for $HOST:$PORT without a timeout"
    fi
    start_ts=$(date +%s)
    while :
    do
        if [ "$ISBUSY" -eq 1 ]; then
            nc -z "$HOST" "$PORT"
            result=$?
        else
            (echo > "/dev/tcp/$HOST/$PORT") >/dev/null 2>&1
            result=$?
        fi
        if [ "$result" -eq 0 ] ; then
            end_ts=$(date +%s)
            echoerr "$0: $HOST:$PORT is available after $((end_ts - start_ts)) seconds"
            break
        fi
        sleep 1
    done
    return "$result"
}

wait_for_wrapper() {
    # In order to support SIGINT during timeout: http://unix.stackexchange.com/a/57692
    if [ "$QUIET" -eq 1 ]; then
        timeout "$TIMEOUT" "$0" -q -h "$HOST" -p "$PORT" -t "$TIMEOUT" &
    else
        timeout "$TIMEOUT" "$0" -h "$HOST" -p "$PORT" -t "$TIMEOUT" &
    fi
    PID=$!
    trap 'kill -INT -$PID' INT
    wait "$PID"
    RESULT=$?
    if [ "$RESULT" -ne 0 ]; then
        echoerr "$0: timeout occurred after waiting $TIMEOUT seconds for $HOST:$PORT"
    fi
    return "$RESULT"
}

# process arguments
while [ "$#" -gt 0 ]
do
    case "$1" in
        *:* )
        HOST=$(echo "$1" | cut -d : -f 1)
        PORT=$(echo "$1" | cut -d : -f 2)
        shift 1
        ;;
        -h)
        HOST="$2"
        if [ -z "$HOST" ]; then break; fi
        shift 2
        ;;
        --host=*)
        HOST="${1#*=}"
        shift 1
        ;;
        -p)
        PORT="$2"
        if [ -z "$PORT" ]; then break; fi
        shift 2
        ;;
        --port=*)
        PORT="${1#*=}"
        shift 1
        ;;
        -t)
        TIMEOUT="$2"
        if [ -z "$TIMEOUT" ]; then break; fi
        shift 2
        ;;
        --timeout=*)
        TIMEOUT="${1#*=}"
        shift 1
        ;;
        -q | --quiet)
        QUIET=1
        shift 1
        ;;
        -s | --strict)
        STRICT=1
        shift 1
        ;;
        --)
        shift
        break
        ;;
        --help)
        usage
        ;;
        *)
        echoerr "Unknown argument: $1"
        usage
        ;;
    esac
done

if [ -z "$HOST" ] || [ -z "$PORT" ]; then
    echoerr "Error: you need to provide a host and port to test."
    usage
fi

ISBUSY=0
if command -v busybox >/dev/null; then
    ISBUSY=1
fi

if [ "$TIMEOUT" -gt 0 ]; then
    wait_for_wrapper
    RESULT=$?
else
    wait_for
    RESULT=$?
fi

if [ "$STRICT" -eq 1 ]; then
    if [ "$RESULT" -ne 0 ]; then
        echoerr "$0: strict mode, refusing to execute subprocess"
        exit "$RESULT"
    fi
fi

exec "$@"
