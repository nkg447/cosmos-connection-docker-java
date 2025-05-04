#!/bin/bash

export DOMAIN="cosmos"
export PASSWORD="changeit"

# ensure docker is up
if ! docker info > /dev/null 2>&1; then
    echo "Docker is not running. Please start Docker and try again."
    exit 1
fi

DOCKER_PATH=$(pwd)/docker
cd $DOCKER_PATH || exit 1

#remove container ssl-cert-gen if it exists
if [ "$(docker ps -aq -f name=ssl-cert-gen)" ]; then
    docker rm -f ssl-cert-gen
fi

docker build -t ssl-cert-gen .
docker run -e DOMAIN=$DOMAIN -e PASSWORD=$PASSWORD --name ssl-cert-gen ssl-cert-gen
docker cp ssl-cert-gen:/opt/certs ../
docker rm -f ssl-cert-gen
