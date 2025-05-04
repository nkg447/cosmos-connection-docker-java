#!/bin/bash

CERT_PATH_HOME=/opt/certs
KEY_STORE=/opt/certs/server.keystore.jks

echo -e "CERT_PATH_HOME=$CERT_PATH_HOME\n\
KEY_STORE=$KEY_STORE\n\
DOMAIN=$DOMAIN\n\
PASSWORD=$PASSWORD"

# Temporary file for OpenSSL configuration with SAN
OPENSSL_CONF_SAN=$(mktemp)

# Create the OpenSSL configuration file with SANs
cat > $OPENSSL_CONF_SAN <<EOF
[req]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no

[req_distinguished_name]
C = US
ST = WA
O = SqlPostgresHostConsole
CN = $DOMAIN

[v3_req]
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
DNS.1 = $DOMAIN
IP.1 = 127.0.0.1
EOF

keytool -keystore server.keystore.jks -alias $DOMAIN -validity 365 -genkey -keyalg RSA -dname "C=US, ST=WA, O=SqlPostgresHostConsole, CN=$DOMAIN" -keypass $PASSWORD -storepass $PASSWORD && \
openssl req -new -x509 -keyout ca-key -out ca-cert -days 365 -passout pass:"$PASSWORD" -subj "/C=US/ST=WA/O=SqlPostgresHostConsole/CN=$DOMAIN" -config $OPENSSL_CONF_SAN && \
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert -storepass $PASSWORD -noprompt && \
keytool -keystore server.keystore.jks -alias $DOMAIN -certreq -file cert-file -storepass $PASSWORD && \
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial -passin pass:$PASSWORD -extensions v3_req -extfile $OPENSSL_CONF_SAN && \
keytool -keystore server.keystore.jks -alias $DOMAIN -import -file cert-signed -storepass $PASSWORD
openssl pkcs12 -export -out $DOMAIN.pfx -inkey ca-key -in ca-cert -passin pass:$PASSWORD -passout pass:$PASSWORD
keytool -import -alias $DOMAIN -file ca-cert -storetype JKS -keystore server.truststore -storepass $PASSWORD -noprompt -trustcacerts < trustprompt

# Clean up temporary OpenSSL config file
rm -f $OPENSSL_CONF_SAN

echo "generated keystore file is ${KEY_STORE}"
