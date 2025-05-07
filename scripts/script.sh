#!/bin/bash

export DOMAIN="cosmos"
./generate-cert.sh
export DOMAIN="localhost"
./generate-cert.sh
cp cosmos/server.truststore ./
keytool -importkeystore -srckeystore ./localhost/server.truststore -destkeystore server.truststore -srcalias localhost -destalias localhost -srcstorepass changeit -deststorepass changeit
