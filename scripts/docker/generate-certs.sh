#!/bin/bash

CERT_PATH_HOME=/opt/certs/$DOMAIN
KEY_STORE=$CERT_PATH_HOME/server.keystore.jks

echo -e "CERT_PATH_HOME=$CERT_PATH_HOME\n\
KEY_STORE=$KEY_STORE\n\
DOMAIN=$DOMAIN\n\
PASSWORD=$PASSWORD"

mkdir $CERT_PATH_HOME
cd $CERT_PATH_HOME
# Temporary file for OpenSSL configuration with SAN
OPENSSL_CONF_SAN=$(mktemp)

# Create the OpenSSL configuration file with SANs
cat > $OPENSSL_CONF_SAN <<EOF
[req]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no

[req_distinguished_name]
C = IN
ST = KA
CN = $DOMAIN

[v3_req]
keyUsage = digitalSignature,dataEncipherment,keyEncipherment,keyAgreement
extendedKeyUsage = serverAuth,clientAuth
subjectAltName = @alt_names

[alt_names]
DNS.1 = $DOMAIN
IP.1 = 127.0.0.1

[v3_ca]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = CA:true
EOF

keytool -keystore server.keystore.jks -alias $DOMAIN -validity 365 -genkey -keyalg RSA -dname "C=IN, ST=KA, CN=$DOMAIN" -keypass $PASSWORD -storepass $PASSWORD && \
openssl req -new -x509 -keyout ca-key -out ca-cert -days 365 -passout pass:"$PASSWORD" -subj "/C=IN/ST=KA/CN=$DOMAIN" -extensions v3_ca -config $OPENSSL_CONF_SAN && \
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert -storepass $PASSWORD -noprompt && \
keytool -keystore server.keystore.jks -alias $DOMAIN -certreq -file cert-file -storepass $PASSWORD && \
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial -passin pass:$PASSWORD -extensions v3_req -extfile $OPENSSL_CONF_SAN && \
keytool -keystore server.keystore.jks -alias $DOMAIN -import -file cert-signed -storepass $PASSWORD
openssl pkcs12 -export -out $DOMAIN.pfx -inkey ca-key -in ca-cert -passin pass:$PASSWORD -passout pass:$PASSWORD
keytool -import -alias $DOMAIN -file ca-cert -storetype JKS -keystore server.truststore -storepass $PASSWORD -noprompt -trustcacerts < ../trustprompt

# Clean up temporary OpenSSL config file
rm -f $OPENSSL_CONF_SAN

echo "generated keystore file is ${KEY_STORE}"
