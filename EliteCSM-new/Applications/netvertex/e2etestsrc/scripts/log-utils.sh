#!/usr/bin/env bash

timestamp() {
  date +"%T"
}

echo_time() {
    date +"%F %T.%N | $*"
}

log() {
    echo_time $1
}