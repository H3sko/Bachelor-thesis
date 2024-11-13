#!/usr/bin/env zsh
set -e
BASEDIR=$(dirname "$0")

docker-compose --file ${BASEDIR}/docker-compose.yml up -d