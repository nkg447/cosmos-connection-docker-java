#!/bin/bash

CERT_PATH_HOME=/opt/certs
KEY_STORE=/opt/certs/server.keystore.jks

echo -e "CERT_PATH_HOME=$CERT_PATH_HOME\n\
KEY_STORE=$KEY_STORE\n\
DOMAIN=$DOMAIN\n\
PASSWORD=$PASSWORD"

keytool -keystore server.keystore.jks -alias $DOMAIN -validity 365 -genkey -keyalg RSA -dname "CN=$DOMAIN, OU=orgunit, O=Organisation, L=bangalore, S=Karnataka, C=IN" -ext SAN=DNS:$DOMAIN -keypass $PASSWORD -storepass $PASSWORD && \
openssl req -new -x509 -keyout ca-key -out ca-cert -days 365 -passout pass:"$PASSWORD" -subj "/CN=$DOMAIN" && \
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert -storepass $PASSWORD -noprompt && \
keytool -keystore server.keystore.jks -alias $DOMAIN -certreq -file cert-file -storepass $PASSWORD && \
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial -passin pass:$PASSWORD && \
keytool -keystore server.keystore.jks -alias $DOMAIN -import -file cert-signed -storepass $PASSWORD
openssl pkcs12 -export -out $DOMAIN.pfx -inkey ca-key -in ca-cert -passin pass:$PASSWORD -passout pass:$PASSWORD
keytool -import -alias $DOMAIN -file ca-cert -storetype JKS -keystore server.truststore -storepass $PASSWORD -noprompt -trustcacerts < trustprompt
echo "generated keystore file is ${KEY_STORE}"